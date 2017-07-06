<template>
  <div>
    <div class="content">
      <div class="list" v-for="value in articles" v-on:click="forward(value.meta.id)">
        <section>
          <span v-if="categories.length >= value.meta.category" class="category">
            {{ categories[value.meta.category - 1] }}
          </span>
          <span class="title">{{ value.meta.title }}</span>
        </section>
        <section>
          <span class="name">{{value.author.username}}</span>
          <small>发布于 {{ value.meta.create_time | moment}}, {{ value.replies }}评论</small>
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
        me: LoginMgr,
        loading: false,
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
      Net.get({url: Config.URL.article.category}, (resp) => {
        this.categories = resp.category;
        this.get(this.requestUrl, 0)
      });
    },
    methods: {
      get(url, offset){
        if (url.trim().length === 0) return;
        const limit = 20;
        Net.get({
          url: url,
          condition: {'offset': offset, 'limit': limit}
        }, (data) => {
          if (offset === 0) this.articles = [];
          this.hasMore = data.articles.length >= limit;
          this.articles = this.articles.concat(data.articles);
          this.offset = data.next_offset;
        })
      },
      forward(id) {
        window.location.href = Config.UI.post + '/' + id
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
      border-bottom: 1px #f1f1f1 solid;
      display: block;
      text-align: left;
      color: #999;
      padding: 10px 0 10px 0;
      margin-top: 10px;
      section {
        margin-top: 3px;
        margin-bottom: 3px;
      }
      .name {
        font-size: .2rem;
        line-height: 30px;
        margin-left: 5px;
      }
      .category {
        margin-left: 10px;
        color: white;
        background-color: #2572e5;
        font-size: .20rem;
        padding: 0 0 0 3px;
      }
      .title {
        color: #333;
        font-size: .30rem;
        margin-left: 3px;
        margin-right: 0;
      }
      .footer {
        text-align: right;
        font-size: .2em;
        margin-top: -5px;
      }
      .right {
        text-align: right;
        display: inline;
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
