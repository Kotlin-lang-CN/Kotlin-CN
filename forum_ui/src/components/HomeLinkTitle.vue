<template>
  <div>
    <div class="banner" v-on:click="homeLink">KOTLIN CHINA 上线了！
      <small class="edit-btn" v-if="me.isAdminRole" v-on:click.stop="editLink">编辑</small>
    </div>
    <div class="dialog" v-if="showDialog">
      <div class="bg" v-on:click="showEdit = !showEdit"></div>
      <div class="dialog-content">
        <h4>编辑首页链接</h4>
        <input class="url-input" type="text" v-model="urlInput"/>
        <div>
          <button v-on:click="applyEdit">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import Login from '../assets/js/LoginMgr'
  import Net from '../assets/js/Net'
  import Config from '../assets/js/Config'

  export default {
    data() {
      return {
        me: Login,
        url: '',
        showEdit: false,
        urlInput: ''
      }
    },
    created() {
      Net.get({url: Config.URL.misc.homeLink}, (resp) => this.url = this.urlInput = resp.link)
    },
    methods: {
      homeLink() {
        if (this.url && this.url.trim().length > 0)
          window.location.href = this.url
      },
      editLink() {
        this.showEdit = !this.showEdit;
        window.console.log(this.showDialog);
      },
      applyEdit() {
        Net.post({url: Config.URL.misc.homeLink, condition: {link: this.urlInput}}, () => {
          this.showEdit = !this.showEdit;
          this.url = this.urlInput;
        })
      },
    },
    computed: {
      showDialog() {
        return this.me.isAdminRole && this.showEdit
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
  .banner {
    background: #73abfb;
    text-align: center;
    line-height: 120px;
    color: white;
    font-size: 25px;
    font-weight: bolder;
    height: 120px;
    margin-bottom: 30px;
    cursor: pointer;

    .edit-btn {
      font-size: 15px;
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
    }
  }
</style>
