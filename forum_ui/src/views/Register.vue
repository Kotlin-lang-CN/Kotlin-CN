<template>
  <div class="register">
    <div class="list-head">注册新用户</div>
    <div class="form">
      <div>
        <input v-model="userName" type="text" name="user" placeholder="用户名">
      </div>
      <div>
        <input v-model="userEmail" type="email" name="email" placeholder="邮箱">
      </div>
      <div>
        <input v-model="userPassword" type="password" name="password" placeholder="密码">
      </div>
      <div>
        <input v-model="userPassword1" type="password" name="password_confirm" placeholder="确认密码">
      </div>
      <div class="button" v-on:click="register">提交注册</div>
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
        msg: Config.URL.account.register,
        userName: '',
        userEmail: '',
        userPassword: '',
        userPassword1: '',
        loginUser: '',
        loginPass: ''
      }
    },
    methods: {
      register() {
        let msg = '';
        if (!Util.isValidName(this.userName)) {
          msg = '用户名：输入2-20个以字母开头、可带数字、“_”、“.”的字串 ';
        } else if (!Util.isValidEmail(this.userEmail)) {
          msg = '邮箱：不是有效的邮箱格式';
        } else if (!Util.isValidPass(this.userPassword)) {
          msg = '密码：至少六位密码';
        } else if (this.userPassword !== this.userPassword1) {
          msg = '密码：前后两个密码不一致';
        }
        if (msg.length > 0) {
          Event.emit("error", msg);
          return;
        }

        let request = {
          url: Config.URL.account.register,
          type: "POST",
          condition: {
            'username': this.userName,
            'password': this.userPassword,
            'email': this.userEmail
          }
        };
        Net.ajax(request, function (data) {
          LoginMgr.login(data);
          location.href = Config.UI.account
        })
      }
    }
  };
</script>
<style scoped lang="less">
    .register {
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
