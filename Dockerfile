FROM ubuntu:latest

ADD ./docker/sources.list /etc/apt/
RUN apt-get update && apt-get install -y wget

WORKDIR /root

RUN wget http://chpengzh.com/raw/jdk-8u144-linux-x64.tar.gz \
    && tar zxf jdk-8u144-linux-x64.tar.gz \
    && rm -rf jdk-8u144-linux-x64.tar.gz

COPY ./forum/build/libs/ /root
COPY ./docker/ /root

ENV MYSQL_URL jdbc:mysql://127.0.0.1:3306/kotlin_cn?useUnicode=true&characterEncoding=utf8
ENV MYSQL_USER root
ENV MYSQL_PW root
ENV REDIS_HOSTS 127.0.0.1:7000,127.0.0.1:7001,127.0.0.1:7002,127.0.0.1:7003,127.0.0.1:7004,127.0.0.1:7005

ENTRYPOINT /bin/bash /root/entrypoint.sh
