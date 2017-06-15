<template>
  <div class="bind_github_account">
    <div class="login" v-if="showMode === 1">
      <input v-model="nameInput" type="text" name="user" placeholder="用户名／邮箱"/> <br>
      <input v-model="passwordInput" type="password" name="password" placeholder="密码"/>
      <div>
        <a h="javascript:void(0);" v-on:click="switchMode">我还没有账号</a>
        <a href="javascript:void(0);" v-on:click="login">绑定账号</a>
      </div>
    </div>
    <div class="register" v-if="showMode === 2">
      <input v-model="emailInput" type="text" name="user" placeholder="邮箱"/> <br>
      <input v-model="nameInput" type="text" name="user" placeholder="用户名"/> <br>
      <input v-model="passwordInput" type="password" name="password" placeholder="密码"/><br>
      <input v-model="passwordRepeatInput" v-if="passwordInput.length >=8"
             type="password" name="password" placeholder="再次输入密码"/>
      <div>
        <a href="javascript:void(0);" v-on:click="switchMode">已有账号</a>
        <a href="javascript:void(0);" v-on:click="register">注册并绑定</a>
      </div>
    </div>
  </div>
</template>
<script>
  import Config from "../assets/js/Config.js";
  import Event from "../assets/js/Event.js";
  import Net from "../assets/js/Net.js";
  import LoginMgr from '../assets/js/LoginMgr';
  import Utils from '../assets/js/Util';

  export default {
    data () {
      return {
        showMode: 2,//0-none, 1-login, 2-register
        emailInput: '',
        nameInput: '',
        passwordInput: '',
        passwordRepeatInput: '',
        loginAlready: false,
      }
    },
    created() {
      Event.on('request_login', (loginAlready) => {
        this.loginAlready = loginAlready;
        this.showMode = 1
      });
      Event.on('request_register', (loginAlready) => {
        this.loginAlready = loginAlready;
        this.showMode = 2
      });
    },
    methods: {
      switchMode(){
        this.showMode = this.showMode === 1 ? 2 : 1
      },
      login() {
        if (this.nameInput.trim().length < 2) {
          Event.emit("error", "请输入正确的用户名");
        } else if (this.passwordInput.length < 8) {
          Event.emit("error", '密码错误');
        } else {
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
        }
      },
      register() {
        if (!Utils.isValidEmail(this.emailInput)) {
          Event.emit('error', "请输入正确的邮箱")
        } else if (this.nameInput.trim().length < 2) {
          Event.emit("error", "用户名过短");
        } else if (this.passwordInput.length < 8) {
          Event.emit("error", '密码需要8位或以上，仅限大小写与数字');
        } else if (this.passwordRepeatInput !== this.passwordInput) {
          Event.emit('error', "两次输入的密码不一致!")
        } else {
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
              email: email
            });
            this.hide()
          })
        }
      },
      hide() {
        this.showMode = 0;
        let loginAlready = this.loginAlready;
        if (loginAlready) {
          this.loginAlready = false;
          this.loginAlready(LoginMgr.info());
        }
      }
    }
  }
</script>
<style scoped lang="less">

</style>
