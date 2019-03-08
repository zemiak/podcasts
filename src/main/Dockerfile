FROM debian:jessie

RUN apt update && apt -y install php5 sendemail python-six apache2 curl && \
    mkdir -p /mnt/media/inbox && \
    ln -s /mnt/media/inbox/ /var/www/html/podcasts

COPY ./etc/* /etc/
COPY ./bin/* /usr/local/bin/
COPY web /opt/web

RUN chmod +x /usr/local/bin/*

VOLUME /mnt/media/inbox
EXPOSE 80

CMD apachectl start && devcron.py /etc/crontab
