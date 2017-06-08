<template>
  <div class="login">
    <div class="login-local">
      <div class="list-head">登录</div>
      <div class="form">
        <div>
          <input v-model="loginUser" type="text" name="user" placeholder="用户名／邮箱"/>
        </div>
        <div>
          <input v-model="loginPass" type="password" name="password" placeholder="密码"/>
        </div>
        <div class="button" v-on:click="login">登录</div>
      </div>
    </div>
    <div class="login-three-party" style="display:none">
      <div class="list-head">用其他平台账号登录</div>
      <div class="button" v-on:click="login">登录</div>
    </div>
  </div>
</template>
<script>
  import Event from '../assets/js/Event.js';
  import Util from '../assets/js/Util.js';
  import Config from '../assets/js/Config';
  import Net from '../assets/js/Net.js';
  import LoginMgr from '../assets/js/LoginMgr.js';
  export default {
    data(){
      return {
        loginUser: '',
        loginPass: ''
      }
    },
    methods: {
      login() {
        if (this.loginUser.length < 2) {
          Event.emit("error", "用户名过短");
        } else if (this.loginPass.length < 8) {
          Event.emit("error", '请输入8位以上密码');
        } else {
          Net.post({
            url: Config.URL.account.login,
            condition: {
              'login_name': this.loginUser.trim(),
              'password': this.loginPass.trim(),
            }
          }, (resp) => {
            LoginMgr.login(resp);
            location.href = Config.UI.account
          })
        }
      }
    }
  };
</script>
<style scoped lang="less">
  .login {
    max-width: 500px;
    margin: auto;
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

  .form {
    margin: 6px 0;
    > div {
      box-sizing: border-box;
      margin: 4px 16px;
      border: 1px solid #f1f1f1;
      padding-left: 8px;
    }
    > div.div-checkbox {
      border: 0;
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
    padding: 0;
    width: 100%;
    outline: none;
    border-width: 0;
    font-size: 15px;
  }

  input[type="checkbox"] {
    display: inline-block;
    width: initial;
  }

  .button {
    line-height: 36px;
    border-radius: 5px;
    height: 36px;
    background: white;
    border-color: #f1f1f1;
    color: #eb5424;
  }
</style>
