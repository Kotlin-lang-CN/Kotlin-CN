<template>
  <div class="content">
    <div class="sub-title">精华</div>
    <article-list :requestUrl="articleListUrl"></article-list>
  </div>
</template>

<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import ArticleList from '../components/ArticleList.vue';
  import Event from "../assets/js/Event.js";

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
      this.articleListUrl = Config.URL.article.list;
    },
    methods: {}
  }
</script>

<style scoped lang="less">
  .content {
    .sub-title {
      text-align: left;
      padding: 24px 16px;
      font-size: 24px;
    }
  }
</style>
