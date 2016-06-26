### 环境依赖

> 编译运行环境

Java openjdk-8

> 数据服务依赖

oracle-mysql

> 如何启动应用

./gradlew web:bootRun

### 应用结构说明

- {root}/docker: 正式环境部署, 用于快速构建正式环境下的release镜像

- {root}/docs: 项目文档

- {root}/web: 项目模块

    -[x] src/main: 项目源码, 包含所有前端与restful后台应用代码
    
    -[ ] src/main/assets: 静态资源文件以及生成的js release
    
    -[x] src/main/app: 前端应用
    
    -[ ] src/main/conf: 固定的项目配置文件(因为正式环境依赖docker, 可变的配置均使用系统环境变量来定义)
    
    -[x] src/main/kotlin: 后台应用 
    
### 关于后台的项目开发

参与开发之前推荐阅读和了解

 - [Kotlin](http://kotlinlang.org/docs/reference/) 的官方网站教程
 
 - [Spring-Boot](https://spring.io/guides) 微型集成网站框架
 
 - [Mybatis3](http://www.mybatis.org/mybatis-3/) 数据库连接库

说明: 

`tech.kotlin.china.controller` 包含 Restful Controller 和 ModelAndView Controller.
所有Controller全部使用 Kotlin 的函数式编程风格, 
实现了从请求到数据库会话的完整过程(去特么的`controller` + `service` + `dao`),
你会发现AOP原来不必如此折腾.

`utils.kotlin` 中包含了一些便捷库(仔细看看, 你可能想象不到不到的 `Kotlin` 原来可以这么用)

### 关于前端开发

说起前端, 笔者是个傻逼来的. 我们提供 restful 接口文档, 并欢迎有能力的前端加入(推荐使用react来实现)

另外笔者会写一个 Kotlin-China Android App 

敬请期待

### 使用 Docker 构建应用 release

构建镜像

```shell
docker build -f docker/Dockerfile -t kotlin_china .
```

初始化运行数据库

```shell
mysql -u <your mysql username> -p < docker/init.sql 
```

启动应用应用(请自己设定环境变量,如下给出的是默认值)

```shell
docker run --name some-kotlin-china -d \
    -p 8080:80 \
    -e "jdbc_url=jdbc:mysql://192.168.99.100:3306/kotlin_china?useUnicode=true&characterEncoding=UTF-8" \
    -e "jdbc_username=root" \
    -e "jdbc_password=root" \
    -e "secret_jwt=XPMQwiRS6aE8pHeUyVDotWCI92F50Ynu" \
    -e "secret_password=L21mVCqxXldrNfaAM0YotRO35FUuP8se" \
    -e "static=http://localhost:8080"
    kotlin_china
```


