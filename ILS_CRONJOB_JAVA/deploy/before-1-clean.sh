oc delete CronJob itsm-cronjob -n NAMESPACE
oc delete PersistentVolumeClaim itsm-cronjob -n NAMESPACE
oc delete configmap config-endpoints-reporter -n NAMESPACE
