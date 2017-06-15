#!/usr/bin/env bash

cp ../release/project.properties project.properties
cp project.properties account/src/main/resources && cp project.properties article/src/main/resources

echo "++ recompile projects ++"
./gradlew account:jar && ./gradlew article:jar

echo '++ service stop ++'
pkill -f *article* && pkill -f *account*

#如果文件夹不存在，创建文件夹
if [ ! -d "log" ]; then
  mkdir log
fi

nohup java -jar ./account/build/libs/account-1.0.0-release.jar > log/account.log &
nohup java -jar ./article/build/libs/article-1.0.0-release.jar > log/article.log &
echo '++ service restart ++'