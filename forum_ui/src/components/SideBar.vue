<template>
  <div class="side-bar">
    <a class="button" :href="uiEdit" v-if="showPostBtn">发布新话题</a>
    <div class="part">
      <header>网站通告</header>
      <div class="card">
        <display-panels :content="dashboard"></display-panels>
      </div>
    </div>
    <div class="part">
      <header>友情链接</header>
      <ul class="card">
        <li><a href="http://cnodejs.org/" target="_blank"><i class="cnodejs"></i></a></li>
        <li><a href="https://laravel-china.org/" target="_blank"><i class="laravel"></i></a></li>
        <li><a href="http://golangtc.com/" target="_blank"><i class="golangtc"></i></a></li>
        <li><a href="http://elixir-cn.com/" target="_blank"><i class="elixir"></i></a></li>
        <!--<li><a href="http://ionichina.com/" target="_blank"><i class="ioni"></i></a></li>-->
        <li><a href="https://testerhome.com/" target="_blank"></a><i class="tester"></i></li>
      </ul>
    </div>
  </div>
</template>
<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import DisplayPanels from '../components/DisplayPanels.vue';
  export default {
    data() {
      return {
        uiEdit: Config.UI.edit,
        dashboard: ''
      }
    },
    props: {
      showPostBtn: true
    },
    components: {
      'display-panels': DisplayPanels
    },
    created(){
      this.updateDashboard();
    },
    methods: {
      updateDashboard(){
        let request = {
          url: Config.URL.misc.dashboard,
          type: "GET",
          condition: {}
        };
        Net.ajax(request, (data) => {
          this.dashboard = data.text;
        })
      }
    }
  }
</script>

<style scoped lang="less">
  .side-bar {
    margin: auto 16px;
    max-width: 230px;
    min-width: 200px;
    .button {
      padding: 6px 12px;
      display: block;
      color: white;
      background: #2572e5;
      font-size: 16px;
      border-radius: 2px;
      margin-bottom: 30px;
    }

    .part {
      margin: 0 8px;
      header {
        font-size: 18px;
        color: #999;
        text-align: left;
        margin-bottom: 12px;
      }
      .card {
        border: 1px #e4e4e4 solid;
        background-color: #f8f9fa;
        padding: 12px 14px;
        line-height: 36px;
        font-size: 16px;
        color: #333;
        margin-bottom: 28px;
      }
      ul {
        padding: 0;
        a {
          display: block;
          i {
            display: block;
            width: 230px;
            height: 40px;
            margin-bottom: 16px;
          }
          .cnodejs {
            background: url(../assets/img/friendship1.png) no-repeat;
          }
          .laravel {
            background: url(../assets/img/friendship2.png) no-repeat;
          }
          .golangtc {
            background: url(../assets/img/friendship3.png) no-repeat;
          }
          .elixir {
            background: url(../assets/img/friendship4.png) no-repeat;
          }
          .ioni {
            background: url(../assets/img/friendship5.png) no-repeat;
          }
          .tester {
            background: url(../assets/img/friendship6.png) no-repeat;
          }
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
