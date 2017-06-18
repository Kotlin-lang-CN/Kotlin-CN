<template>
  <div>
    <div class="content">
      <div class="list" v-for="value in articles" v-on:click="toArticle(value.meta.id)">
        <section>
          <app-avatar :avatar="value.author.username" :size="'small'"></app-avatar>
          <span class="name">{{value.author.username}}</span>
          <small class="tag focus">精品{{ value.is_fine }}</small>
        </section>
        <section>
          <span v-if="categories.length >= value.meta.category"
                class="category"> {{ categories[value.meta.category - 1] }}</span>
          <span v-on:click="toArticle(value.meta.id)" class="title">{{ value.meta.title }}</span>
        </section>
        <section>
           <span class="tag focus" v-on:click="toArticle(value.meta.id)"
                 v-for="tag in value.meta.tags.split(/;/)">{{ '#' + tag + '&nbsp' }}</span>
        </section>
        <section class="right">
          {{ value.author.username }} 发布于 {{ value.meta.create_time | moment}}
          {{ value.replies }}评论
        </section>
      </div>
    </div>
    <button v-on:click="get(requestUrl, offset)" v-show="hasMore">加载更多</button>
  </div>
</template>

<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import Event from "../assets/js/Event.js";
  import LoginMgr from '../assets/js/LoginMgr.js';
  import Avatar from "../components/Avatar.vue";

  export default {
    components: {
      "app-avatar": Avatar
    },
    data() {
      return {
        isAdmin: LoginMgr.isAdmin(),
        loading: false,
        urlTopic: Config.UI.post + '/',
        articles: [],
        offset: 0,
        hasMore: false,
        options: [
          {text: '正常', value: 0},
          {text: '冻结', value: 1},
          {text: '删除', value: 2},
          {text: '精品', value: 3}
        ],
        categories: []
      }
    },
    props: {
      requestUrl: ''
    },
    created() {
      this.getCategories();
      Event.on('login', () => this.isAdmin = LoginMgr.isAdmin());
    },
    methods: {
      get(url, offset){
        if (url.trim().length === 0)
          return;

        const limit = 20;
        Net.get({
          url: url,
          condition: {'offset': offset, 'limit': limit}
        }, (data) => {
          if (offset === 0) {
            this.articles = [];
          }
          this.hasMore = data.articles.length >= limit;
          this.articles = this.articles.concat(data.articles);
          this.offset = data.next_offset;
        })
      },
      toArticle(id) {
        window.location.href = this.urlTopic + id
      },
      updateState(article) {
        Net.post({
          url: Config.URL.admin.updateArticleState.format(article.id),
          condition: {
            state: article.state
          },
        }, () => {
          window.console.log("success!")
        }, () => {
          this.get(this.requestUrl, 0)
        })
      },
      getCategories(){
        if (!window.data) window.data = {};
        if (window.data.categories) {
          this.categories = window.data.categories;
        } else {
          Net.get({url: Config.URL.article.categoryType}, (resp) => {
            window.data.categories = resp.categories;
            this.categories = resp.category;
          });
        }
      },
      showDelete(article) {
        let info = LoginMgr.info();
        return !info.isAdminRole && info.isLogin && info.uid === article.author.uid
      },
      deleteArticle(article){
        Net.post({url: Config.URL.article.delete.format(article.meta.id)}, () => this.articles.remove(article))
      }
    },
    watch: {
      requestUrl: function (newValue, oldValue) {
        if (newValue !== oldValue) {
          this.get(newValue, 0);
        }
      },
    }
  }
</script>

<style scoped lang="less">
  .content {
    border-top: 1px solid #f1f1f1;
    .list:nth-child(1) {
      border-top: 0;
    }
    .list {
      border-top: 1px #f1f1f1 solid;
      display: block;
      text-align: left;
      color: #999;
      font-size: 10px;
      padding: 15px 0 10px 0;
      section {
        margin-bottom: 5px;
      }
      .name {
        font-size: 14px;
        line-height: 30px;
        margin-left: 5px;
      }
      .category {
        margin: 5px 0;
        color: white;
        background-color: #2572e5;
        font-size: 12px;
        padding: 0 6px;
      }
      .title {
        color: #333;
        font-size: 15px;
      }
      .right {
        text-align: right;
      }
    }

    button {
      background-color: #f2f7fd;
      outline: none;
      border: 1px #6ba0f1 solid;
      border-radius: 4px;
      margin: 16px 0;
      display: block;
      text-align: center;
      height: 50px;
      width: 100%;
      padding: 8px;
      color: #6ba0f1;
      font-size: 16px;
    }
    button:hover {
      background-color: #f8fbff;
      color: #2572e5;
    }
    button:active {
      background-color: #ecf4ff;
      color: #2572e5;
    }
  }

</style>
