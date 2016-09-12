FROM zemiak/wildfly
MAINTAINER zemiak@gmail.com

RUN mkdir -p /mnt/media/inbox

USER wildfly
COPY podcasts.war /opt/wildfly/standalone/deployments/

USER root
VOLUME ["/mnt/media/inbox"]
EXPOSE 8080 22
