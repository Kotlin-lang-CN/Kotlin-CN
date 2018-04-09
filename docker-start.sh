#!/bin/bash

docker run -dt \
    -p 8080:8080 \
    --name kotliner \
    -e MYSQL_URL="jdbc:mysql://192.168.1.17:3306/kotlin_cn?useUnicode=true&characterEncoding=utf8" \
    -e MYSQL_USER="root" \
    -e MYSQL_PW="root" \
    -e REDIS_HOSTS="192.168.1.17:7000,192.168.1.17:7001,192.168.1.17:7002,192.168.1.17:7003,192.168.1.17:7004,192.168.1.17:7005" \
    kotliner
