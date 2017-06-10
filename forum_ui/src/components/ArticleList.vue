<template>
  <div>
    <div class="content">
      <div class="list" v-for="value in articles">
        <a href="javascript:void(0);">
          <div class="footnote" v-on:click="toArticle(value.meta.id)">
            最后由{{ value.last_editor.username }} 更新于 {{ value.meta.last_edit_time | moment}}
          </div>
          <i v-on:click="toArticle(value.meta.id)">{{ value.author.username.charAt(0).toUpperCase() }}</i>
          <div class="aside">
            <div class="title">
              <span v-on:click="toArticle(value.meta.id)">{{ value.meta.title }}</span>
              <select v-on:change="updateState(value.meta)" v-model="value.meta.state" class="right" v-if="isAdmin">
                <option v-for="option in options" v-bind:value="option.value">
                  {{ option.text }}
                </option>
              </select>
            </div>
            <div class="tag" v-on:click="toArticle(value.meta.id)">{{ value.meta.tags }}</div>
            <div class="footnote right" v-on:click="toArticle(value.meta.id)">
              {{ value.author.username }} 发布于 {{ value.meta.create_time | moment}}
            </div>
          </div>
        </a>
      </div>
    </div>

    <button v-on:click="get(requestUrl, offset)" v-show="hasMore">加载更多</button>
  </div>
</template>

<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import Event from "../assets/js/Event.js";
  import LoginMgr from '../assets/js/LoginMgr.js'

  export default {
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
        ]
      }
    },
    props: {
      requestUrl: ''
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
    .list a {
      display: block;
      padding: 16px 16px;
      text-align: left;
      border-top: 1px solid #f1f1f1;
      background: white;
      .footnote {
        font-size: 12px;
        color: #999;
      }
      i {
        display: inline-block;
        width: 60px;
        height: 60px;
        color: white;
        line-height: 60px;
        background-color: #2D93CA;
        text-align: center;
        font-size: 50px;
        font-style: normal;
        border-radius: 30px;
        vertical-align: top;
        margin-top: 4px;
      }
      .aside {
        display: inline-block;
        padding-top: 10px;
        width: 85%;
        .title {
          line-height: 28px;
          font-size: 24px;
          color: #333;
        }
        .tag {
          display: inline-block;
          font-size: 16px;
          color: #999;
        }
        .right {
          float: right;
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
  }

</style>
