<template>
  <div class="dialog" v-if="show">
    <div class="bg" v-on:click="doCancel"></div>
    <div class="dialog-content">
      <img class="logo" v-bind:src="user.logo"/>
      <h2>{{user.username}}</h2>
      <a :href="'mailto:'+user.email"><i class="email"></i><span>{{user.email}}</span></a>
    </div>
  </div>
</template>

<script>
  import Event from '../assets/js/Event'

  export default {
    data() {
      return {
        show: false,
        user: {
          uid: '',
          logo: '',
          email: '',
          username: '',
        }
      }
    },
    created() {
      Event.on('name-card', (user) => {
        this.user = user;
        this.show = true;
      })
    },
    methods: {
      doCancel() {
        this.show = false
      },
    }
  }
</script>

<style scoped>
  .dialog .dialog-content {
    margin-left: calc(50% - 200px);
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

    .dialog-content {
      position: absolute;
      width: 400px;
      margin-top: 200px;
      padding: 40px;
      box-sizing: border-box;
      background: white;
      border: 1px #f1f1f1 solid;
      box-shadow: 0 0 3px #2572e5;

      i.email {
        display: inline-block;
        width: 30px;
        height: 25px;
        background: url(../assets/img/email.png) no-repeat;
      }
      span {
        padding-bottom: 5px;
      }
    ;
    }
  }
</style>
