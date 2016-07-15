## Rest接口文档

### 关于用户鉴权

用户使用github账号登录, OAuth 鉴权成功后会为添加失效为1周的 Cookie, key 为 'kotlin_cn' 值为经过 URIEncode 后的明文 json 串. Json 串包含
用户名, 用户头像等用户基本信息, 还包括JWT. 

登录成功后, 所有的对服务器的请求均需要带上 cookie, 用于识别登录用户和鉴权

如我使用github账号登录后, 返回的cookie信息如下

```
kotlin_cn=%7B%22profile%22%3A%7B%22avatar_url%22%3A%22https%3A%2F%2Favatars.githubusercontent.com%2Fu%2F7821898%3Fv%3D3%22%2C%22email%22%3A%22chpengzh%40foxmail.com%22%2C%22html_url%22%3A%22https%3A%2F%2Fgithub.com%2Fchpengzh%22%2C%22id%22%3A7821898%2C%22name%22%3A%22%E6%89%8B%E4%B8%8D%E8%A6%81%E4%B9%B1%E6%91%B8%22%7D%2C%22token%22%3A%22eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJwcm9maWxlXCI6e1wiYXZhdGFyX3VybFwiOlwiaHR0cHM6Ly9hdmF0YXJzLmdpdGh1YnVzZXJjb250ZW50LmNvbS91Lzc4MjE4OTg_dj0zXCIsXCJlbWFpbFwiOlwiY2hwZW5nemhAZm94bWFpbC5jb21cIixcImh0bWxfdXJsXCI6XCJodHRwczovL2dpdGh1Yi5jb20vY2hwZW5nemhcIixcImlkXCI6NzgyMTg5OCxcIm5hbWVcIjpcIuaJi-S4jeimgeS5seaRuFwifSxcInNhbHRcIjpcImtxemRqeGN6Znp2amZ2c2liZW9wcHV6b2xzeHZjd3dsbGlicnNna2dmaXN1eXNnaGFuaG1pZ2FodnBpdXZtb2RcIn0ifQ.m16DbVKRxKZl2QD9E3pVIam3OMzK_QYbnjFeoGGwfmyBTSlOAa9qofELdSON82U2mYWXbFZQq_dk3Z4Bu5S2RA%22%7D
```

经过 URIDecode 后, kotlin_cn 的值为(忽略排版)

```json
{
    "profile":{
        "avatar_url":"https://avatars.githubusercontent.com/u/7821898?v=3",
        "email":"chpengzh@foxmail.com",
        "html_url":"https://github.com/chpengzh",
        "id":7821898,
        "name":"手不要乱摸"
    },
    "token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJwcm9maWxlXCI6e1wiYXZhdGFyX3VybFwiOlwiaHR0cHM6Ly9hdmF0YXJzLmdpdGh1YnVzZXJjb250ZW50LmNvbS91Lzc4MjE4OTg_dj0zXCIsXCJlbWFpbFwiOlwiY2hwZW5nemhAZm94bWFpbC5jb21cIixcImh0bWxfdXJsXCI6XCJodHRwczovL2dpdGh1Yi5jb20vY2hwZW5nemhcIixcImlkXCI6NzgyMTg5OCxcIm5hbWVcIjpcIuaJi-S4jeimgeS5seaRuFwifSxcInNhbHRcIjpcImtxemRqeGN6Znp2amZ2c2liZW9wcHV6b2xzeHZjd3dsbGlicnNna2dmaXN1eXNnaGFuaG1pZ2FodnBpdXZtb2RcIn0ifQ.m16DbVKRxKZl2QD9E3pVIam3OMzK_QYbnjFeoGGwfmyBTSlOAa9qofELdSON82U2mYWXbFZQq_dk3Z4Bu5S2RA"
}
```

服务器并不会对所有接口进行鉴权, 本文档中对所有需要鉴权的接口我们表述为`Cookie: Require`

除此之外, 所有的请求都加上了对`Referer`的校验

### 响应数据

对于未通过鉴权的接口统一的返回格式如下

```json
{"status":403, "message":"some message"}
```

对于执行成功但无数据的请求, 返回格式如下

```json
{"status":200, "message":""}
```

对于执行成功,且有数据返回的格式如下

```json
{
    "status":200,
    "message":""
    "data":<数据对象/数据列表等>
}
```

### 默认声明

接口的url统一使用`http://<host>/api/<version>/<path>` 这种格式

接口版本为1, 使用的 url 为 `http://localhost:8080/api/v1/<path>`

接口默认方法为 `GET`, 使用 `POST` 方法的接口会明确说明

### 接口文档

#### 文章详情

```
url: /article/{aid}
path_variable: aid 文章id
response:
{
    "data": {
        "create_time": "4 天 前",
        "avatar_url": "https://avatars.githubusercontent.com/u/7821898?v=3",
        "author": 7821898,
        "html_url": "https://github.com/chpengzh",
        "name": "手不要乱摸",
        "comment": 0,
        "title": "我是来测试发送的",
        "category": "tag1",
        "aid": 1,
        "content": "我是来测试发送内容的",
        "email": "chpengzh@foxmail.com",
        "flower": 1
    },
    "message": "",
    "status": 200
}
```

#### 文章列表

```json
url: /article/list
param: page 页数
param: category all-所有, <other>-具体类别
response:
{
    "data": [
        {
            "create_time": "4 天 前",
            "avatar_url": "https://avatars.githubusercontent.com/u/7821898?v=3",
            "author": 7821898,
            "html_url": "https://github.com/chpengzh",
            "name": "手不要乱摸",
            "comment": 0, #评论数量
            "title": "我是来测试发送的4",
            "category": "tag3",
            "aid": 4,
            "email": "chpengzh@foxmail.com",
            "flower": 0 #点赞数量
        }
    ],
    "message": "",
    "status": 200
}
```

### 我的文章列表

```json
url: /article/mine
Cookie: Require
param: page 页数
param: category all-所有, <other>-具体类别
response: 
{
    "data": [
        {
            "create_time": "4 天 前",
            "avatar_url": "https://avatars.githubusercontent.com/u/7821898?v=3",
            "author": 7821898,
            "html_url": "https://github.com/chpengzh",
            "name": "手不要乱摸",
            "comment": 0,
            "title": "我是来测试发送的4",
            "category": "tag3",
            "aid": 4,
            "email": "chpengzh@foxmail.com",
            "flower": 0
        }
    ],
    "message": "",
    "status": 200
}
```

### 发布一篇文章

```json
url: /article/publish
Cookie: Require
method: POST
request:
{
    "title":"测试发送文章标题", 
    "content":"markdown格式的文章内容",
    "category": "default"
}
response:
{
    "status":200, 
    "message":""
}
```

> 

### 发布评论

```json
url: /comment/make
Cookie: Require
method: POST
request:
{
    "content":"评论的内容", 
    "aid":1, #文章id
    "reply":1 #可选 回复用户id
}
response:
{
    "status":200,
    "message":""
}
```

### 查看文章的评论列表

```json
url: /comment/list
param: aid 文章id
param: page 页数
response: 
{
    "data": [
        {
            "create_time": 1467831158000,
            "aid": 2,
            "content": "测试评论2",
            "cid": 2,
            "commenter": 7821898
        },
        {
            "create_time": 1467829186000,
            "aid": 2,
            "content": "测试评论1",
            "cid": 1,
            "commenter": 7821898
        }
    ],
    "message": "",
    "status": 200
}
```

### 查看我的评论列表

```json
url: /comment/mine
Cookie: Require
param: aid 文章id
param: page 页数
response: 
{
    "data": [
        {
            "create_time": 1467831158000,
            "aid": 2,
            "content": "测试评论2",
            "cid": 2,
            "commenter": 7821898
        },
        {
            "create_time": 1467829186000,
            "aid": 2,
            "content": "测试评论1",
            "cid": 1,
            "commenter": 7821898
        }
    ],
    "message": "",
    "status": 200
}
```

### 查看所有回复我的列表

```json
url: /comment/reply
Cookie: Require
param: aid 文章id
param: page 页数
response: 
{
    "data": [
        {
            "create_time": 1467963113000,
            "hint": "有人@了你",
            "reply": 1,
            "aid": 2,
            "content": "测试评论",
            "cid": 3,
            "commenter": 7821898
        },
        {
            "create_time": 1467831158000,
            "hint": "有人评论了你的文章",
            "aid": 2,
            "content": "测试评论",
            "cid": 2,
            "commenter": 7821898
        }
    ],
    "message": "",
    "status": 200
}
```

### 点赞

```json
url: /flower
Cookie: Require
method: POST
param: oid 对象id
param: mode 0-文章, 1-评论
response:
{
    "status":200,
    "message":""
}
```

### 取消点赞

```json
url: /flower/cancel
Cookie: Require
method: POST
param: oid 对象id
param: mode 0-文章, 1-评论
response:
{
    "status":200,
    "message":""
}
```
