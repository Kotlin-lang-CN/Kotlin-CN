FROM ubuntu:latest

RUN apt-get update
RUN apt-get install -y nginx
RUN apt-get install -y redis-server

RUN export DEBIAN_FRONTEND="noninteractive" && \
    echo "mysql-server mysql-server/root_password password root" | debconf-set-selections && \
    echo "mysql-server mysql-server/root_password_again password root" | debconf-set-selections && \
    apt-get install -y mysql-server

WORKDIR /root

RUN apt-get install -y wget
RUN wget http://chpengzh.com/raw/jdk-8u144-linux-x64.tar.gz && \
    tar zxf jdk-8u144-linux-x64.tar.gz && \
    rm -rf jdk-8u144-linux-x64.tar.gz

RUN apt-get install -y ssh
RUN apt-get ssh clone

RUN export COMPILE_VERSION=10
COPY ./forum/build/libs/ /root
COPY ./docker/ /root

ENTRYPOINT /bin/bash /root/entrypoint.sh

