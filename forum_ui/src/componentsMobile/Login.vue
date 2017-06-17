<template>
  <div class="dialog" v-if="showMode !==0">
    <i class="close" v-on:click="showMode = 0"></i>

    <div class="cont">
      <div class="logo"><b>Kotlin</b> CHINA</div>
      <div class="switcher">
        <div v-bind:class="{'sel' : showMode === 2 || showMode === 4 ,'nor' : showMode === 1 || showMode === 3} "
             v-on:click="switchMode">注册
        </div>
        <div v-bind:class="{'sel' : showMode === 1 || showMode === 3 ,'nor' : showMode === 2 || showMode === 4} "
             v-on:click="switchMode">登录
        </div>
      </div>
      <div class="login" v-if="showMode === 1 || showMode === 3">
        <p v-if="showMode === 3">绑定GitHub账号到一个已创建的账号</p>
        <input v-model="nameInput" type="text" name="user" placeholder="用户名或邮箱"/>
        <span v-if="error && error.key == 'login-name'" class="error">{{ error.value }}</span>
        <input v-model="passwordInput" type="password" name="password" placeholder="用户密码"/>
        <span v-if="error && error.key == 'login-password'" class="error">{{ error.value }}</span>
        <button v-on:click="login" class="btn" v-if="showMode === 1">登录</button>
        <button v-on:click="login" class="btn" v-if="showMode === 3">确认基本信息</button>
      </div>
      <div class="register" v-if="showMode === 2 || showMode === 4">
        <p v-if="showMode === 4">使用GitHub账号首次登录需要完善以下基本信息</p>
        <input v-model="emailInput" type="text" name="user" placeholder="邮箱"/>
        <span v-if="error && error.key == 'register-email'" class="error">{{ error.value }}</span>
        <input v-model="nameInput" type="text" name="user" placeholder="用户名"/>
        <span v-if="error && error.key == 'register-name'" class="error">{{ error.value }}</span>
        <input v-model="passwordInput" type="password" name="password" placeholder="密码"/>
        <span v-if="error && error.key == 'register-password'" class="error">{{ error.value }}</span>
        <input v-model="passwordRepeatInput" v-if="passwordInput.length >=8"
               type="password" name="password" placeholder="再次输入密码"/>
        <span v-if="error && error.key == 'register-password-repeat'" class="error">{{ error.value }}</span>
        <button v-on:click="register" class="btn" v-if="showMode === 2">注册并登录</button>
        <button v-on:click="register" class="btn" v-if="showMode === 4">创建并绑定</button>
      </div>
    </div>

  </div>
</template>

<style scoped lang="less">
  .dialog {
    z-index: 5;
    position: absolute;
    background-color: white;
    top: 0;
    bottom: 0;
    right: 0;
    left: 0;
  }

  .cont {
    text-align: center;
    width: 292px;
    line-height: 44px;
    margin: 8% auto 0 auto;
    color: #999;
  }

  .logo {
    font-size: 25px;
    font-weight: bolder;
    color: #6f6f6f;
    margin-bottom: 30px;

    b {
      color: #2572e5;
    }
  }

  .close {
    width: 30px;
    height: 30px;
    display: inline-block;
    border: 1px #ccc solid;
    margin-left: 15px;
    margin-top: 15px;
  }

  .switcher {
    border-bottom: 1px #f1f1f1 solid;
    margin-bottom: 7px;
    > div {
      display: inline-block;
      width: 49%;
    }
    .sel {
      color: #2572e5;
      border-bottom: 3px #2572e5 solid;
    }
    .nor {
      border-bottom: 3px transparent solid;
    }
  }

  input {
    box-sizing: border-box;
    display: block;
    margin-top: 5px;
    border: 1px #ddd solid;
    border-radius: 3px;
    outline: none;
    width: 100%;
    height: 44px;
    font-size: 13px;
    padding: 0 10px;
  }

  input:hover {
    -webkit-box-shadow: 0 0 3px #2e8ded;
  }

  input::-webkit-input-placeholder {
    color: #999;
  }

  input::-moz-placeholder {
    color: #999;
  }

  input:-webkit-autofill {
    -webkit-box-shadow: 0 0 0 1000px #fff inset;
  }

  .btn {
    width: 100%;
    margin-top: 15px;
    display: block;
    height: 44px;
    line-height: 33px;
    background-color: #2572e5;
    color: white;
    font-size: 15px;
    border-radius: 2px;
  }
  .btn:active {
    background-color: #1c4ecf;
  }
  .error {
    text-align: left;
    display: block;
    font-size: 10px;
    color: #fb6666;
    width: 100%;
    height: 25px;
    line-height: 25px;
  }

</style>

<script>
  import Config from "../assets/js/Config.js";
  import Event from "../assets/js/Event.js";
  import Net from "../assets/js/Net.js";
  import LoginMgr from '../assets/js/LoginMgr';
  import Utils from '../assets/js/Util';
  import Cookie from 'js-cookie';

  export default {
    data () {
      return {
        showMode: 0,//0-none, 1-login, 2-register, 3-GitHub-login, 4-GitHub-register
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
        const githubToken = Cookie.get('X-App-Github');
        this.loginAlready = loginAlready;
        this.showMode = githubToken ? 3 : 1;
      });
      Event.on('request_register', (loginAlready) => {
        const githubToken = Cookie.get('X-App-Github');
        this.loginAlready = loginAlready;
        this.showMode = githubToken ? 4 : 2;
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
        let githubToken = Cookie.get('X-App-Github');
        if (!githubToken) {
          this.showMode = this.showMode === 1 ? 2 : 1
        } else {
          this.showMode = this.showMode === 3 ? 4 : 3
        }
      },
      forget(){

      },
      notifyError() {
        if (this.showMode === 1 || this.showMode === 3) {
          if (this.nameInput.trim().length < 2) {
            this.error = {key: 'login-name', value: '请输入正确的用户名'}
          } else if (this.passwordInput.length < 8) {
            this.error = {key: 'login-password', value: '请输入正确的密码'}
          } else {
            this.error = false
          }
        } else if (this.showMode === 2 || this.showMode === 4) {
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
        let githubToken = Cookie.get('X-App-Github');
        Net.post({
          url: Config.URL.account.login,
          condition: githubToken ? {
            'login_name': this.nameInput.trim(),
            'password': this.passwordInput,
            "github_token": githubToken,
          } : {
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
        let githubToken = Cookie.get('X-App-Github');
        let name = this.nameInput;
        let email = this.emailInput;
        Net.post({
          url: Config.URL.account.register,
          condition: githubToken ? {
            "username": this.nameInput,
            "password": this.passwordInput,
            "email": this.emailInput,
            "github_token": githubToken
          } : {
            "username": this.nameInput,
            "password": this.passwordInput,
            "email": this.emailInput,
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
        Cookie.remove('X-App-GitHub');
        this.showMode = 0;
        const info = LoginMgr.info();
        if (this.loginAlready && info.isLogin) {
          this.loginAlready(info);
        }
        this.loginAlready = undefined;
        this.nameInput = '';
        this.emailInput = '';
        this.passwordInput = '';
        this.passwordRepeatInput = '';
        this.error = false
      }
    }
  }
</script>
