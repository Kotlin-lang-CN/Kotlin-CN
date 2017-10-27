#!/bin/bash

export JAVA_HOME=/root/jdk1.8.0_144
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$PATH:$JAVA_HOME/bin

service mysql start
mysql -u root -proot -e "CREATE DATABASE kotlin_cn"
service nginx start
redis-server --port 7000 --daemonize yes

/root/jdk1.8.0_144/bin/java -jar /root/kotliner-1.0.0-release.jar