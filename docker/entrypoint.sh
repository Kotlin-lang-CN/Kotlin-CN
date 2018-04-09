#!/bin/bash

export JAVA_HOME=/root/jdk1.8.0_144
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$PATH:$JAVA_HOME/bin

/root/jdk1.8.0_144/bin/java -jar /root/kotliner-1.0.0-release.jar \
    --spring.datasource.url=${MYSQL_URL} \
    --spring.datasource.username=${MYSQL_USER} \
    --spring.datasource.password=${MYSQL_PW} \
    --spring.redis.cluster.nodes=${REDIS_HOSTS}