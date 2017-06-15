<template>
  <div class="drawer">
    <ul>
      <li><a href="#">社区</a></li>
      <li><a href="#">注册</a></li>
      <li><a href="#">登录</a></li>
      <li><a href="#">发布新话题</a></li>
    </ul>
  </div>
</template>
<script>
  export default {
    name: 'hello',
    data: function () {
      return {
        msg: 'TODO:Manager'
      }
    }
  }
</script>
<style scoped lang="less">
  .drawer ul {
    list-style-type: none;
    a {
      display: block;
      padding: 10px;
      color: #c7c7c7;
      text-decoration: none;
    }
    a:hover {
      color: white;
    }
  }
</style>
