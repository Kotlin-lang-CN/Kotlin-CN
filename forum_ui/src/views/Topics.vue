<template>
  <div>
    <div class="content">
      <div class="post">
        <div class="sub-nav">
          <button v-on:click="selectDefault"
                  v-bind:class="{ 'select': select==='default', 'normal': select!=='default' }">默认
          </button>
          <button v-on:click="selectFine"
                  v-bind:class="{ 'select': select==='fine', 'normal': select!=='fine' }">优质
          </button>
          <button v-on:click="selectNew"
                  v-bind:class="{ 'select': select==='news', 'normal': select!=='news' }">最新
          </button>
        </div>
        <article-list :requestUrl="articleListUrl"></article-list>
      </div>
      <div class="side">
        <side-bar></side-bar>
      </div>
    </div>
  </div>
</template>

<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import ArticleList from '../components/ArticleList.vue';
  import SideBar from '../components/SideBar.vue';

  export default {
    data() {
      return {
        uiEdit: Config.UI.edit,
        select: 'default',
        articleListUrl: ''
      }
    },
    components: {
      'article-list': ArticleList,
      'side-bar': SideBar
    },
    mounted(){
      this.articleListUrl = Config.URL.article.list;
    },
    methods: {
      selectDefault(){
        this.select = 'default';
        this.articleListUrl = Config.URL.article.list;
      },
      selectFine(){
        this.select = 'fine';
        this.articleListUrl = Config.URL.article.fine;
      },
      selectNew(){
        this.select = 'news';
        this.articleListUrl = Config.URL.article.list;
      }
    }
  }
</script>
<style scoped lang="less">
  .content {
    padding: 0 16px;
    display: flex;
    box-sizing: border-box;

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
    .side{
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
    .content{
      display: block;
      .post{
        display: block;
        float: none;
        width: 100%;
      }
      .side{
        display: block;
        width: 100%;
      }
    }
  }
</style>
