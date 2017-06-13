<template>
  <app-layout>
    <div class="content">
      <div class="post">
        <div class="sub-nav">
          <button v-on:click="getLatest" v-bind:class="{'select': select===0, 'normal': select!==0}">最新发布</button>
          <button v-for="(category, id) in categories" v-on:click="selectCategory(id + 1)"
                  v-bind:class="{
              'select': categories.length >= select && categories[select -1]===category,
              'normal': categories.length >= select && categories[select -1]!==category
          }">{{category}}
          </button>
        </div>
        <article-list :requestUrl="articleListUrl"></article-list>
      </div>
      <div class="side">
        <side-bar :showPostBtn="true"></side-bar>
      </div>
    </div>
  </app-layout>
</template>

<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import ArticleList from '../components/ArticleList.vue';
  import SideBar from '../components/SideBar.vue';
  import Cache from '../assets/js/Cache.js';
  import AppLayout from '../App.vue'
  import LoginMgr from '../assets/js/LoginMgr.js';

  export default {
    data() {
      return {
        uiEdit: Config.UI.edit,
        select: 0,
        articleListUrl: '',
        categories: []
      }
    },
    components: {
      AppLayout,
      'article-list': ArticleList,
      'side-bar': SideBar
    },
    created(){
      this.getCategories();
      setTimeout(() => {
        this.getLatest()
      }, 200)
    },
    methods: {
      selectCategory(id){
        this.articleListUrl = Config.URL.article.category.format(id);
        this.select = id;
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
      }
    }
  }
</script>
<style scoped lang="less">
  .content {
    padding: 0 16px;
    display: flex;
    box-sizing: border-box;
    max-width: 1120px;
    margin: auto;

    .post {
      float: left;
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
      width: 23%;
      padding-top: 8px;
    }
    .page {
      > div {
        display: inline-block;
        padding: 16px;
      }
    }
  }

  @media screen and (max-width: 480px) {
    .content {
      display: block;
      .post {
        display: block;
        float: none;
        width: 100%;
      }
      .side {
        display: block;
        width: 100%;
      }
    }
  }
</style>
