# Kotlin-CN 接口文档

## About

### 请求地址 

> Host `http://kotlin-cn.org/api` 

### 请求头

```
{
   "X-App-Device": <string>device token,设备唯一标识,
   "X-App-Platform": <int>设备平台，0-html, 1-android, 2-ios,
   "X-App-Vendor": <string>设备厂商名,
   "X-App-System": <string>设备系统名称,
   "Content-Type": "application/x-www-form-urlencoded"
}
```

### 登录鉴权

*注*: 登录用户需要带上登录token来标识登录状态

```
{
  "X-App-Token": <string>用户token, 用于用户身份鉴权
}
```

### 接口格式描述

- [x] 所有`GET`请求的查询参数全部带在url尾部，post请求的查询参数

- [x] 所有`POST`请求的查询参数以表单形式带在请求方法体内

- [x] 所有请求返回值为`json`格式，包含返回码`code`和错误提示`msg`, 请求成功状态码为`0` 

## Account

### 注册用户

```
POST /account/register
username    <string>用户名, 长度不小于两位
password    <string>密码, 长度不小于8位('a'..'z' + 'A'..'Z' + '0'..'9')
email       <string>用户email, 满足邮箱格式

{
    "code": 0,
	"msg": "",
	"uid": <long>用户uid,
	"token": <string>登录会话 token
}
```

### 登录

```
POST /account/login
login_name  <string>用户名或邮箱
password    <string>用户密码

{
    "code": 0,
    "msg": "",
    "uid": <long>用户uid,
    "email": <string>注册邮箱,
    "is_email_validate": <bool>邮箱是否验证,
    "token": <string>登录会话 token,
    "username": <string>用户名
}
```

### 获取用户信息

```
GET /account/user/:uid
:uid    <long>查询用户uid

{
    "code": 0,
    "msg": "",
    "role": <int>用户角色 0-普通用户 1-管理员,
    "create_time": <long>创建时间戳,毫秒,
    "last_login": <long>上次登录时间,毫秒,
    "state": <int>用户状态 0-正常 1-封禁,
    "email": <string>用户邮箱,
    "is_email_validate": <bool>用户邮箱是否验证,
    "username": <string>用户名
}
```

### 更换登录密码

```
POST /account/user/:uid/password
:uid        <long>用户id
password    <string>用户密码

{
    "msg": "",
    "code": 0
}
```

### 更新我的用户信息

```
POST /account/user/:uid/update
:uid        <long>用户id
username    <string>用户名, 长度不小于两位
logo        <string>用户logo
email       <string>用户email, 满足邮箱格式
* username, logo, email三者至少需要填入一项

{
    "code": 0,
    "msg": ""
}
```

## Article

### 发布文章

```
POST /article/post
title       <string>文章标题, 非空, 不少于2个字符
author      <long>文章发布者uid
category    <int>文章类型
tags        <string>文章tags
content     <string>文章内容, 非空, 不少于30个字符

{
    "msg": "",
    "code": 0,
    "id": <long>生成文章id
}
```

### 获取文章信息

```
GET /article/post/:id
:id     <long>文章id

{
  "msg": "",
  "code": 0,
  "author": <user> 作者信息,
  "article": <article>文章信息,
  "last_editor": <user>上次编辑人信息,
  "content": <text_content>文本内容信息
}
```

### 修改文章内容

```
POST /article/post/:id/update
:id     <long>文章id
title   <string>文章标题
tags    <string>文章tags
content <string>文章内容

{
	"code": 0,
	"msg": ""
}
```

### 删除文章

```
POST /article/post/:id/delete
:id     <long>文章id

{
	"code": 0,
	"msg": ""
}
```

### 获取评论

```
GET /article/:id/reply
offset  <int>分页偏移值, 默认0
limit   <int>查询数, 最大20, 默认20

{
    "msg": "",
    "code": 0,
    "reply": [{
        "user": <user> 评论者,
        "meta": <reply> 评论元数据,
        "content": <text_conten> 评论内容
    }]
}
```

### 评论

```
POST /aritcle/:id/reply
:id         <long>文章id
content     <string>评论内容, 非空, 不少于10个字符

{
    "msg": "",
    "code": 0,
    "id": <long>生成评论id
}
```

### 删除评论

```
POST /article/reply/:id/delete
:id         <long>评论id

{
    "msg": "",
    "code": 0
}
```

### 获取最新文章列表

```
GET /article/list
offset  <int>分页偏移值, 默认0
limit   <int>查询数, 最大为20, 默认20

{
    "msg": "",
    "code": 0,
    "articles":[{
        "meta": <article> 文章元信息,
        "author": <user> 作者信息,
        "last_editor": <user> 上次编辑人信息，
        "replies": <int>评论总数，
        "is_fine": <bool>是否是精品
    }],
    "next_offset": <int>下次请求分页偏移值
}
```

### 获取特定类别最新文章列表

```
GET /article/category/:id
:id     <int>文章类别id
offset  <int>分页偏移值, 默认0
limit   <int>查询数, 最大为20, 默认20
 
{
    "msg": "",
    "code": 0,
    "articles":[{
        "meta": <article> 文章元信息,
        "author": <user> 作者信息,
        "last_editor": <user> 上次编辑人信息，
        "replies": <int>评论总数，
        "is_fine": <bool>是否是精品
    }],
    "next_offset": <int>下次请求分页偏移值
}
```

### 获取精品文章列表

```
GET /article/fine
offset  <int>分页偏移值, 默认0
limit   <int>查询数, 最大为20, 默认20

{
    "msg": "",
    "code": 0,
    "articles":[{
        "meta": <article> 文章元信息,
        "author": <user> 作者信息,
        "last_editor": <user> 上次编辑人信息，
        "replies": <int>评论总数，
        "is_fine": <bool>是否是精品
    }],
    "next_offset": <int>下次请求分页偏移值
}
```

### 获取文章区话题

*注*: 文章类型下标从0开始,对应列表元素

```
GET /article/category

{  
    "msg": "",
    "code": 0,
    "category": ["<string>文章话题名称"]
}
```

## Flower

### 文章点赞

```
POST /flower/article/:aid/star
:aid 文章id

{
    "msg": "",
    "code": 0
}
```

### 文章取消点赞

```
POST /flower/article/:aid/unstar
:aid 文章id

{
    "msg": "",
    "code": 0
}
```

### 查看某篇文章点赞状态

```
GET /flower/article/:aid/star
:aid 文章id

{
    "msg": "",
    "code": 0,
    "has_star": <bool>是否点过赞
    "flower": {
        "create_time": <long>创建时间戳
    }
}
```

### 查看某篇文章点赞数

```
GET /flower/article/star/count
ids 查询文章id，以`,`分割

{
    "msg": "",
    "code": 0,
    "data": {
        <long>查询id: <int>点赞数
    }
}
```

## Misc

### 获取网站公告栏

```
GET /misc/dashboard

{
    "msg": "",
    "code": 0,
    "text": <string>markdown格式
}
```

### 获取首页链接

```
GET /misc/dashborad

{
    "msg": "",
    "code": 0,
    "home_link": <string>url
}
``` 

## Admin

### 修改用户状态

```
POST /admin/user/:uid/state
:uid    <long>操作用户uid
state   <int>用户状态

{
    "code": 0,
    "msg": ""
}
```

### 修改文章状态

```
POST /admin/article/:id/state
:id     <long>文章id
state   <int>文章状态

{   
    "code": 0,
    "msg": ""
}
```

### 修改回复状态

```
POST /admin/reply/:id/state
:id     <long>文章id
state   <int>回复状态

{   
    "code": 0,
    "msg": ""
}
```

### 获取全部文章列表

```
GET /admin/article/list
offset  <int>分页偏移值, 默认0
limit   <int>查询数, 最大为20, 默认20

{
    "msg": "",
    "code": 0,
    "articles":[{
        "meta": <article> 文章元信息,
        "author": <user> 作者信息,
        "last_editor": <user> 上次编辑人信息，
        "replies": <int>评论总数，
        "is_fine": <bool>是否是精品
    }],
    "next_offset": <int>下次请求分页偏移值
}
```
## Rss

### 精品区

```
GET /rss/fine
```

### 最新文章

```
GET /rss/latest
```

## Model

### article 

```
{
    "id": <long>文章id,
    "title": <string>文章标题,
    "author": <long>作者id,
    "create_time": <long>创建时  间戳,毫秒,
    "category": <int>文章类型,
    "tags": <string>使用';'分割的tag,
    "last_edit_time": <long>上次编辑时间,
    "last_edit_uid": <long>上次编辑uid,
    "state": <int>文章状态 0-正常 1-封禁 2-删除 3-加精,
    "content_id": <long>文章文本id
}
```

### reply 

```
{
    "id": <long> 回复id,
    "reply_pool_id": <string> 评论池id,
    "owner_uid": <long> 回复人id,
    "create_time": <long> 创建时间,
    "state": <int>回复状态 0-正常 1-封禁 2-删除,
    "content_id": <long> 评论文本id,
    "alias_id": <long>关联评论id
}
```

### user

```
{
    "uid": <long>用户uid,
    "username": <string>用户名,
    "logo": <string>用户logo,
    "email": <string>用户email
}
```

### text_content

```
{
    "id": <long>文本id,
    "content": <string>文本内容,
    "serialize_id": <string>文本池,
    "create_time": <long>文本创建时间
}
```
