<template>
  <div>
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
    <div class="content">
      <div class="post">
        <article-list :requestUrl="articleListUrl"
                      :requestOffset="articleListOffset"></article-list>
        <div class="page">
          <div>ONE</div>
          <div>TWO</div>
          <div>THREE</div>
        </div>
      </div>
      <div class="right-nav">
        <a class="button" :href="uiEdit">发布新话题</a>
      </div>
    </div>
  </div>
</template>

<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import ArticleList from '../components/ArticleList.vue';
  export default {
    data() {
      return {
        uiEdit: Config.UI.edit,
        select: 'default',
        articleListUrl: '',
        articleListOffset: 0
      }
    },
    components: {
      'article-list': ArticleList
    },
    created() {
      setTimeout(() => {
        this.articleListUrl = Config.URL.article.list;
      }, 20);
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
      },
      createPost(){
        location.href = Config.UI.edit;
      }
    }
  }
</script>
<style scoped lang="less">
  .sub-nav {
    text-align: left;
    padding: 16px;
    > button {
      background: white;
      outline: none;
      border: 0;
      color: #666;
      display: inline-block;
      font-size: 24px;
    }
    .select {
      color: #eb5424;
    }
    .normal {
      color: #666;
    }
  }

  .content {
    padding: 0 16px;
    display: flex;
    box-sizing: border-box;

    .post {
      float: left;
      width: 75%;
    }

    .page {
      > div {
        display: inline-block;
        padding: 16px;
      }
    }
  }

  .right-nav {
    padding: 8px 0;
    background: white;
    float: right;
    width: 23%;
  }

  .button {
    border-left: 1px #f1f1f1 solid;
    padding: 6px 12px;
    float: right;
  }

</style>
