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
