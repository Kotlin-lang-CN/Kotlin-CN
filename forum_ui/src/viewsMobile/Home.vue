<template>
  <app-layout>
    <div class="home-root">
      <div class="content">
        <div class="post">
          <div class="sub-nav">
            <button v-on:click="selectFine" v-bind:class="{'select': select===0, 'normal': select!==0 }">精品</button>
            <button v-on:click="selectLatest" v-bind:class="{'select': select===1, 'normal': select!==1}">最新发布</button>
            <!--suppress CommaExpressionJS -->
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
    </div>
  </app-layout>
</template>

<script>
  import HomeLinkTitle from '../components/HomeLinkTitle.vue';
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import ArticleList from '../componentsMobile/ArticleList.vue';
  import SideBar from '../components/SideBar.vue';
  import LoginMgr from '../assets/js/LoginMgr';
  import AppLayout from '../layout/AppMobile.vue';
  import Event from '../assets/js/Event.js';

  export default {
    components: {
      'home-link-title': HomeLinkTitle,
      'app-layout': AppLayout,
      'article-list': ArticleList,
      'side-bar': SideBar
    },
    data() {
      return {
        select: 0,
        articleListUrl: '',
        categories: [],
      }
    },
    mounted() {
      Net.get({url: Config.URL.article.category}, (resp) => {
        this.categories = resp.category;
        this.selectFine()
      });
    },
    methods: {
      selectFine(){
        this.articleListUrl = Config.URL.article.getFine;
        this.select = 0;
      },
      selectLatest() {
        this.articleListUrl = LoginMgr.isAdmin() ? Config.URL.admin.articleList : Config.URL.article.getLatest;
        this.select = 1;
      },
      selectCategory(id){
        this.articleListUrl = Config.URL.article.getCategory.format(id);
        this.select = id + 1;
      },
    }
  }
</script>

<style scoped lang="less">
  .home-root {
    margin: auto;
    .content {
      box-sizing: border-box;
      overflow-y: scroll;
      padding: 0 10px;
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
          font-size: 12px;
          padding: 0 12px;
          line-height: 30px;
          height: 34px;
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
