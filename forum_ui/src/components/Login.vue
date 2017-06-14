<template>
  <div class="dialog" v-if="showMode !==0">
    <div class="bg" v-on:click="hide"></div>
    <div class="cont">
      <div class="header"><span class="blue">Kotlin</span>China</div>
      <div class="login" v-if="showMode === 1">
        <input v-model="nameInput" type="text" name="user" placeholder="用户名或邮箱"/>
        <span v-if="error && error.key == 'login-name'" class="error">{{ error.value }}</span>
        <input v-model="passwordInput" type="password" name="password" placeholder="用户密码"/>
        <span v-if="error && error.key == 'login-password'" class="error">{{ error.value }}</span>
        <button v-on:click="login" class="big-btn">登录</button>
        <div class="small-btn">
          <button v-on:click="forget">忘记密码</button>
          <button v-on:click="switchMode">我还没有账号</button>
        </div>
      </div>
      <div class="register" v-if="showMode === 2">
        <input v-model="emailInput" type="text" name="user" placeholder="邮箱"/><br>
        <span v-if="error && error.key == 'register-email'" class="error">{{ error.value }}</span>
        <input v-model="nameInput" type="text" name="user" placeholder="用户名"/><br>
        <span v-if="error && error.key == 'register-name'" class="error">{{ error.value }}</span>
        <input v-model="passwordInput" type="password" name="password" placeholder="密码"/><br>
        <span v-if="error && error.key == 'register-password'" class="error">{{ error.value }}</span>
        <input v-model="passwordRepeatInput" v-if="passwordInput.length >=8"
               type="password" name="password" placeholder="再次输入密码"/>
        <span v-if="error && error.key == 'register-password-repeat'" class="error">{{ error.value }}</span>
        <button v-on:click="register" class="big-btn">注册</button>
        <div class="small-btn">
          <button v-on:click="switchMode">已有账号，去登陆</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .dialog .cont {
    margin-left: calc(50% - 253px);
  }
</style>

<style scoped lang="less">
  .dialog {
    position: fixed;
    z-index: 3;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    .bg {
      position: absolute;
      background: #000000;
      filter: alpha(opacity=50);
      -moz-opacity: 0.5;
      opacity: 0.5;
      width: 100%;
      height: 100%;
    }
    .cont {
      position: absolute;
      width: 506px;
      margin-top: 200px;
      padding: 40px;
      box-sizing: border-box;
      background: white;
      border: 1px #f1f1f1 solid;
      box-shadow: 0 0 3px #2572e5;
      .header {
        line-height: 60px;
        text-align: center;
        .blue {
          display: inline-block;
          padding-right: 4px;
          color: #2e8ded;
        }
      }
      input {
        display: block;
        margin: 0 auto 20px auto;
        border: 1px #ccc solid;
        outline: none;
        width: 100%;
        height: 40px;
      }
      input:hover {
        -webkit-box-shadow: 0 0 3px #2e8ded;
      }
      input:-webkit-autofill {
        -webkit-box-shadow: 0 0 0 1000px #fff inset;
      }
      .error {
        display: block;
        font-size: .8em;
        margin: -15px auto -15px auto;
        color: #f00;
        width: 100%;
        height: 40px;
      }
      .big-btn {
        margin: auto;
        display: block;
        width: 100%;
        height: 40px;
        background-color: #2572e5;
        color: white;
      }
      .small-btn {
        margin: 4px auto 40px auto;
        width: 100%;
        button:nth-child(2) {
          float: right;
        }
      }
    }
  }
</style>

<script>
  import Config from "../assets/js/Config.js";
  import Event from "../assets/js/Event.js";
  import Net from "../assets/js/Net.js";
  import LoginMgr from '../assets/js/LoginMgr';
  import Utils from '../assets/js/Util';

  export default {
    data () {
      return {
        showMode: 0,//0-none, 1-login, 2-register
        emailInput: '',
        nameInput: '',
        passwordInput: '',
        passwordRepeatInput: '',
        loginAlready: false,
        error: false
      }
    },
    created() {
      Event.on('request_login', (loginAlready) => {
        this.loginAlready = loginAlready;
        this.showMode = 1;
      });
      Event.on('request_register', (loginAlready) => {
        this.loginAlready = loginAlready;
        this.showMode = 2;
      });
    },
    watch: {
      showMode() {
        this.notifyError()
      },
      emailInput() {
        this.notifyError()
      },
      nameInput() {
        this.notifyError()
      },
      passwordInput() {
        this.notifyError()
      },
      passwordRepeatInput() {
        this.notifyError()
      }
    },
    methods: {
      switchMode(){
        this.showMode = this.showMode === 1 ? 2 : 1
      },
      forget(){

      },
      notifyError() {
        if (this.showMode === 1) {
          if (this.nameInput.trim().length < 2) {
            this.error = {key: 'login-name', value: '请输入正确的用户名'}
          } else if (this.passwordInput.length < 8) {
            this.error = {key: 'login-password', value: '请输入正确的密码'}
          } else {
            this.error = false
          }
        } else if (this.showMode === 2) {
          if (!Utils.isValidEmail(this.emailInput)) {
            this.error = {key: 'register-email', value: '请输入正确的邮箱'}
          } else if (this.nameInput.trim().length < 2) {
            this.error = {key: 'register-name', value: '用户名过短'}
          } else if (this.passwordInput.length < 8) {
            this.error = {key: 'register-password', value: '密码需要8位或以上，仅限大小写与数字'}
          } else if (this.passwordRepeatInput !== this.passwordInput) {
            this.error = {key: 'register-password-repeat', value: '两次输入的密码不一致'}
          } else {
            this.error = false
          }
        }
      },
      login() {
        if (this.error) return;
        Net.post({
          url: Config.URL.account.login,
          condition: {
            'login_name': this.nameInput.trim(),
            'password': this.passwordInput,
          }
        }, (resp) => {
          LoginMgr.login(resp);
          this.hide()
        })
      },
      register() {
        if (this.error) return;
        let name = this.nameInput;
        let email = this.emailInput;
        Net.post({
          url: Config.URL.account.register,
          condition: {
            "username": this.nameInput,
            "password": this.passwordInput,
            "email": this.emailInput
          }
        }, (resp) => {
          LoginMgr.login({
            uid: resp.uid,
            token: resp.token,
            username: name,
            email: email,
            role: 0,
          });
          this.hide()
        })
      },
      hide() {
        this.showMode = 0;
        let loginAlready = this.loginAlready;
        if (loginAlready) {
          this.loginAlready = false;
          let info = LoginMgr.info();
          if (info) loginAlready(info);
        }
        this.nameInput = '';
        this.emailInput = '';
        this.passwordInput = '';
        this.passwordRepeatInput = '';
        this.error = false
      }
    }
  }
</script>
