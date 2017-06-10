<template>
  <div class="content">
    <div class="banner">KOTLIN CHINA 上线了！</div>
    <div class="sub-title">精华</div>
    <article-list :requestUrl="articleListUrl"></article-list>
  </div>
</template>

<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import ArticleList from '../components/ArticleList.vue';
  import Event from "../assets/js/Event.js";
  import LoginMgr from '../assets/js/LoginMgr.js';

  export default {
    name: 'home',
    components: {
      'article-list': ArticleList
    },
    data: function () {
      return {
        uiTopics: Config.UI.topics,
        articleListUrl: ''
      }
    },
    mounted(){
      this.articleListUrl = (LoginMgr.isAdmin()) ? Config.URL.admin.articleList : Config.URL.article.list;
    },
    methods: {}
  }
</script>

<style scoped lang="less">
  .content {
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
    .sub-title {
      text-align: left;
      padding: 24px 16px;
      font-size: 24px;
    }
  }
</style>
