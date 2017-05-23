# Article Api

## 模型

### article 

```json
{
  "id": "<long>文章id",
  "title": "<string>文章标题",
  "create_time": "<long>秒级时间戳",
  "category": "<int>文章分类",
  "tags": "<string>文章tags，使用`;`分割"
  
}
```

### user

```json
{
    "uid": "<long>用户uid",
    "username": "<string>用户名",
    "logo": "<string>用户头像",
    "email": "用户email"
}
```


## 发布文章

> POST

```
/article/post
```

> Parameter

```
title: <string>文章标题
author: <string>文章内容
category: <int>文章类型
tags: <string>文章tags
content: <string>文章内容
```

> Response

```json
{
	"code": 0,
	"msg": "",
	"article": "<article>文章"
}
```

## 修改文章内容

> POST

```
/article/post/:id/update
```

> Parameter

```
:id: <long>文章id
title: <string>文章标题
tags: <string>文章tags
content: <string>文章内容
```

> Response

```json
{
	"code": 0,
	"msg": "",
	"article": "<article>文章内容"
}
```

## 获取文章信息

> GET

```
/article/post/:id
```

> Parameter

```
":id": "文章id"
```

> Response

```json
{
	"code": 0,
	"msg": "",
	"article": "<article>文章元数据",
	"author": "<user>作者",
	"last_edit_user": "<user>上次编辑用户",
	"last_edit_time": "<long>秒级时间戳"
}
```

## 获取文章内容

> GET

```
/article/post/:id/content
```

> Parameter

```
":id": "文章id
```

> Response(Content-Type: text/html)

* 文章文本内容(markdown 格式)

## 获取文章列表

> GET

```
/article/list
```

> Parameter

```
order: <int>获取方式 (0-所有文章时间倒叙, 1-精品文章时间倒叙)
offset: <int>查询标记
```

> Response

```json
{
    "data": [{
        "article": "<article>文章元数据",
        "author": "<user>作者",
        "last_edit_user": "<user>上次编辑用户", 
        "last_edit_time": "<long>秒级时间戳"
    }],
    "next_offset": "<int>下次查询标记" 
}
```







