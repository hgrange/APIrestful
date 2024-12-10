FROM quay.io/openshift/origin-cli:latest
RUN yum install -y net-tools iproute wget; yum clean all
COPY ./cron_workload.sh /app/cron_workload.sh
WORKDIR /app
RUN chmod 755 /app/cron_workload.sh
ENTRYPOINT ["/app/cron_workload.sh"]

