#!/usr/bin/env bash

cp ../release/project.properties forum/src/main/resources/project.properties

./gradlew forum:jar

pid=$(ps -ef | egrep "forum-" | grep -v grep | awk '{print $2}')
if [ ${pid} ]; then
    kill ${pid}
fi

nohup java -jar ./forum/build/libs/forum-1.0.0-release.jar > ../forum.log &