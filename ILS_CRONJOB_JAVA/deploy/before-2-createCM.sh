CMDBAPI=$(oc get route -n NAMESPACE itsm -o jsonpath='{.spec.host}')
ITSMAPI=$(oc get route -n NAMESPACE itsm -o jsonpath='{.spec.host}')
REPORTERAPI=$(oc get route -n NAMESPACE ibm-license-service-reporter -o jsonpath='{.spec.host}')


oc create configmap config-endpoints-reporter -n NAMESPACE \
  --from-literal=REPORTERAPI=$REPORTERAPI \
  --from-literal=CMDBAPI=$CMDBAPI \
  --from-literal=ITSMAPI=$ITSMAPI \
  --from-literal=DEBUG=true \
  --from-literal=INSECURE=true \
  --from-literal=BOUCHON=false
