# Account Api

## 注册用户

> POST

```
/account/register
```

> Parameter

```
username: <string>用户名
email: <string>用户email
password: <string>密码)
```

> Response

```json
{
	"code": 0,
	"msg": "",
	"uid": "<long>用户uid",
	"token": "<string>用户登录 token"
}
```

## 登录

> POST

```
/account/login
```

> Parameter

```
login_name: <string>用户名或邮箱
password: <string>用户密码
```

> Response

```json
{
	"code": 0,
	"msg": "",
	"uid": "<long>用户uid",
	"token": "<string>用户登录 token",
	"username": "<string>用户名",
	"email": "<string>用户email",
	"is_email_validate": "<bool>邮箱验证状态"
}
```

## 获取用户信息

> GET

```
/account/user/:uid
```

> Parameter

```
:uid: <long>查询用户uid
```

> Response

```json
{
	"code": 0,
	"msg": "",
	"uid": "<long>用户uid",
	"username": "<string>用户名",
	"logo":"<string>用户logo",
	"email": "<string>用户email1",
	"is_email_validate": "<bool>邮箱验证状态"
}
```

## 更换登录密码

> POST

```
/account/user/:uid/password
```

> Parameter

```
:uid: <long>用户id
password: <string>用户密码
```

> Response 

```json
{ 
    "code": 0,
    "msg": ""
}
```

## 更新我的用户信息

> POST

```
/account/user/:uid/update
```

> Parameter

```
:uid: <long>用户id
username: <string>用户名
logo: <string>用户logo
email: <string>用户email
```

> Response

```json
{
    "code": 0,
    "msg": ""
}
```
