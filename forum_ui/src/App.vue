<template>
  <div>
    <app-header></app-header>
    <div id="app">
      <router-view></router-view>
    </div>
    <app-foot></app-foot>
  </div>
</template>
<script>
  import Event from './assets/js/Event.js';
  import Header from './components/Headers.vue';
  import Footer from './components/Footers.vue';
  export default {
    name: 'app',
    components: {
      'app-header': Header,
      'app-foot': Footer
    }
  }
</script>
<style lang="less">
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin: 1px auto;
    max-width: 1000px;

  }

  a {
    text-decoration: none;
    color: #2c3e50;
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

  textarea{
    outline: none;
    border-radius: 8px;
    border: 1px #f1f1f1 solid;
  }
</style>
