本项目为开发者准备了 Docker 镜像用以快速部署, 只需要使用docker就能凯苏启动`Kotlin-China`应用

构建镜像

```shell
docker build -f docker/Dockerfile -t kotlin_china .
```

初始化运行数据库

```shell
mysql -u <your mysql username> -p < docker/init.sql 
```

启动应用应用

```shell
docker run --name some-kotlin-china -d \
    -p 8080:80 \
    -e "jdbc_url=jdbc:mysql://192.168.99.100:3306/kotlin_china?useUnicode=true&characterEncoding=UTF-8" \
    -e "jdbc_username=root" \
    -e "jdbc_password=root" \
    kotlin_china
```



