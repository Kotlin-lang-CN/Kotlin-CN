<template>
  <div>
    <div class="content">
      <div class="post">
        <div class="sub-nav">
          <button v-for="(category, id) in categories" v-on:click="selectCategory(id)"
                  v-bind:class="{
              'select': categories.length > select && categories[select]===category,
              'normal': categories.length > select && categories[select]!==category
          }">{{category}}
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
        select: 0,
        articleListUrl: '',
        categories: []
      }
    },
    components: {
      'article-list': ArticleList,
      'side-bar': SideBar
    },
    created(){
      Net.get({url: Config.URL.article.categoryType}, (resp) => {
        this.categories = resp.category;
        this.selectCategory(0)
      });
    },
    methods: {
      selectCategory(id){
        this.articleListUrl = Config.URL.article.category.format(id + 1);
        this.select = id;
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
