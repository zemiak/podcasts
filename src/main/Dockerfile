FROM centos:7

RUN yum -y install epel-release && \
    yum -y install php sendemail python-six httpd && \
    mkdir -p /mnt/media/inbox && \
    ln -s /mnt/media/inbox/ /var/www/html/podcasts

COPY ./etc/* /etc/
COPY ./bin/* /usr/local/bin/
COPY web /opt/web

RUN chmod +x /usr/local/bin/*

VOLUME /mnt/media/inbox
EXPOSE 80

CMD httpd && devcron.py /etc/crontab
