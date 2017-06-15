#!/usr/bin/env bash
cp project.properties account/src/main/resources && cp project.properties article/src/main/resources

echo "++ recompile projects ++"
./gradlew account:jar && ./gradlew article:jar

echo '++ service stop ++'
pkill -f *article* && pkill -f *account*

#如果文件夹不存在，创建文件夹
if [ ! -d "log" ]; then
  mkdir log
fi

echo '++ run storage service by docker ++'
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
--name redis-account \
-p 10002:6379 \
-d \
daocloud.io/library/redis \
redis-server --appendonly yes

docker run \
--name redis-article \
-p 10003:6379 \
-d \
daocloud.io/library/redis \
redis-server --appendonly yes

nohup java -jar ./account/build/libs/account-1.0.0-release.jar > log/account.log &
nohup java -jar ./article/build/libs/article-1.0.0-release.jar > log/article.log &
echo '++ service restart ++'