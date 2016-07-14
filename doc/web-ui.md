## web-ui 模块概述

#### 编译构建

```shell
cd web-ui && npm install && grunt
```

在Firefox中打开index.html(坚定的firefox党)

```shell
open /Applications/Firefox.App src/app/index.html
```

#### 文件结构

`src/app/*` 应用页面
`src/component/*.jsx` 页面组件
`src/framework/*.js` 业务相关的函数库
`src/*.jsx` 页面

#### 使用 docker 构建 release

构建

```shell
docker build -f web-ui/docker/Dockerfile -t web_ui .
```

运行

```shell
docker run -d -p 8081:80 --name some-web-ui web_ui
```

访问首页`http://localhost:8081`
