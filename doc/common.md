# Common

> Host 

```
http://api.kotlin-cn.org
```

> Common Headers

```json
{
   "X-App-Device": "<string>device token,设备唯一标识",
   "X-App-Platform": "<int>设备平台，0-html, 1-android, 2-ios",
   "X-App-Vendor": "<string>设备厂商名",
   "X-App-System": "<string>设备系统名称",
   "Content-Type": "application/x-www-form-urlencoded"
}
```

> Authorization Headers

*注*: 登录用户需要带上该token来标识登录状态

```json
{
  "X-App-Token": "<string>用户token, 用于用户身份鉴权"
}
```

> 接口格式描述

- 所有`GET`请求的查询参数全部带在url尾部，post请求的查询参数

- 所有`POST`请求的查询参数以表单形式带在请求方法体内

- 所有请求返回值为`json`格式，包含返回码`code`和错误提示`msg`, 请求成功状态码为`0` 


> api

|name|prefix|process status|
|---|---|---|
|account|/account|0/0|
|article|/article|0/0|
|reply|/reply|0/0|