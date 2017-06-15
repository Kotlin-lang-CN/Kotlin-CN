## [Kotlin China](http://kotlin-china.wiki)

[Kotlin China](http://kotlin-china.wiki) 社区地址

我们致力于提供最好的Kotlin中文教程 共建最潮流的Kotlin中文社区

走过路过欢迎 star 一波~~

欢迎广大 Kotlin 爱好者提交 commit 或 issue, 你们的支持是我们最大的动力

## 社区

社区正在开发中，欢迎参与kotlin学习讨论, 或是参与论坛开发...

加入我们 QQ:561490348 [![QQ群](http://pub.idqqimg.com/wpa/images/group.png)](//shang.qq.com/wpa/qunwpa?idkey=3ca5ebb183d90a980fff13e960380bdd660b3475e1434b12e35d42d5df0428b6)

邮件联系我: [chpengzh](mailto:chpengzh@foxmail.com)

## Why Kotlin?

首先, Java 能做的 Kotlin 也能做, 而且后者诚然具有不少优秀的高级语言特性 (去特么的Java, 垃圾语言毁我青春耗我钱财).

其次, Kotlin 写 Android 也写了一段时间了, 想试试写后台什么感觉, 于是就决定做一个 Kotlin-China 的论坛.

最近 Kotlin 也被 google 推为了官方编程语言之一，也是大势所趋.
新事物出现是必然的、客观的规则， 一方面有些人会顾虑新事物所造成的革新会颠覆自己过去的认知，一方面会有些人大胆的去接受这些新事物并一探究竟.
于是我决定就用这门语言去做一个论坛(使用的基本都是传统`javaEE`库)， 用最简单的最原始的方式来证明这个语言是完备的，并不脱离于现有的`java`体系， 而是更好的依附在其之上.

诚如所言, Kotlin-China 将是一个 Kotlin in product 的网站. 

## For Developer

社区正在开发中，欢迎你的加入

### 三步搭建后端测试环境
 
- [x] 配置java运行环境(略，不会的google)

- [x] 配置docker服务

> Ubuntu
    
```shell
sudo apt-get install docker.io
```

> mac 

安装 [virtual-box](https://www.virtualbox.org/wiki/Downloads)

安装 docker-machine(https://github.com/docker/machine)

> 更多

如何配置 docker 服务，可查看 docker [官方网站](https://www.docker.com/)

- [x] 启动测试环境模式

```
./dev.sh
```

### 前端测试环境

该项目采用前后端分离的方式，前端采用`vue 2.0`框架

- [x] 启动前端测试环境

```shell
cd forum_ui && npm install && npm run dev
```