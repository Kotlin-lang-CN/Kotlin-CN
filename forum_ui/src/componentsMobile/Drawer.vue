<template>
  <div class="drawer">
    <div class="cont">
      <div class="user">
        <app-avatar :avatar="loginInfo.username" :size="'middle'"></app-avatar>
        <span class="name">{{ loginInfo.username }}</span>
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
        loginInfo: LoginMgr.info(),
        msg: 'TODO:Manager'
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
      top: 80px;
      .name {
        color: white;
        line-height: 40px;
        font-size: 26px;
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
        display: block;
        padding: 20px;
        color: #c7c7c7;
        text-decoration: none;
        border-left: 8px transparent solid;
      }
      a:hover {
        color: white;
      }
      a.sel {
        border-left: 8px #2b75e1 solid;
        background-color: #2b3444;
      }

    }
    .foot {
      border-top: 1px white solid;
      color: #c7c7c7;
      position: absolute;
      line-height: 60px;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      text-align: center;

      .register, .login {
        display: inline-block;
        width: 50%;
      }
      .login {
        border-left: 1px white solid;
      }
      .logout {
        width: 100%;
      }
      div:hover {
        color: white;
      }
    }
  }
</style>
