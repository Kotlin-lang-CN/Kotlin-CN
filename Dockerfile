FROM daocloud.io/library/java
MAINTAINER chpengzh <chpengzh@foxmail.com>
WORKDIR /usr/libs/Kotlin-CN
ADD forum/build/libs/forum-1.0.0-release.jar /usr/libs/Kotlin-CN
EXPOSE 8080
ENTRYPOINT java -jar forum-1.0.0-release.jar