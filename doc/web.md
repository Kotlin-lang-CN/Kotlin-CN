## web 模块概述

#### 环境依赖

- [x] mysql-server

- [x] openjdk-jdk-8

#### gradle 任务

安装外部包依赖(第一次导入工程之后需要安装)

```shell
./gradlew web:buildDependents
```

启动应用(如果没有安装包依赖, 会自动安装包依赖)

```shell
./gradlew web:bootRun
```

应用默认工作ip地址为`http://localhost:8080`

#### 关于开发

该项目的运行入口(main函数)在 `tech.kotlin.china.Application.kt` 中定义

所有的 restful 接口均定义在 `tech.kotlin.china.controller.rest` 中

现在以其中一个接口为例

```Kotlin
@Doc("查看用户列表")
@RequestMapping("/account/list/{page}", method = arrayOf(GET))
fun userList(@PathVariable("page") @Doc("分页") page: Int,
             @RequestParam("category", defaultValue = "all")
             @Doc("筛选用户类别(all/admin/disable)") category: String) = check {
    page.require("不合法的页数") { it > 0 }
    category.require("错误的用户类型") { it.equals("all") || it.equals("admin") || it.equals("disable") }
}.authorized(admin = true).session {
    val mapper = it.of<AccountMapper>()
    PageHelper.startPage<Account>((page - 1) * ACCOUNT_PAGE_SIZE + 1, page * ACCOUNT_PAGE_SIZE)
    when (category) {
        "all" -> mapper.queryUserList()
        "admin" -> mapper.queryAdminList()
        "disable" -> mapper.queryDisabledList()
        else -> null
    }!!.map {
        it[Account::uid, Account::name]
                .p("rank", if (it.rank == 1) "admin" else "normal")
                .p("forbidden", if (it.forbidden) "被封禁" else "未封禁")
    }
}
```

很容易看出的是, 函数参数定义了该请求的必要参数(更多文档参考 Spring-MVC)

`check` 函数用于请求的基本数据校验
`authorized` 函数用于身份校验
`session` 中定义数据库会话(事务会话需要声明transaction = true)以及最后的数据响应

每一个请求将按照以上顺序串联执行, 对于过程中不满足条件的情况会抛出 `BusinessError` 

更多这一方面的使用方法, 参考基类定义 `tech.kotlin.china.controller.rest._Rest`

### 想说的话

参与开发之前推荐阅读和了解

 - [x] [Kotlin](http://kotlinlang.org/docs/reference/) 的官方网站教程
 
 - [x] [Spring-Boot](https://spring.io/guides) 微型集成网站框架
 
 - [x] [Mybatis3](http://www.mybatis.org/mybatis-3/) 数据库连接库

说明: 

所有Controller全部使用 Kotlin 的函数式编程风格, 实现了从请求到数据库会话的完整过程(去特么的`controller` + `service` + `dao`), 你会发现AOP原来不必如此折腾.

`utils.kotlin` 中包含了一些便捷库(仔细看看, 你可能想象不到不到的 `Kotlin` 原来可以这么用)

#### Docker release

本项目提供 Dockerfile 来快速构建 release 镜像

```shell
docker build -f web/docker/Dockerfile -t kotlin_china .
```

初始化运行测试数据库

```shell
mysql -u <your mysql username> -p < web/docker/init.sql 
```

启动应用应用(请自己设定环境变量,如下给出的是默认值)

```shell
docker run --name some-kotlin-china -d \
    -p 8080:80 \
    -e "jdbc_url=jdbc:mysql://localhost:3306/kotlin_china?useUnicode=true&characterEncoding=UTF-8" \
    -e "jdbc_username=root" \
    -e "jdbc_password=root" \
    -e "secret_jwt=XPMQwiRS6aE8pHeUyVDotWCI92F50Ynu" \
    -e "secret_password=L21mVCqxXldrNfaAM0YotRO35FUuP8se" \
    kotlin_china
```