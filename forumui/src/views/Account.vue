<template>
  <div class="account">
    <div class="error" style="display:none">
      错误信息看这里
    </div>
    <div class="register">
      <div class="list-head">注册新用户</div>
      <form action="/api/account/register" accept-charset="utf-8" method="post">
        <div>
          <input type="text" name="user" placeholder="用户名">
        </div>
        <div>
          <input type="email" name="email" placeholder="邮箱">
        </div>
        <div>
          <input type="password" name="password" placeholder="密码">
        </div>
        <div>
          <input type="password" name="password_confirm" placeholder="确认密码">
        </div>
        <div>
          <input type="submit" name="commit" value="提交注册">
        </div>
      </form>
    </div>
    <div class="login">
      <div class="list-head">登录</div>
      <form action="/api/account/login" accept-charset="utf-8" method="post">
        <div>
          <input type="text" name="user" placeholder="用户名／邮箱">
        </div>
        <div>
          <input type="password" name="password" placeholder="密码">
        </div>
        <div class="div-checkbox">
          <input type="checkbox" value="1" name="remember">
          <div>记住登录状态</div>
        </div>
        <div>
          <input type="submit" name="commit" value="登录">
        </div>
      </form>
    </div>
    <div class="login-three-party" style="display:none">
      <div class="list-head">用其他平台账号登录</div>
      <form action="#/account" accept-charset="utf-8" method="post">
        <div>
          <input type="submit" name="commit" value="GITHUB登录">
        </div>
      </form>
    </div>
  </div>
</template>
<script>
  import Util from '../assets/js/Util.js'
  export default {
    name: 'hello',
    data: function () {
      return {
        msg: 'TODO:Home'
      }
    },
    methods: {


    }
  }
</script>
<style scoped lang="less">
  .account {
    .error {
      margin: 24px 16px 8px 16px;
      padding: 8px 8px;
      background: antiquewhite;
      color: darkgrey;
      font-size: 14px;
      text-align: center;
    }
    .register, .login {
      margin: 0px 16px;
      background: white;
      margin-top: 20px;
      padding-bottom: 12px;
    }
    .list-head {
      background: #fafafa;
      font-size: 14px;
      padding: 4px 16px;
      color: #999;
    }
    form {
      margin: 6px 0px;
      > div {
        box-sizing: border-box;
        margin: 4px 16px;
        border: 1px solid #f1f1f1;
        padding-left: 8px;
      }
      > div.div-checkbox {
        border: 0px;
        font-size: 12px;
        color: #666;
        margin: 12px 8px;
        div {
          display: inline-block;
          margin-left: 8px;
        }
      }
    }
    input {
      line-height: 38px;
      margin: 0;
      padding: 0px;
      width: 100%;
      outline: none;
      border-color: white;
      border-width: 0px;
      font-size: 15px;
      [type="checkbox"] {
        display: inline-block;
        width: initial;
      }
      [type="submit"] {
        border-radius: 5px;
        height: 36px;
        background: white;
        border-color: #f1f1f1;
        color: #eb5424;
      }
    }
  }
</style>
