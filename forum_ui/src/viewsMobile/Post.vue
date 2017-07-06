<template>
  <app-layout>
    <div class="topic">
      <article>
        <header>
          <span v-if="article && categories.length >= article.article.category" class="category">
              {{ categories[article.article.category - 1]}}
          </span>
          <section class="title">{{ article.article.title }}</section>
          <br>
          <span class="description right">
            {{article.author.username}} 发布于 {{article.article.create_time | moment}}
          </span>
        </header>
        <section>
          <display-panels :content="article.content.content"></display-panels>
        </section>
      </article>
      <a class="footer" v-on:click="toComment">
        <i class="icon-com"></i>评论
      </a>
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
    components: {
      'app-layout': AppLayout,
      'app-reply': Reply,
      'display-panels': DisplayPanels,
    },
    data () {
      return {
        id: this.$root.params.id,
        article: {
          article: {title: '', tags: '', category: 1},
          content: {content: ''},
          author: {uid: ''},
        },
        categories: [],
        reply: [],
        toReplyContent: '',
      }
    },
    created(){
      this.init();
      Event.on('route-update', () => {
        const newId = this.$root.params.id;
        if (this.id !== newId) {
          this.id = newId;
          if (this.id) this.init()
        }
      })
    },
    methods: {
      init(){
        Net.get({url: Config.URL.article.category}, (resp) => {//categories 信息
          this.categories = resp.category;
          Net.get({url: Config.URL.article.detail.format(this.id)}, (resp) => {//问斩信息
            this.article = resp;
          }, (resp) => {
            if (resp.code === 34) window.location.href = "/404"
          });
        });
      },
      toComment() {
        window.location.href = Config.UI.comments + "/" + this.id
      }
    },
    computed: {
      showEdit() {
        return this.article && LoginMgr.isLogin && LoginMgr.uid === this.article.author.uid || LoginMgr.isAdminRole
      },
      metaData() {
        const result = this.article.article;
        result.categories = this.categories;
        return result
      }
    },
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
        margin-top: 15px;
        font-size: .2rem;
        color: #999;
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
        .title {
          display: inline;
          color: #333;
          font-size: .33rem;
          line-height: 30px;
          margin-bottom: 0;

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
        .description {
          font-size: .22rem;
          line-height: 30px;
          margin-left: 8px;
          vertical-align: top;
          margin-right: 8px
        }
        .right {
          float: right;
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

