<template>
  <div class="foot" v-show="moduleShow">
    <hr/>
    <i></i>
    <div class="content">
      <div>联系我们：<a href=mailto:chpengzh@kotlin-cn.org>kotlin-cn Admin</a></div>
      <div>RSS订阅：TODO </div>
      <div>项目地址：<a href="https://github.com/Kotlin-lang-CN">KOTLIN-LANG-CN</a></div>
      <div>ICP备案号：TODO </div>
    </div>
  </div>
</template>
<script>
  import Event from '../assets/js/Event.js';
  export default {
    name: 'foot',
    data: function () {
      return {
        moduleShow: true
      }
    },
    created: function () {
      Event.on("fullscreen", (on) => {
        this.moduleShow = !on;
      })
    }
  }
</script>

<style scoped lang="less">
  .foot {
    max-width: 1000px;
    min-width: 320px;
    text-align: left;
    margin: 90px auto 40px auto;
    color: #999;

    i{
      display: inline-block;
      width: 60px;
      height: 60px;
      vertical-align: top;
      background: #2c3e50;
      margin-left: 30px;
      margin-right: 8px;
    }
    .content{
      display: inline-block;
    }
  }
</style>
