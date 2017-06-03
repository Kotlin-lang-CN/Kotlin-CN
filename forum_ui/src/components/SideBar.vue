<template>
  <div class="side-bar">
    <a class="button" :href="uiEdit">发布新话题</a>
    <div class="card">
      <header>网站通告</header>
      <div>文字常驻文字常驻文字常驻文字常驻文字常驻文字常驻文字常驻文字常驻文字常驻文字常驻文字常驻</div>
    </div>
    <div class="card">
      <header>友情链接</header>
      <ul>
        <li><a href="http://cnodejs.org/">cnodejs</a></li>
        <li><a href="http://golangtc.com/">golangtc</a></li>
        <li><a href="https://laravel-china.org/">laravel-china</a></li>
        <li><a href="http://elixir-cn.com/">elixir-cn</a></li>
        <li><a href="http://ionichina.com/">ionichina</a></li>
      </ul>
    </div>
  </div>
</template>
<script>
  import Config from "../assets/js/Config.js";
  export default {
    data() {
      return {
        uiEdit: Config.UI.edit
      }
    },
    methods: {}
  }
</script>

<style scoped lang="less">
  .side-bar {
    margin: auto 16px;
    border-left: 1px #f1f1f1 solid;
    max-width: 230px;
    min-width: 200px;
    .button {
      padding: 6px 12px;
      display: block;
    }

    .card {
      margin: 0 8px;
      header {
        font-weight: bolder;
        border-bottom: 1px #f1f1f1 solid;
        margin: 36px 8px 8px 8px;
      }
      ul {
        padding: 0;
        li {
          padding: 8px 0;
        }
      }
    }
  }

  @media screen and (max-width: 480px) {
    .side-bar {
      border-left: none;
      margin: auto;
    }

  }
</style>
