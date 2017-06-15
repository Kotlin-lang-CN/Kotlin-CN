<template>
  <app-layout>
    <div class="home-root">
      <div class="banner" v-on:click="homeLink">KOTLIN CHINA 上线了！
        <small v-if="isAdmin" v-on:click="editLink">编辑</small>
      </div>
      <div v-if="isAdmin && editURL">
        <input type="text" v-model="urlInput"/>
        <button v-on:click="applyEdit">确认</button>
      </div>
      <div class="content">
        <div class="post">
          <div class="sub-nav">
            <button v-bind:class="{ 'select': select==='fine', 'normal': select!=='fine' }"
                    v-on:click="selectFine">精品
            </button>
            <button v-bind:class="{ 'select': select==='latest', 'normal': select!=='latest' }"
                    v-on:click="selectLatest">最新
            </button>
          </div>
          <article-list :requestUrl="articleListUrl"></article-list>
        </div>
        <div class="side">
          <side-bar :showPost="false"></side-bar>
        </div>
      </div>
    </div>
  </app-layout>
</template>

<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import ArticleList from '../components/ArticleList.vue';
  import SideBar from '../components/SideBar.vue';
  import LoginMgr from '../assets/js/LoginMgr';
  import AppLayout from '../App.vue';
  import Event from '../assets/js/Event.js';

  export default {
    data() {
      return {
        uiEdit: Config.UI.edit,
        select: 'fine',
        articleListUrl: '',
        link: '',
        editURL: false,
        urlInput: '',
        isAdmin: LoginMgr.isAdmin(),
      }
    },
    components: {
      AppLayout,
      'article-list': ArticleList,
      'side-bar': SideBar
    },
    mounted(){
      this.articleListUrl = Config.URL.article.fine;
    },
    created() {
      Net.get({url: Config.URL.misc.homeLink}, (resp) => {
        this.urlInput = resp.link;
        this.link = resp.link;
      });
    },
    methods: {
      selectFine(){
        this.select = 'fine';
        this.articleListUrl = Config.URL.article.fine;
      },
      selectLatest(){
        this.select = 'latest';
        this.articleListUrl = LoginMgr.isAdmin() ? Config.URL.admin.articleList : Config.URL.article.list;
      },
      homeLink() {
        if (this.link !== '') window.location.href = this.link
      },
      editLink() {
        this.link = '';
        this.editURL = !this.editURL;
      },
      applyEdit() {
        Net.post({url: Config.URL.misc.homeLink, condition: {link: this.urlInput}}, () => {
          this.link = this.urlInput;
          this.editURL = !this.editURL;
        })
      }
    }
  }
</script>

<style scoped lang="less">
  .home-root {
    max-width: 1120px;
    margin: auto;
  }

  .banner {
    background: #73abfb;
    text-align: center;
    line-height: 120px;
    color: white;
    font-size: 25px;
    font-weight: bolder;
    height: 120px;
    margin-bottom: 30px;
  }

  .content {
    display: flex;
    box-sizing: border-box;
    .sub-title {
      text-align: left;
      padding: 24px 16px;
      font-size: 24px;
    }

    .post {
      width: 75%;
      .sub-nav {
        text-align: left;
        > button {
          background: white;
          outline: none;
          border: 0;
          color: #999;
          display: inline-block;
          font-size: 18px;
          padding: 0 16px;
          line-height: 50px;
          height: 50px;
        }
        .select {
          border-bottom: 4px #2572e5 solid;
          color: #2572e5;
        }
        .normal {
          border-bottom: 4px white solid;
          color: #999;
        }
      }
    }
    .side {
      padding-left: 30px;
      width: 25%;
      padding-top: 8px;
    }
    .page {
      > div {
        display: inline-block;
        padding: 16px;
      }
    }
  }

  @media screen and (max-width: 1000px) {
    .content {
      display: block;
      .post {
        display: block;
        float: none;
        width: 100%;
      }
      .side {
        padding: 0;
        display: block;
        width: 100%;
      }
    }
  }
</style>
