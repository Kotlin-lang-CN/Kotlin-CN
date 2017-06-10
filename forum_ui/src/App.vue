<template>
  <div>
    <app-header></app-header>
    <div id="app">
      <router-view></router-view>
    </div>
    <app-foot></app-foot>
    <app-login></app-login>
  </div>
</template>

<script>
  import Event from './assets/js/Event.js';
  import Header from './components/Headers.vue';
  import Footer from './components/Footers.vue';
  import Login from './components/Login.vue';

  export default {
    name: 'app',
    components: {
      'app-header': Header,
      'app-foot': Footer,
      'app-login': Login
    },
    mounted() {
      Event.on("error", (err) => {
        layer.msg(err)
      });
    },
  }
</script>

<style lang="less">
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;

    > div {
      max-width: 1120px;
      margin: 1px auto;
    }
  }

  a {
    text-decoration: none;
    color: #333;
    font-weight: normal;
  }

  body {
    margin: 0;
    font: 16px "Lucida Grande", Helvetica, Arial, sans-serif;
  }

  li {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  textarea {
    outline: none;
    border-radius: 8px;

  }

  .button, button {
    line-height: 36px;
    height: 36px;
    color: #2572e5;
    padding: 6px 12px;
    background: transparent;
    outline: none;
    border: none;
    font-size: 16px;
  }
</style>
