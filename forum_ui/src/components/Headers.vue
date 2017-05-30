<template>
  <div class="header">
    <div class="nav-bar">
      <div class="menu-header"><a :href="urlRoot" title=""><b>kotlin</b> China</a></div>
      <div class="menu-main">
        <ul>
          <li><a :href="urlTopics" title="社区">社区</a></li>
        </ul>
      </div>
      <div class="menu-authen menu-right" v-if="strUserName ===''">
        <a :href="urlRegister" title="注册">注册</a>
        <a :href="urlLogin" title="登录">登录</a>
      </div>
      <div class="menu-user menu-right" v-if="strUserName !==''">
        <a :href="urlAccount" title="登录">你好！{{ strUserName }}</a>
      </div>
    </div>
    <div class="toast" v-if="strToast !== ''">{{ strToast }} </div>
  </div>
</template>

<script>
  import LoginMgr from '../assets/js/LoginMgr.js';
  import Event from '../assets/js/Event.js';
  import Config from '../assets/js/Config';
  export default {
    data: function () {
      return {
        urlRoot: Config.UI.root,
        urlTopics: Config.UI.topics,
        urlRegister: Config.UI.register,
        urlAccount: Config.UI.account,
        urlLogin: Config.UI.login,
        strToast: '',
        strUserName: LoginMgr.username
      }
    },
    created: function () {
      Event.on("error", this.toast);
      Event.on("login",this.login);
    },
    methods: {
      toast(msg){
        this.strToast = msg;
        setTimeout(this.hideToast, 3000);
      },
      hideToast(){
        this.strToast = '';
      },
      login(){
        this.strUserName = LoginMgr.username
      }
    }
  }
</script>

<style scoped lang="less">
  .header{
    min-width: 320px;
  }

  .toast {
    background: lightcoral;
    color: white;
    font-size: 14px;
    line-height: 20px;
    padding: 8px;
    text-align: center;
  }

  .nav-bar {
    background: white;
    padding: 0 16px;
    border-bottom: 1px #f1f1f1 solid;
    > div {
      display: inline-block;
    }
    .menu-header b {
      color: #eb5424;
      font-weight: normal;
    }
    .menu-right {
      float: right;
      margin: 16px 16px;
    }
  }
</style>
