<template>
  <div class="account">
    <div>
      Account info listed here
    </div>
    <ul>
      <li v-for="(value, key) in object">
        {{ key }} : {{ value }}
      </li>
    </ul>
    <div class="button" v-on:click="logout">登出</div>
  </div>
</template>
<script>
  import Event from '../assets/js/Event.js';
  import Util from '../assets/js/Util.js';
  import Config from '../assets/js/Config';
  import Net from '../assets/js/Net.js';
  import LoginMgr from '../assets/js/LoginMgr.js';
  import AppLayout from '../App.vue'

  export default {
    data(){
      return {
        msg: Config.URL.account.register,
        object: {
          Name: LoginMgr.info().username,
          Email: LoginMgr.info().email
        }
      }
    },
    components: {
      AppLayout
    },
    methods: {
      logout(){
        LoginMgr.logout();
        location.href = Config.UI.root;
      }
    }
  };
</script>
<style scoped lang="less">
  .button {
    max-width: 300px;
    margin: auto;
    line-height: 36px;
    border-radius: 5px;
    height: 36px;
    background: white;
    border-color: #f1f1f1;
    color: #eb5424;
  }
</style>
