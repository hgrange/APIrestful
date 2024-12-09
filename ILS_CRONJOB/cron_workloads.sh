post_incident() {
curdate=$(date +"%Y/%m/%d %H:%M:%S")
cat << EOF | jq -c
{"checked":"false",
"id":$(date +%s),
"title":"$project $ownerEmail No assertion",
"description":"$description",
"project":"$project",
"ownerEmail":"$ownerEmail",
"openingDate":"$curdate",
"closedDate":" ",
"status":"Open"}
EOF
}

insert_data()
{
  curl -s -k -X 'PUT'  -H 'Content-Type: application/json' https://$apihost/workloads/$1/custom_columns/$2?token=$token -d "{\"value\":\"$3\"}"
   
}

retrieve_data()
{
  curl -s -k -H 'Content-Type: application/json' https://$apihost/workloads/$1/custom_columns/$2?token=$token | jq -r '.value'
   
}

NAMESPACE_REPORTER=$(oc get secret  -A | grep ibm-license-service-reporter-cert | awk '{ print $1 }' | uniq)
token=$(oc get  secret ibm-license-service-reporter-token -n $NAMESPACE_REPORTER -o jsonpath='{.data.token}' | base64 -d)
apihost=$(oc get route ibm-license-service-reporter  -n $NAMESPACE_REPORTER -o jsonpath='{.spec.host }')
cmdbhost=$(oc get route -n reporter itsm -o jsonpath='{.spec.host}')

curl -s -k -X "GET"  -H 'Content-Type: application/json'  https://$apihost/workloads?token=$token  > /tmp/workloads.json
curl -s -k -X "GET"  -H 'Content-Type: application/json'  https://$apihost/custom_columns?token=$token > /tmp/custom_columns.json
curl -s -k -X "GET"  -H 'Content-Type: application/json'  https://$cmdbhost/v2/cmdbs > /tmp/cmdbs.json
#curl -s -k -X "GET"  -H 'Content-Type: application/json'  http://192.168.14.25:9080/v2/cmdbs > /tmp/cmdbs.json

#"id":122,"kind":"Deployment","name":"es1-ibm-es-metrics","namespace":"cp4i","clusterId":"ClusterTest1","clusterName":"Cluster1","chargebackGroupName":"","replicas":1,"containers":[{"name":"metrics","image":"cp.icr.io/cp/ibm-eventstreams-metrics-collector@sha256:fb8f5b6d49c629007b930ee280f2f6055d8cb3d3e52d95cebd1ec3eca73dd0e9","charged":false,"components":null}],"annotations":"IBM_Event_Streams_for_Non_Production/VIRTUAL_PROCESSOR_CORE/IBM_Cloud_Pak_for_Integration/VIRTUAL_PROCESSOR_CORE/2:1","customColumns":[]},{

#{"cluster":"cluster1","namespace":"namespace1","ownerEmail":"herve@ibm.com","project":"app1","sid":"cluster1_app1"}

#[{"id":1,"name":"SAM Status","type":"text"},{"id":2,"name":"Project","type":"text"},{"id":3,"name":"Project Owner","type":"text"},{"id":4,"name":"ServiceNow status","type":"text"},{"id":5,"name":"ServiceNow ticket","type":"text"},{"id":6,"name":"Comment","type":"text"},{"id":7,"name":"Remediation needed","type":"text"}]

list_id=$(cat /tmp/workloads.json | jq '.[].id' | sort -n)
echo $list_id

idcol_ownerEmail=$( cat /tmp/custom_columns.json | sed 's/{"id":/\n{"id":/g' | grep \"name\":\"Project\ Owner\" | tr ',' '\12' | grep {\"id\" | awk -F: '{ print $2 }' )
idcol_project=$( cat /tmp/custom_columns.json | sed 's/{"id":/\n{"id":/g' | grep \"name\":\"Project\" | tr ',' '\12' | grep {\"id\" | awk -F: '{ print $2 }' )
idcol_ticket=$( cat /tmp/custom_columns.json | sed 's/{"id":/\n{"id":/g' | grep \"name\":\"ServiceNow\ ticket\" | tr ',' '\12' | grep {\"id\" | awk -F: '{ print $2 }' )
idcol_comment=$( cat /tmp/custom_columns.json | sed 's/{"id":/\n{"id":/g' | grep \"name\":\"Comment\"  | tr ',' '\12' | grep {\"id\" |  awk -F: '{ print $2 }')
idcol_status=$( cat /tmp/custom_columns.json | sed 's/{"id":/\n{"id":/g' | grep \"name\":\"ServiceNow\ status\"  | tr ',' '\12' | grep {\"id\" |  awk -F: '{ print $2 }')

#for recId in $list_id
for recId in 1                
do
    rec=$(cat /tmp/workloads.json | jq ".[] | select (.id==$recId)" | jq -c )
    namespace=$( echo $rec | tr ',' '\12' | grep namespace | awk -F\" '{ print $4 }' )
    cluster=$( echo $rec | tr ',' '\12' | grep clusterName | awk -F\" '{ print $4 }' )
    charged=$( echo $rec | tr ',' '\12' | grep charged | awk -F: '{ print $2 }' )
    container=$( echo $rec | tr ',' '\12' | grep containers | awk -F\" '{ print $(NF-1) }' )
    image=$( echo $rec | tr ',' '\12' | grep image | awk -F\" '{ print $(NF-1) }')
    components=$( echo $rec |  jq '[.containers[].components[].name,.containers[].components[].version]' | jq -c | sed 's/\[//g' | sed 's/\]//g' | sed 's/"//g' )

    recCmdb=$(cat /tmp/cmdbs.json | sed 's/{"c/\n{"c/g' | grep -i \"namespace\":\"$namespace\" | grep -i \"cluster\":\"$cluster\")
    ownerEmail=$(echo $recCmdb | tr ',' '\12' | grep ownerEmail | awk -F\" '{ print $4 }' )
    echo $ownerEmail
    project=$(echo $recCmdb | tr ',' '\12' | grep project | awk -F\" '{ print $4 }' )
    echo $project
    tid=$(retrieve_data  $recId $idcol_ticket)
    if [ $( echo $charged | grep true | wc -l ) -eq 0 ]; then
       #insert_data $recId $idcol_ticket "_"
       #read
       if [ $( echo $tid | wc -c) -lt 10 ]; then 

          description="No assertion for Container $container, with components $components"
          #curl -v -X 'POST' https://$cmdbhost/v2/incident -H 'accept: application/json' -H 'Content-Type: application/json' \
	  tid=$(curl  -X 'POST' http://192.168.14.25:9080/v2/incident -H 'accept: application/json' -H 'Content-Type: application/json' \
              -d "$(post_incident)"  )
          echo $tid
          insert_data $recId $idcol_project $project
          insert_data $recId $idcol_comment $description
          insert_data $recId $idcol_ownerEmail $ownerEmail
          insert_data $recId $idcol_ticket $tid
       fi
   else
       if [ $(echo $rec | jq -r ".customColumns[] | select (.name==\"Project\")|.value" | wc -c) -eq 0 ]; then
         insert_data $recId $idcol_project $project
       fi
       if [ $(echo $rec | jq -r ".customColumns[] | select (.name==\"Project Owner\")|.value" | wc -c) -eq 0 ]; then
         insert_data $recId $idcol_ownerEmail $ownerEmail
       fi
   fi 

   if [ $( echo $tid | wc -c) -ge 10 ]; then 
       echo Retrieve Status !!!!!
       status=$(curl -k -v http://192.168.14.25:9080/v2/incident/$tid | jq -r '.status')
       if [ $(echo $status | wc -c ) -le 1 ]; then
         insert_data $recId $idcol_ticket "0"
         insert_data $recId $idcol_status "_"
       else 
         echo status: $status
         insert_data $recId $idcol_status $status
       fi
   fi
done   

    
    






    


