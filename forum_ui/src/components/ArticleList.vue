<template>
  <div>
    <div class="content">
      <div class="list" v-for="value in articles">
        <section>
          <div class="footnote">
            最后由{{ value.last_editor.username }} 更新于 {{ value.meta.last_edit_time | moment}}
          </div>
          <div class="flex">
            <app-avatar :avatar="value.author.username"></app-avatar>
            <div class="wrap">
              <div class="title">
                <span v-on:click="toArticle(value.meta.id)" class="focus">{{ value.meta.title }}</span>
                <select v-on:change="updateState(value.meta)" v-model="value.meta.state" class="control" v-if="isAdmin">
                  <option v-for="option in options" v-bind:value="option.value">
                    {{ option.text }}
                  </option>
                </select>
              </div>
              <span v-if="categories.length >= value.meta.category"
                    class="category"> {{ categories[value.meta.category - 1] }}</span>
              <span class="tag focus" v-on:click="toArticle(value.meta.id)"
                    v-for="tag in value.meta.tags.split(/;/)">{{ tag
                }}
            </span>
              <div class="footnote right">
                {{ value.author.username }} 发布于 {{ value.meta.create_time | moment}}
              </div>
            </div>
          </div>
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
  import Avatar from "./Avatar.vue";

  export default {
    components: {
      "app-avatar": Avatar
    },
    data() {
      return {
        isAdmin: LoginMgr.isAdmin(),
        loading: false,
        urlTopic: Config.UI.topic + '/',
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
        Net.ajax({
          url: url,
          type: "GET",
          condition: {
            'offset': offset,
            'limit': limit
          }
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
      section {
        padding: 12px 0 30px 0;
        text-align: left;

        background: white;
        .footnote {
          font-size: 12px;
          color: #999;
        }
        .flex {
          margin-top: 16px;
          position: relative;
          display: flex;
          .wrap {
            margin-left: 10px;
          }
          .title {
            line-height: 28px;
            font-size: 24px;
            color: #333;
            margin-bottom: 6px;
          }
          .tag {
            display: inline-block;
            font-size: 16px;
            color: #999;
          }
          .category {
            display: inline-block;
            background-color: #256fe8;
            border-radius: 2px;
            color: white;
            margin-right: 10px;
            padding: 0 7px;
          }
          .focus {
            cursor: pointer;
          }
          .control {
            position: absolute;
            right: 0;
            bottom: 36px;
          }
          .right {
            position: absolute;
            right: 0;
            bottom: 5px;
          }
        }
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
