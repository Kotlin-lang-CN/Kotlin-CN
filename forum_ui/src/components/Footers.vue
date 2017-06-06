<template>
  <div class="foot" v-show="moduleShow">
    <hr/>
    <div class="content">
      <a href=mailto:chpengzh@kotlin-cn.org><i class="email"></i></a>
      <a target="_blank" href="http://localhost:8080/api/rss/fine"><i class="rss"></i></a>
      <a target="_blank" href="https://github.com/Kotlin-lang-CN"><i class="github"></i></a>
      <span class="icp">ICP备案号：<a target="_blank" href="http://www.miitbeian.gov.cn/">81999922</a></span>
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
    line-height: 30px;

    hr{
      background: #f1f1f1;
      border: 0;
      height: 1px;
    }
    .content {
      margin: auto;
      text-align: center;
      padding-top: 30px;
      i {
        display: inline-block;
        width: 30px;
        height: 30px;
      }
      .email {
        background: url(../assets/img/email.png) no-repeat;
      }
      .rss {
        background: url(../assets/img/ttf.png) no-repeat;
      }
      .github {
        background: url(../assets/img/github.png) no-repeat;
      }
      .icp {
        font-size: 14px;
        color: #999;
        display: inline-block;
        vertical-align: top;
        a{
          color:#999;
        }
      }
    }

  }
</style>
