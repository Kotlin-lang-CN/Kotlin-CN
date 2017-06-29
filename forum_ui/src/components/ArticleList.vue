<template>
  <div>
    <div class="content">
      <div class="list" v-for="value in articles" v-on:click="forward(value.meta.id)">
        <section>
          <div class="footnote">
            最后由{{ value.last_editor.username }} 更新于 {{ value.meta.last_edit_time | moment}}
          </div>
          <div class="flex">
            <app-avatar :avatar="value.author.username"></app-avatar>
            <div class="wrap">
              <div class="title">
                <span class="focus">{{ value.meta.title }}</span>
                <div class="extra">
                  <!--suppress HtmlFormInputWithoutLabel -->
                  <select v-model="value.meta.state" v-on:change="updateState(value.meta)"
                          v-on:click.stop="" v-if="me.isAdminRole">
                    <option v-for="option in options" v-bind:value="option.value">{{ option.text }}</option>
                  </select>
                  <small v-if="showDelete(value)" class="delete" v-on:click.stop="deleteArticle(value)">删除</small>
                  <small class="reply-count">{{ value.replies }}</small>
                  <i v-if="value.is_fine" class="fine"></i>
                </div>
              </div>
              <span v-if="categories.length >= value.meta.category"
                    class="category"> {{ categories[value.meta.category - 1] }}</span>
              <span class="tag focus" v-for="tag in value.meta.tags.split(/;/)">{{ '#' + tag + '&nbsp' }}
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
      Net.get({url: Config.URL.article.categoryType}, (resp) => {
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
          if (offset === 0) {
            this.articles = [];
          }
          this.hasMore = data.articles.length >= limit;
          this.articles = this.articles.concat(data.articles);
          this.offset = data.next_offset;
        })
      },
      forward(id){
        window.location.href = Config.UI.post + '/' + id
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
      showDelete(article) {
        return !this.me.isAdminRole && this.me.isLogin && this.me.uid === article.author.uid
      },
      deleteArticle(article){
        new Promise((next) => {
          Event.emit('alert', {
            title: '删除文章',
            text: '确认删除【' + article.meta.title + '】?',
            allow_dismiss: true,
            confirm: {text: '确定', action: () => next()},
            cancel: {text: '取消', action: () => false},
          })
        }).then(() => {
          Net.post({url: Config.URL.article.delete.format(article.meta.id)}, () => {
            this.articles.remove(article)
          })
        });
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
    .list section:hover .flex .delete {
      display: inline-block;
    }

    .list {
      border-top: 1px #f1f1f1 solid;
      display: block;
      cursor: pointer;

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
            font-size: 14px;
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
          .reply-count {
            font-size: 18px;
            padding: 0 18px;
            background-color: #c9dcf6;
            color: white;
          }
          .delete {
            display: none;
            font-size: 14px;
            color: red;
          }
          .fine {
            display: inline-block;
            width: 30px;
            height: 30px;
            float: right;
            margin-top: 3px;
            margin-left: 12px;
            background: url(../assets/img/fine.png) no-repeat;
          }
          .extra {
            position: absolute;
            right: 0;
            top: -3px;
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
