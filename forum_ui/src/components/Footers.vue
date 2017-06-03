<template>
  <div class="foot" v-show="moduleShow">
    <hr/>
    <i></i>
    <div class="content">
      <div>
        <a href=mailto:chpengzh@kotlin-cn.org>Email</a>
        <a target="_blank" href="http://localhost:8080/api/rss/fine">RSS</a>
        <a target="_blank" href="https://github.com/Kotlin-lang-CN">Github</a>
      </div>
      <div>ICP备案号：<a target="_blank" href="http://www.miitbeian.gov.cn/">xxx</a></div>
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
    },
    action: {}
  }
</script>

<style scoped lang="less">
  .foot {
    max-width: 1000px;
    min-width: 320px;
    text-align: left;
    margin: 90px auto 40px auto;
    color: #999;

    i {
      display: inline-block;
      width: 60px;
      height: 60px;
      vertical-align: top;
      background: #2c3e50;
      margin-left: 30px;
      margin-right: 8px;
    }
    .content {
      display: inline-block;
    }
  }
</style>
