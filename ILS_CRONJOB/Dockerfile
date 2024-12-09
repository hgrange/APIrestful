FROM quay.io/openshift/origin-cli:latest
RUN yum install -y net-tools iproute wget; yum clean all
COPY ./cron_workload.sh /app/requirements.txt
WORKDIR /app
RUN python3 -m pip install -r requirements.txt
COPY ./src /app/src
EXPOSE 7878
ENTRYPOINT ["python3"]
CMD ["src/main.py"]
