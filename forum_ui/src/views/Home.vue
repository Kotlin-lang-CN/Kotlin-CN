<template>
  <div class="content">
    <div class="sub-title">精华帖</div>
    <article-list :requestUrl="articleListUrl"
                  :requestOffset="articleListOffset"></article-list>
    <a :href="uiTopics" class="more">查看更多...</a>
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
        articleListUrl: '',
        articleListOffset: 0
      }
    },
    created() {
      setTimeout(() => {
        this.articleListUrl = Config.URL.article.list;
      }, 20);
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
    .more {
      display: block;
      text-align: left;
      padding: 16px;
      color: #eb5424;
      font-size: 14px;
    }
  }
</style>
