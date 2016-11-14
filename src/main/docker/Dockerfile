FROM zemiak/payara
MAINTAINER zemiak@gmail.com

RUN asadmin start-domain ${DOMAIN_NAME} && \
    echo "AS_ADMIN_PASSWORD=${ADMIN_PASSWORD}" > payarapwd && \
    asadmin --passwordfile=payarapwd create-javamail-resource --mailuser none --fromaddress noreply@nowhere.com --mailhost mail mail/podcasts && \
    asadmin --passwordfile=payarapwd stop-domain ${DOMAIN_NAME} && \
    mkdir -p /mnt/media

VOLUME ["/mnt/media"]

COPY podcasts.war $DEPLOYMENT_DIR
COPY start.sh /usr/local/bin/

CMD sh /usr/local/bin/start.sh
