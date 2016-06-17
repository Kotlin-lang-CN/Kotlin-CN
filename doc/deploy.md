### Mysql

> get image

```docker
docker pull mysql
```

> start mysql

```docker
docker run --name mysql -v /root/mysql:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql
```

