<template>
  <div class="header" v-show="moduleShow">
    <div class="nav-bar" v-bind:class="{ 'not-top': !top}">
      <div class="nav-content">
        <div class="menu-header"><a :href="urlRoot" title=""><b>Kotlin</b> China</a></div>
        <div class="menu-main"><a :href="urlTopics" title="社区">社区</a></div>

        <div class="menu-authen menu-right" v-if="strUserName ===''">
          <a v-on:click="register" title="注册">注册</a>
          <a v-on:click="login" title="登录">登录</a>
        </div>

        <div class="menu-user menu-right" v-if="strUserName !==''">
          <div class="btn"><span><i class="add-icon"></i></span>
            <div class="sub-menu"><a :href="urlEdit">发布新话题</a></div>
          </div>
          <div class="btn"><span><app-avatar :avatar="strUserName" :size="'small'"></app-avatar><i
            class="choice-icon"></i></span>
            <div class="sub-menu">
              <button v-on:click="logout">退出登录</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import LoginMgr from '../assets/js/LoginMgr.js';
  import Event from '../assets/js/Event.js';
  import Config from '../assets/js/Config.js';
  import Avatar from "./Avatar.vue";

  export default {
    components: {
      "app-avatar": Avatar
    },
    data: function () {
      return {
        urlRoot: Config.UI.root,
        urlTopics: Config.UI.topics,
        urlRegister: Config.UI.register,
        urlAccount: Config.UI.account,
        urlEdit: Config.UI.edit,
        urlLogin: Config.UI.login,
        strUserName: LoginMgr.check((it) => it.username, () => ''),
        moduleShow: true,
        top: true
      }
    },
    created: function () {
      Event.on("login", () => {
        this.strUserName = LoginMgr.check((it) => it.username, () => '')
      });
      Event.on("fullscreen", (on) => {
        this.moduleShow = !on;
      });
      Event.on("page-scroll", (top) => {
        this.top = top;
      })
    },
    methods: {
      login() {
        Event.emit('request_login')
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
  .header {
    min-width: 320px;
    .not-top {
      -webkit-box-shadow: 0 0 10px #f1f1f1;
      -moz-box-shadow: 0 0 10px #f1f1f1;
      box-shadow: 0 0 10px #f1f1f1;
    }
    .nav-bar {
      background: white;
      padding: 0 16px;
      font-size: 20px;
      .nav-content {
        max-width: 1120px;
        min-height: 86px;
        margin: auto;
        > div {
          display: inline-block;
        }
        .menu-header, .menu-main {
          height: 38px;
          line-height: 38px;
          text-align: center;
          margin-right: 20px;
          padding-top: 24px;
          padding-bottom: 24px;
        }
        .menu-header b {
          color: #2572e5;
          font-weight: normal;
        }
        .menu-user {
          .add-icon {
            display: inline-block;
            width: 18px;
            height: 18px;
            background: url(../assets/img/add-icon.png) no-repeat;
          }
          .choice-icon {
            display: inline-block;
            width: 18px;
            height: 18px;
            vertical-align: bottom;
            margin-bottom: 12px;
            background: url(../assets/img/choice-icon.png) no-repeat;
          }
          .btn {
            position: relative;
            vertical-align: top;
            height: 86px;
            display: inline-block;
            text-align: center;
          }
          .btn > span {
            margin-right: auto;
            display: block;
            line-height: 86px;
            min-height: 86px;
            width: 78px;
            i {
              margin-top: 24px;
            }
          }
          .btn > span:hover {
            background-color: #f9f9f9;
          }
          .btn .sub-menu {
            display: none;
          }
          .btn:hover .sub-menu {
            position: absolute;
            background-color: white;
            right: -1px;
            width: 182px;
            height: 62px;
            display: block;
            box-shadow: 0 0 10px #ccc;
            a {
              display: block;
              line-height: 62px;
              color: #333;
            }
            button {
              display: block;
              line-height: 62px;
              color: #333;
              height: 62px;
              width: 182px;
            }
          }
        }
        .menu-authen {
          > a {
            margin: 24px 0;
            display: inline-block;
            width: 96px;
            height: 38px;
            line-height: 38px;
            text-align: center;
          }
          > a:nth-child(2) {
            border: 1px #2572e5 solid;
            border-radius: 2px;
            color: #2572e5;
          }
        }
        .menu-right {
          float: right;
          font-size: 16px;
          position: relative;
        }
      }
    }
  }

  @media screen and (max-width: 480px) {
    .header .nav-bar .nav-content .menu-main {
      display: none;
    }
  }
</style>
