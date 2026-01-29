oc delete webspherelibertyapplication itsm -n NAMESPACE
oc delete openlibertyapplication -n NAMESPACE
oc create secret itsm-oidc --from-literal=oidc.properties="issuerUri=$(
# oc delete pvc itsm-db -n NAMESPACE
