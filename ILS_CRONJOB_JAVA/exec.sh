#!/bin/sh -xv
curDate=`date +"%y%m%d-%H%M%S"`
curDir=
logsDir=/data/logs
mkdir -p /data/logs

logFile=$logsDir/$curDate-cron_workloads.log

echo $* > $logFile
echo >> $logFile
echo Script executed by `whoami` >> $logFile
echo >> $logFile
date >> $logFile
echo >> $logFile
cd /data
/bin/bash -xv /home/ubuntu/entrypoint.sh  2>&1 | tee -a $logFile
gzip workloads/*.json
echo >> $logFile
date >> $logFile
cat $logFile
gzip $logFile
