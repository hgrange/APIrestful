FROM docker.io/maven:3.9.11-ibm-semeru-25-noble AS builder
ARG BUILD_DIR=/home/ubuntu
WORKDIR $BUILD_DIR

USER 1000
#ENV http_proxy=http://172.17.0.1:3128
#RUN cd /build/$APP && mvn dependency:go-offline -B

COPY --chown=1000:0 . $BUILD_DIR
RUN pwd && ls
RUN mvn  clean package -DskipTests

FROM docker.io/ibm-semeru-runtimes:open-25-jre-noble
ARG BUILD_DIR=/home/ubuntu
USER 1000
RUN chmod 755 /home/ubuntu

COPY --from=builder --chown=1000:0  $BUILD_DIR/target/*.*ar $BUILD_DIR/lib/
COPY --from=builder --chown=1000:0 $BUILD_DIR/target/lib/*.*ar $BUILD_DIR/lib/
COPY --from=builder --chown=1000:0 $BUILD_DIR/entrypoint.sh $BUILD_DIR/
COPY --from=builder --chown=1000:0 $BUILD_DIR/exec.sh $BUILD_DIR/
