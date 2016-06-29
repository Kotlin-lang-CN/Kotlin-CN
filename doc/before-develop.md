## Kotlin CN 工程概述

#### 主要分为两个模块 web 和 web-ui
 
其中 web 模块为后端模块

- 基于 Spring-Boot 框架, 为前端应用提供 restful 接口
- 使用 Mybatis3 操作 Mysql 数据库, 使用 JDBC 管理数据库事务

web-ui 为前端页面模块

- 基于 react 框架实现应用交互
- 使用 Grunt 实现前端构建任务
- 使用 Nginx 作为静态页面服务器

#### 项目使用 gradle-wrapper 构架

web模块的启动任务

```shell
./gradlew web:bootRun
```

web-ui模块的构建

```shell
./gradlew web-ui:grunt
```

web-ui应用启动方式: 直接使用浏览器打开 web-ui/src/app/index.html