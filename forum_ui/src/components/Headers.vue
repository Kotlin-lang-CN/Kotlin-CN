<template>
  <div class="header" v-show="moduleShow">
    <div class="nav-bar">
      <div class="nav-content">
        <div class="menu-header"><a :href="urlRoot" title=""><b>Kotlin</b> China</a></div>
        <div class="menu-main"><a :href="urlTopics" title="社区">社区</a></div>
        <div class="menu-authen menu-right" v-if="strUserName ===''">
          <a :href="urlRegister" title="注册">注册</a>
          <a :href="urlLogin" title="登录">登录</a>
        </div>
        <div class="menu-user menu-right" v-if="strUserName !==''">
          <a :href="urlAccount" title="登录">你好！{{ strUserName }}</a>
        </div>
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
        strUserName: LoginMgr.username,
        moduleShow: true
      }
    },
    created: function () {
      Event.on("error", (msg) => {
        this.strToast = msg;
        setTimeout(() => {
          this.strToast = '';
        }, 3000);
      });
      Event.on("login", () => {
        this.strUserName = LoginMgr.username
      });
      Event.on("fullscreen", (on) => {
        this.moduleShow = !on;
      })
    },
    methods: {}
  }
</script>

<style scoped lang="less">
  .header {
    min-width: 320px;

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
      font-size: 20px;
      .nav-content {
        max-width: 1120px;
        padding-top: 24px;
        padding-bottom: 24px;
        margin: auto;
        > div {
          display: inline-block;
        }
        .menu-header ,.menu-main{
          height: 38px;
          line-height: 38px;
          text-align: center;
          margin-right: 20px;
        }
        .menu-header b {
          color: #2572e5;
          font-weight: normal;
        }
        .menu-right {
          float: right;
          font-size: 16px;
          >a{
            display: inline-block;
            width: 96px;
            height: 38px;
            line-height: 38px;
            text-align: center;
          }
          >a:nth-child(2){
            border: 1px #2572e5 solid;
            border-radius: 2px;
            color: #2572e5;
          }
        }
      }
    }
  }


</style>
