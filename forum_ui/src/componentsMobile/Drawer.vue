<template>
  <div class="drawer">
    <div class="cont">
      <div class="user">
        <app-avatar :avatar="loginInfo.username" :size="'middle'"></app-avatar>
        <span class="name" v-if="loginInfo.isLogin">{{ loginInfo.username }}</span>
        <span class="name" v-if="!loginInfo.isLogin">未登录</span>
      </div>
      <ul>
        <li><a :href="urlHome" class="sel">首页</a></li>
        <li><a href="http://www.kotliner.cn/">社区</a></li>
        <li><a :href="urlEdit">发布新话题</a></li>
      </ul>
      <div class="foot">
        <div v-on:click="register" v-if="!loginInfo.isLogin" class="register">注册</div>
        <div v-on:click="login" v-if="!loginInfo.isLogin" class="login">登录</div>
        <div v-on:click="logout" v-if="loginInfo.isLogin" class="logout">注销登录</div>
      </div>
    </div>
  </div>
</template>
<script>
  import LoginMgr from '../assets/js/LoginMgr.js';
  import Config from "../assets/js/Config.js";
  import Avatar from "../components/Avatar.vue";
  import Event from '../assets/js/Event.js';

  export default {
    components: {
      "app-avatar": Avatar
    },
    data: function () {
      return {
        urlHome: Config.UI.root,
        urlEdit: Config.UI.edit,
        loginInfo: LoginMgr.info()
      }
    },
    methods: {
      login() {
        Event.emit('request_login');
      },
      register() {
        Event.emit('request_register')
      },
      logout(){
        LoginMgr.logout();
      }
    }
  }
</script>
<style scoped lang="less">
  .cont {
    .user {
      position: absolute;
      top: 66px;
      left: 0;
      margin-left: 58px;
      .name {
        line-height: 44px;
        font-size: .28rem;
        color: #999;
        display: inline-block;
        margin-left: 12px;
      }
    }
    ul {
      position: absolute;
      top: 140px;
      left: 0;
      right: 0;
      list-style-type: none;
      a {
        line-height: 60px;
        height: 60px;
        padding-left: 50px;
        display: block;
        color: white;
        text-decoration: none;
        border-left: 8px transparent solid;
      }
      a.sel {
        border-left: 8px #2b75e1 solid;
        background-color: #2b3444;
      }

    }
    .foot {
      border-top: 1px #58647a solid;
      color: white;
      position: absolute;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      text-align: center;
      font-size: .3rem;

      .register, .login {
        display: inline-block;
        width: 50%;
      }
      .login {
        border-left: 1px #58647a solid;
      }
      .logout {
        width: 100%;
      }
      div{
        height: 50px;
        line-height: 50px;
        margin:5px 0;
      }
    }
  }
</style>
