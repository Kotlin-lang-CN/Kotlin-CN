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
        <div class="sub-nav">
          <button v-on:click="getLatest" v-bind:class="{'select': select===0, 'normal': select!==0}">最新发布</button>
          <button v-on:click="selectFine" v-bind:class="{'select': select===1, 'normal': select!==1 }">精品</button>
          <button v-for="(category, id) in categories" v-on:click="selectCategory(id + 1)"
                  v-bind:class="{
              'select': categories.length >= select - 1 && categories[select -2]===category,
              'normal': categories.length >= select - 1 && categories[select -2]!==category
              }">{{category}}
          </button>
        </div>
        <article-list :requestUrl="articleListUrl"></article-list>
      </div>
    </div>
  </app-layout>
</template>

<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import ArticleList from '../componentsMobile/ArticleList.vue';
  import SideBar from '../components/SideBar.vue';
  import LoginMgr from '../assets/js/LoginMgr';
  import AppLayout from '../layout/AppMobile.vue';
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
        categories: [],
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
      this.getCategories();
      setTimeout(() => {
        this.getLatest()
      }, 200)
    },
    methods: {
      selectCategory(id){
        this.articleListUrl = Config.URL.article.category.format(id);
        this.select = id + 1;
      },
      selectFine(){
        this.select = 1;
        this.articleListUrl = Config.URL.article.fine;
      },
      getCategories(){
        if (!window.data) window.data = {};
        if (window.data.categories) {
          this.categories = window.data.categories;
        } else {
          Net.get({url: Config.URL.article.categoryType}, (resp) => {
            window.data.categories = resp.category;
            this.categories = resp.category;
          });
        }
      },
      getLatest() {
        this.articleListUrl = LoginMgr.isAdmin() ? Config.URL.admin.articleList : Config.URL.article.list;
        this.select = 0;
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
    margin: auto;

    .banner {
      background: #73abfb;
      text-align: center;
      line-height: 80px;
      color: white;
      font-size: 20px;
      font-weight: bolder;
      height: 80px;
      margin-bottom: 15px;
    }

    .content {
      box-sizing: border-box;
      overflow-y: scroll;
      padding: 0 16px;

      .sub-title {
        text-align: left;
        padding: 24px 16px;
      }

      .sub-nav {
        text-align: left;
        > button {
          background: white;
          outline: none;
          border: 0;
          display: inline-block;
          font-size: 15px;
          padding: 0 16px;
          line-height: 50px;
          height: 50px;
        }
        .select {
          border-bottom: 2px #2572e5 solid;
          color: #2572e5;
        }
        .normal {
          border-bottom: 2px white solid;
          color: #999;
        }
      }
      > button:hover {
        color: #2572e5;
      }
    }
  }
</style>
