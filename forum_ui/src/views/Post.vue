<template>
  <app-layout>
    <div class="topic">
      <article>
        <header>
          <article-meta :meta.sync="metaData"></article-meta>
          <div>
            <label v-if="article.article.create_time !== article.article.last_edit_time">
              最近更新于{{ article.article.last_edit_time | moment}}，
            </label>
            <span>{{ article.author.username }}</span> 发布于 {{ article.article.create_time | moment}}
            <a :href="'/edit/' + id" v-if="showEdit" class="edit">编辑</a>
          </div>
        </header>
        <section>
          <display-panels :content="article.content.content"></display-panels>
        </section>
        <app-reply :articleId="id"></app-reply>
      </article>
    </div>
  </app-layout>
</template>

<script>
  import LoginMgr from "../assets/js/LoginMgr.js";
  import Config from "../assets/js/Config.js";
  import Event from "../assets/js/Event.js";
  import Net from "../assets/js/Net.js";
  import Util from '../assets/js/Util.js';

  import DisplayPanels from '../components/DisplayPanels.vue';
  import Reply from '../components/Reply.vue';
  import AppLayout from '../layout/AppWeb.vue';
  import ArticleMeta from '../components/ArticleMeta.vue';

  export default {
    components: {
      'article-meta': ArticleMeta,
      'app-layout': AppLayout,
      'app-reply': Reply,
      'display-panels': DisplayPanels
    },
    data () {
      return {
        id: this.$root.params.id,
        article: {
          article: {title: '', tags: '', category: 1},
          content: {content: ''},
          author: {uid: ''}
        },
        categories: [],
        reply: [],
        toReplyContent: '',
      }
    },
    created(){
      this.init();
      Event.on('route-update', () => {
        this.id = this.$root.params.id;
        if (this.id) this.init()
      })
    },
    methods: {
      init(){
        Net.get({url: Config.URL.article.categoryType}, (resp) => {//categories 信息
          this.categories = resp.category;
          Net.get({url: Config.URL.article.detail.format(this.id)}, (resp) => {//问斩信息
            this.article = resp;
            this.scrollToTop();
          }, (resp) => {
            if (resp.code === 34) window.location.href = "/404"
          });
        });
      },
      scrollToTop() {
        $('html, body').animate({scrollTop: 0}, 'fast');
      }
    },
    computed: {
      showEdit() {
        return this.article && LoginMgr.isLogin && LoginMgr.uid === this.article.author.uid
          || LoginMgr.isAdminRole
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
    max-width: 1120px;
    article {
      max-width: 840px;
      header {
        border-top: 1px #e4e4e4 solid;
        border-bottom: 1px #e4e4e4 solid;
        padding-top: 10px;
        padding-bottom: 24px;
        margin-bottom: 20px;
        font-size: 12px;
        color: #999;
        span {
          color: #2572e5;
          display: inline-block;
          margin-right: 4px;
          margin-left: 4px;
        }
        .edit {
          padding: 0 20px;
          color: red;
          display: inline-block;
        }
      }
    }
  }

  @media screen and (max-width: 480px) {
    #app > div.topic {
      margin: 30px 16px 10px 16px;
    }
  }
</style>
