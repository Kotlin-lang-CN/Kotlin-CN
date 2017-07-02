<template>
  <div class="app-root">
    <header id="root-header">
      <app-header></app-header>
    </header>

    <div ref="page" id="page-content">
      <div class="app">
        <slot></slot>
      </div>
    </div>
    <footer id="root-footer" v-if="footer">
      <app-foot></app-foot>
    </footer>

    <app-login></app-login>
    <common-dialog></common-dialog>
  </div>
</template>

<script>
  import Event from '../assets/js/Event.js';
  import Header from '../components/Headers.vue';
  import Footer from '../components/Footers.vue';
  import Login from '../components/Login.vue';
  import Drawer from '../componentsMobile/Drawer.vue';
  import Dialog from '../components/Dialog.vue';

  export default {
    name: 'app',
    components: {
      'app-header': Header,
      'app-foot': Footer,
      'app-login': Login,
      'app-drawer': Drawer,
      'common-dialog': Dialog
    },
    props: {
      'footer': true
    },
    data(){
      return {isTop: true}
    },
    mounted() {
      Event.on("error", (err) => layer.msg(err));
    }
  }
</script>

<style lang="less" scoped>
  .app-root {
    position: relative;
  }

  #root-header {
    z-index: 2;
    position: fixed;
    left: 0;
    right: 0;
    top: 0;
  }

  #page-content {
    z-index: 1;
    position: fixed;
    top: 86px;
    bottom: 20px;
    left: 0;
    right: 0;
    overflow-y: auto;
  }

  #root-footer {
    z-index: 2;
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
  }

</style>

<!--global style-->
<style>
  body {
    margin: 0;
    font: 16px "Avenir", Helvetica, Arial, sans-serif;
  }

  .app {
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    margin: auto;
    padding: 0 16px;
    color: #2c3e50;
  }

  a {
    text-decoration: none;
    font-weight: normal;
    color: #333;
  }

  ul {
    margin: 0;
    padding: 0;
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
    cursor: pointer;
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
