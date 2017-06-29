<template>
  <div class="side-bar">
    <a class="button" href="javascript:void(0);" v-on:click="newPost" v-if="showPostBtn">发布新话题</a>
    <div class="part">
      <header>网站通告
        <button v-if="me.isAdminRole" v-on:click="editDashboard = !editDashboard">编辑</button>
      </header>
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
        <li><a href="https://testerhome.com/" target="_blank"></a><i class="tester"></i></li>
      </ul>
    </div>
    <div class="dialog" v-if="showDialog">
      <div class="bg" v-on:click="editDashboard = !editDashboard"></div>
      <div class="dialog-content">
        <h4>编辑首公告栏</h4>
        <textarea class="dashboard" v-model="dashboardInput" title=""></textarea>
        <div>
          <button v-on:click="updateDashboard">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import DisplayPanels from '../components/DisplayPanels.vue';
  import LoginMgr from '../assets/js/LoginMgr.js';

  export default {
    components: {
      'display-panels': DisplayPanels
    },
    data() {
      return {
        dashboard: '',
        dashboardInput: '',
        me: LoginMgr,
        editDashboard: false
      }
    },
    props: {
      showPostBtn: true
    },
    created(){
      Net.get({url: Config.URL.misc.dashboard}, (data) => this.dashboard = this.dashboardInput = data.text);
    },
    computed: {
      showDialog() {
        return this.editDashboard && this.me.isAdminRole
      }
    },
    methods: {
      updateDashboard() {
        const input = this.dashboardInput;
        Net.post({url: Config.URL.misc.dashboard, condition: {dashboard: input}}, () => {
          this.dashboard = input;
          this.editDashboard = !this.editDashboard;
        })
      },
      newPost() {
        LoginMgr.require(() => window.location.href = Config.UI.edit)
      }
    }
  }
</script>

<style scoped>
  .dialog .dialog-content {
    margin-left: calc(50% - 200px);
  }
</style>

<style scoped lang="less">
  .side-bar {
    .button {
      line-height: 38px;
      height: 38px;
      display: block;
      color: white;
      background: #2572e5;
      font-size: 16px;
      border-radius: 2px;
      margin-bottom: 30px;
    }
    .button:hover {
      background-color: #4599f7;
    }
    .button:active {
      background-color: #1c4ecf;
    }
    .part {
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
            width: 138px;
            height: 40px;
            margin: 0 auto 16px auto;
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

  .dialog {
    position: fixed;
    z-index: 3;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;

    .url-input {
      width: 300px;
    }

    .bg {
      position: absolute;
      background: #000000;
      filter: alpha(opacity=50);
      -moz-opacity: 0.5;
      opacity: 0.5;
      width: 100%;
      height: 100%;
    }

    .dialog-content {
      position: absolute;
      width: 400px;
      margin-top: 200px;
      padding: 20px;
      box-sizing: border-box;
      background: white;
      border: 1px #f1f1f1 solid;
      box-shadow: 0 0 3px #2572e5;

      .dashboard {
        width: 100%;
        height: 170px;
        box-sizing: border-box;
        display: block;
        max-width: 100%;
        line-height: 1.5;
        padding: 15px 15px 30px;
        border-radius: 3px;
        border: 1px solid #f1f1f1;
        font-size: 13px;
      }
    }
  }

  @media screen and (max-width: 1000px) {
    .side-bar {
      border-left: none;
      margin: auto;
    }
  }
</style>
