#!/usr/bin/env bash

docker run \
-e MYSQL_ROOT_PASSWORD=root \
-e MYSQL_USER=kotlin \
-e MYSQL_PASSWORD=kotlin_cn \
-e MYSQL_DATABASE=kotlin_cn \
--name mysql \
-p 3306:3306 \
-dt \
daocloud.io/library/mysql

docker run \
--name redis \
-p 10001:6379 \
-d \
daocloud.io/library/redis \
redis-server --appendonly yes

./gradlew forum:jar

pid=$(ps -ef | egrep "forum-" | grep -v grep | awk '{print $2}')
if [ ${pid} ]; then
    kill ${pid}
fi

java -jar ./forum/build/libs/forum-1.0.0-release.jar