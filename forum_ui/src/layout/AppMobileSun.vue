<template>
  <div class="app-root">
    <nav>
      <div v-on:click="back" class="back">
        <i class="icon-back"></i>返回
      </div>
    </nav>
    <div ref="page" class="page-content">
      <div class="app">
        <slot></slot>
      </div>
    </div>
    <app-login></app-login>
  </div>
</template>

<script>
  import Login from '../componentsMobile/Login.vue';
  export default {
    components: {
      'app-login': Login
    },
    methods: {
      back(){
        history.back();
      }
    }
  }
</script>

<style lang="less">
  html {
    font-size: 13.333vw;
  }

  body {
    margin: 0 !important;
    padding: 0;
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

  a {
    text-decoration: none;
    font-weight: normal;
    color: #333;
  }

  .app-root {
    > nav {
      border-bottom: 1px #f1f1f1 solid;
      line-height: 60px;
      height: 60px;
      color: #999;
      font-size: .28rem;
      background-color: white;
      position: fixed;
      left: 0;
      right: 0;
      padding: 0 15px;
      z-index: 2;

      .icon-back {
        vertical-align: top;
        margin-top: 15px;
        display: inline-block;
        width: 30px;
        height: 30px;
        border: 1px #ccc solid;
      }
    }
  }

  .page-content {
    width: 100%;
    height: 100%;
    -webkit-overflow-scrolling: touch;
    box-sizing: border-box;
    position: absolute;
    top: 60px;
    bottom: 0;
    right: 0;
    left: 0;
  }

  @media all and (-webkit-transform-3d) {
    .page-content {
      overflow-y: scroll;
      overflow-x: hidden;
    }
  }

</style>
