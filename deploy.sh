#!/usr/bin/env bash

cp ../release/project.properties project.properties
cp project.properties account/src/main/resources && cp project.properties article/src/main/resources

echo "++ compile project(':account') ++"
./gradlew account:jar
echo "++ compile project(':article') ++"
./gradlew article:jar

#如果文件夹不存在，创建文件夹
if [ ! -d "log" ]; then
  mkdir log
fi

accountPid=$(ps -ef | egrep "account-" | grep -v grep | awk '{print $2}')
if [ ${accountPid} ]; then
    kill ${accountPid}
fi
articlePid=$(ps -ef | egrep "article-" | grep -v grep | awk '{print $2}')
if [ ${accountPid} ]; then
    kill ${accountPid}
fi

nohup java -jar ./account/build/libs/account-1.0.0-release.jar > log/account.log &
nohup java -jar ./article/build/libs/article-1.0.0-release.jar > log/article.log &