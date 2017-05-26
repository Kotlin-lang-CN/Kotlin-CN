<template>
  <div class="nav-bar">
    <div class="menu-header"><a href="#/" title=""><b>kotlin</b> China</a></div>
    <div class="menu-main">
      <ul>
        <li><a href="#/topics" title="社区">社区</a></li>
      </ul>
    </div>
    <div class="menu-authen menu-right">
      <a href="#/account" title="注册">注册</a>
      <a href="#/account" title="登录">登录</a>
    </div>
    <div class="menu-user menu-right"></div>
  </div>
</template>

<script>
  export default {
    name: 'foot',
    data: function () {
      return {
        msg: 'FOOTER'
      }
    }
  }
</script>

<style scoped lang="less">
  .nav-bar {
    background: white;
    padding: 0px 16px;
    box-shadow: 0 1px 1px rgba(0, 0, 0, 0.21);
    margin-bottom: 8px;

    > div {
      display: inline-block;
    }
    .menu-header b {
      color: #eb5424;
      font-weight: normal;
    }
    .menu-right {
      float: right;
      margin: 16px 16px;
    }
  }
</style>
