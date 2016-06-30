# Kotlin China web Dockerfile
# For more information please visit Kotlin China http://kotlin-cn.tech'
# This docker file is build for deploying release
# Author: chpengzh

# DEFAULT ENVIORMENT CONFIG:
#    jdbc_driver=com.mysql.jdbc.Driver
#    jdbc_url=jdbc:mysql://localhost:3306/kotlin_china?useUnicode=true&characterEncoding=UTF-8
#    jdbc_username=root
#    jdbc_password=root
#    secret_jwt=XPMQwiRS6aE8pHeUyVDotWCI92F50Ynu
#    secret_password=L21mVCqxXldrNfaAM0YotRO35FUuP8se

FROM nimmis/java

MAINTAINER chpengzh chpengzh@foxmail.com

# 安装 nginx & git
ADD web/docker/sources.list /etc/apt/sources.list
RUN apt-get update && apt-get install -y git && \
    apt-get autoremove && apt-get autoclean

# 同步代码, 安装依赖
RUN cd /usr/local/ && git clone https://github.com/Kotlin-lang-CN/Kotlin-CN.git && \
    cd Kotlin-CN && ./gradlew web:buildDependents

# 编译代码, 会同步一次代码, 以便每一次编译都会使用上一步的依赖缓存
ENV BUILD_VERSION=1
RUN mkdir /usr/local/app && cd /usr/local/Kotlin-CN &&  \
    git fetch && git reset --hard origin/master && \
    ./gradlew web:build && \
    mv web/build/libs/kotlin_china-latest.jar /usr/local/app && \
    rm -rf /usr/local/Kotlin-CN

# 应用的 nginx 配置
WORKDIR /usr/local/app
EXPOSE 8080
CMD java -jar kotlin_china-latest.jar
