<template>
  <div class="header" v-show="moduleShow">
    <div class="nav-bar" v-bind:class="{ 'not-top': !top}">
      <div class="nav-content">
        <a :href="urlRoot" class="menu-header">
          <i class="logo"></i><span><b>Kotlin</b> CHINA</span>
        </a>
        <div class="menu-main"><a href="//www.kotliner.cn" target="_blank" title="社区">社区</a></div>
        <div class="menu-main"><a href="//www.kotlincn.net" target="_blank" title="中文站">中文站</a></div>

        <div class="menu-user menu-right" v-if="!loginInfo.isLogin">
          <div class="btn">
            <span><button v-on:click="register">注册</button></span>
          </div>
          <div class="btn">
            <span>
              <span>登录</span>
            </span>
            <ul>
              <li>
                <button v-on:click="loginWithGithub">GitHub登录</button>
              </li>
              <li>
                <button v-on:click="login">账号登录</button>
              </li>
            </ul>
          </div>
        </div>
        <div class="menu-user menu-right" v-if="loginInfo.isLogin">
          <div class="btn">
            <span><i class="add-icon"></i></span>
            <div class="sub-menu"><a :href="urlEdit">发布新话题</a></div>
          </div>
          <div class="btn">
            <span>
              <app-avatar :avatar="loginInfo.username" :size="'small'"></app-avatar>
              <i class="choice-icon"></i>
            </span>
            <div class="sub-menu">
              <button v-on:click="logout">退出登录</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="isLoading" class="github-auth-loading">
      <h4>获取第三方登录信息...</h4>
      <vue-loading type="spin" color="#d9544e" :size="{ width: '50px', height: '50px' }"></vue-loading>
    </div>
  </div>
</template>

<script>
  import LoginMgr from '../assets/js/LoginMgr.js';
  import Event from '../assets/js/Event.js';
  import Config from '../assets/js/Config.js';
  import Avatar from "./Avatar.vue";
  import Net from '../assets/js/Net.js';
  import Cookie from 'js-cookie';
  import vueLoading from 'vue-loading-template'

  function getParam(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)')
        .exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null;
  }

  export default {
    components: {
      "app-avatar": Avatar,
      'vue-loading': vueLoading
    },
    data: function () {
      return {
        urlRoot: Config.UI.root,
        urlTopics: Config.UI.topics,
        urlRegister: Config.UI.register,
        urlAccount: Config.UI.account,
        urlEdit: Config.UI.edit,
        urlLogin: Config.UI.login,
        loginInfo: LoginMgr.info(),
        moduleShow: true,
        top: true,
        isLoading: false,
      }
    },
    created: function () {
      Event.on("fullscreen", (on) => this.moduleShow = !on);
      Event.on("page-scroll", (top) => this.top = top);
      const code = getParam('code');
      const state = getParam('state');
      if (code && state && code !== null && state !== null) {
        this.isLoading = true;
        this.handleGithubAuth(code, state)
      }
    },
    methods: {
      login() {
        LoginMgr.require()
      },
      loginWithGithub() {
        Net.get({url: Config.URL.github.createState}, (resp) => {
          window.location.href = 'http://github.com/login/oauth/authorize' +
            '?state=' + resp.state +
            '&client_id=' + Config.OAuth.github.clientId +
            '&redirect_url=' + window.location.host +
            '&scope=' + Config.OAuth.github.scope
        })
      },
      handleGithubAuth(code, state) {
        Net.post({
          url: Config.URL.github.auth,
          condition: {
            code: code,
            state: state
          }
        }, (resp) => {
          window.console.log(resp);
          if (resp.need_create_account) {
            Cookie.set('X-App-Github', resp.github_token);
            this.isLoading = false;
            LoginMgr.require(/*login already*/() => window.location.href = '/', "request_register")
          } else {
            LoginMgr.login({
              username: resp.username,
              email: resp.email,
              uid: resp.uid,
              token: resp.token,
              role: resp.role,
            });
            window.location.href = '/'
          }
        }, () => window.location.href = '/');
      },
      register() {
        Event.emit('request_register')
      },
      logout(){
        LoginMgr.logout();
      }
    },
  }
</script>

<style scoped>
  .github-auth-loading {
    margin-left: calc(50% - 200px);
  }
</style>

<style scoped lang="less">
  .header {
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
        width: 1120px;
        height: 86px;
        margin: auto;
        > div {
          display: inline-block;
        }
        .menu-main {
          height: 38px;
          line-height: 38px;
          text-align: center;
          margin-left: 20px;
          padding-top: 26px;
          padding-bottom: 24px;
          vertical-align: top;
          font-size: 18px;
        }
        a.menu-header {
          display: inline-block;
          margin-top: 26px;
          padding-bottom: 14px;
          color: #6b6b6b;
          font-weight: bolder;
          font-size: 20px;
          line-height: 38px;
          height: 38px;
          .logo {
            display: inline-block;
            width: 20px;
            height: 22.5px;
            background: url(../assets/img/logo_k.png) no-repeat;
            background-size: 100% 100%;
            vertical-align: top;
            margin-top: 4px;
            margin-right: 4px;
          }
          b {
            color: #2b75e1;
          }
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
            button {
              height: 66px;
              color: #333;
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
              line-height: 50px;
              color: #333;
              height: 62px;
              width: 182px;
            }
          }
          .btn .sub-menu:hover {
            background-color: #f8fbff;
            a {
              color: #2572e5;
            }
            button {
              color: #2572e5;
            }
          }
          .btn ul {
            display: none;
          }
          .btn:hover ul {
            position: absolute;
            background-color: white;
            right: -1px;
            width: 182px;
            height: 124px;
            display: block;
            box-shadow: 0 0 10px #ccc;

            button {
              color: #333;
              line-height: 62px;
              height: 62px;
              width: 100%;
              padding: 0;
              margin: 0;
            }
            button:hover{
              background-color: #f8fbff;
              color: #2572e5;
            }
          }
        }
        .menu-right {
          float: right;
          font-size: 16px;
          position: relative;
        }
      }
    }
    .github-auth-loading {
      z-index: 3;
      position: absolute;
      width: 400px;
      margin-top: 200px;
      padding: 20px;
      box-sizing: border-box;
      background: white;
      border: 1px #f1f1f1 solid;
      box-shadow: 0 0 3px #2572e5;
      text-align: center;
    }
  }
</style>
