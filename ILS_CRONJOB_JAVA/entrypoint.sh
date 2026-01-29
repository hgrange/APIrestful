#!/bin/sh
# Fail on any error
set -e
cur=$PWD
cd /data
if [ ! -d workloads ]; then
   mkdir workloads
fi

# JVM options (disable hostname verification, truststore config)
JAVA_OPTS="-Djdk.internal.httpclient.disableHostnameVerification=true -cp /home/ubuntu/lib/gson-2.13.2.jar:/home/ubuntu/lib/apiILS.jar"
#JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStoreType=PKCS12"
#JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStore=/app/truststore.pkcs12"
#JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStorePassword=password"

# Run the fat JAR (preferred)
exec java $JAVA_OPTS com.herve.CronWLs $REPORTERAPI $CMDBAPI $ITSMAPI
