FROM registry.access.redhat.com/ubi9/openjdk-21:latest as builder
ARG APP
WORKDIR /build/$APP

#ENV http_proxy=http://172.17.0.1:3128
#RUN cd /build/$APP && mvn dependency:go-offline -B

COPY --chown=1000:1000 . /build/$APP
#RUN echo $APP && cd /build/$APP && pwd && ls && mvn -o package -Dmaven.repo.local=/build/$APP/m2/repository
RUN echo $APP && cd /build/$APP/ && pwd && ls -la && mvn package 

FROM icr.io/appcafe/websphere-liberty:25.0.0.3-kernel-java21-openj9-ubi-minimal
ARG APP
ARG TLS=true
USER 0
#RUN dnf install -y procps-ng && dnf clean all
RUN microdnf update -y && microdnf install -y tar gzip jq  procps util-linux vim-minimal iputils net-tools
USER 1001

COPY --from=builder --chown=1001:0  /build/$APP/target/*.*ar /config/apps/
COPY --from=builder --chown=1001:0  /build/$APP/src/main/liberty/config/ /config/
COPY --from=builder --chown=1001:0  /build/$APP/wlp-base-license.jar /tmp
COPY --from=builder --chown=1001:0 /home/default/.m2/repository/org/apache/derby/derby/10.15.1.3/derby-10.15.1.3.jar /home/default/.m2/repository/org/apache/derby/derbyshared/10.15.1.3/derbyshared-10.15.1.3.jar /home/default/.m2/repository/org/apache/derby/derbytools/10.15.1.3/derbytools-10.15.1.3.jar /opt/ibm/wlp/usr/shared/resources

# This script will add the requested XML snippets to enable Liberty features and grow image to be fit-for-purpose using featureUtility.
# Only available in 'kernel-slim'. The 'full' tag already includes all features for convenience.
ENV VERBOSE=true

RUN bash -xv features.sh
# Add interim fixes (optional)
# COPY --chown=1001:0  interim-fixes /opt/ibm/wlp/fixes/

# This script will add the requested server configurations, apply any interim fixes and populate caches to optimize runtime
RUN bash -xv configure.sh 
# Upgrade to production license 
RUN java -jar /tmp/wlp-base-license.jar --acceptLicense /opt/ibm/wlp && rm /tmp/wlp-base-license.jar
