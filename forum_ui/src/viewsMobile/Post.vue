<template>
  <app-layout>
    <div class="topic">
      <article v-if="topic !== null ">
        <header>
          <section>
            <app-avatar :avatar="topic.author.username" :size="'small'"></app-avatar>
            <span class="name">{{topic.author.username}}</span>
            <span v-if="topic && categories.length >= topic.article.category" class="category">
              {{ categories[topic.article.category - 1]}}
            </span>
            <a :href="editUrl" v-if="editUrl !== ''" class="edit">编辑</a>
            <i v-if="topic.is_fine" class="fine"></i>
          </section>
          <section class="title">{{ topic.article.title }}</section>
          <section class="tag-lay">
            <span class="tag" v-for="tag in topic.article.tags.split(/;/)">{{ '#' + tag + '&nbsp' }}</span>
          </section>
          <section class="right">发布于 {{ topic.article.create_time | moment}}</section>
        </header>
        <section>
          <display-panels :content="content"></display-panels>
        </section>
      </article>
      <a class="footer" :href="commentUrl" v-if="commentUrl !== ''"><i class="icon-com"></i>评论{{ topic.replies }}</a>
    </div>
  </app-layout>
</template>

<script>
  import LoginMgr from "../assets/js/LoginMgr.js";
  import Config from "../assets/js/Config.js";
  import Event from "../assets/js/Event.js";
  import Net from "../assets/js/Net.js";

  import DisplayPanels from '../components/DisplayPanels.vue';
  import Reply from '../components/Reply.vue';
  import AppLayout from '../layout/AppMobileSun.vue';
  import Avatar from "../components/Avatar.vue";

  export default {
    data () {
      return {
        editUrl: '',
        commentUrl: '',

        id: '',
        articleId: '',

        topic: null,
        content: '',
        reply: [],
        toReplyContent: '',
        categories: []
      }
    },
    components: {
      AppLayout,
      'app-reply': Reply,
      'display-panels': DisplayPanels,
      "app-avatar": Avatar
    },
    created(){
      this.id = this.$root.params.id;
      this.getArticle();
      this.getCategories();
      Event.on('login', () => this.renderEditUrl());
      Event.on('route-update', () => {
        this.id = this.$root.params.id;
        if (this.id) {
          this.articleId = this.id;
          this.getArticle();
          this.getCategories();
        }
      })
    },
    mounted(){
      this.articleId = this.id;
    },
    methods: {
      getArticle(){
        Net.get({
          url: Config.URL.article.detail.format(this.id),
          type: "GET",
          condition: {}
        }, (data) => {
          this.topic = data;
          this.content = data.content.content;
          this.renderEditUrl();
          this.commentUrl = Config.UI.comments + "/" + this.articleId;
        }, (resp) => {
          if (resp.code === 34) window.location.href = "/404"
        })
      },
      getCategories() {
        if (!window.data) window.data = {};
        if (window.data.categories) {
          this.categories = window.data.categories;
        } else {
          Net.get({url: Config.URL.article.categoryType}, (resp) => {
            window.data.categories = resp.category;
            this.categories = resp.category;
          });
        }
      },
      renderEditUrl() {
        if (this.topic !== null
          && this.topic.author
          && LoginMgr.info().isLogin
          && LoginMgr.info().uid === this.topic.author.uid
          || LoginMgr.isAdmin()) {
          this.editUrl = Config.UI.edit + "/" + this.articleId;
        } else {
          this.editUrl = ''
        }
      }
    }
  }
</script>


<style scoped lang="less">
  div.topic {
    text-align: left;
    margin: 0 auto 10px auto;
    article {
      max-width: 840px;
      padding: 0 16px 80px 16px;
      header {
        border-bottom: 1px #e4e4e4 solid;
        padding-top: 15px;
        padding-bottom: 38px;
        margin-bottom: 20px;
        font-size: .2rem;
        color: #999;

        .name {
          font-size: .28rem;
          line-height: 30px;
          margin-left: 8px;
          vertical-align: top;
        }
        .category {
          font-size: .2rem;
          display: inline-block;
          padding: 0 4px;
          color: white;
          background-color: #2572e5;
          vertical-align: top;
          margin-top: 8px;
          margin-left: 8px;
        }
        .fine {
          display: inline-block;
          width: 30px;
          height: 30px;
          background: url(../assets/img/fine.png) no-repeat;
          float: right;
          margin-top: 4px;
          background-size: 50% 50%;
        }
        .title {
          color: #333;
          font-size: .38rem;
          line-height: 30px;
          margin-bottom: 0;
        }
        .tag-lay {
          padding: 6px 0;
        }
        .tag {
          font-size: 12px;
          display: inline-block;
          background-color: #c9dcf5;
          color: #333;
          padding:0 2px;
          vertical-align: top;
          margin-top: 0;
          line-height: 20px;
          margin-right: 8px;
        }
        .right {
          float: right;
        }
        .edit {
          padding: 8px 10px;
          color: red;
          display: inline-block;
        }
      }
    }
  }

  .footer {
    display: block;
    border-top: 1px #e3e3e3 solid;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    text-align: center;
    height: 45px;
    line-height: 45px;
    color: #999;
    background-color: #fcfcfc;
    font-size: .28rem;

    .icon-com {
      background: url(../assets/img/comment-icon.png) no-repeat center;
      background-size: 50% 50%;
      display: inline-block;
      width: 36px;
      height: 34px;
      margin-top: 7px;
      vertical-align: top;
    }
  }
</style>

