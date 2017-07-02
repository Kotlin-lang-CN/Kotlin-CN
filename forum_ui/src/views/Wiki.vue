<template>
  <app-layout>
    <div class="wiki-root">
      <section class="toc">
        <display-panels :content="toc.content.content"></display-panels>
      </section>
      <section class="content">
        <display-panels :content="article.content.content"></display-panels>
      </section>
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
  import AppLayout from '../layout/AppWebFixContent.vue';
  import ArticleMeta from '../components/ArticleMeta.vue';
  import ArticleSideBar from '../components/ArticleSideBar.vue';

  export default {
    components: {
      'article-meta': ArticleMeta,
      'app-layout': AppLayout,
      'app-reply': Reply,
      'display-panels': DisplayPanels,
      'article-side': ArticleSideBar,
    },
    data () {
      return {
        id: this.$root.params.id,
        toc: {
          article: {title: ''},
          content: {content: ''},
          author: {uid: ''}
        },
        article: {
          article: {title: ''},
          content: {content: ''},
          author: {uid: ''}
        },
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
          this.asyncToc();
          this.asyncContent();
        });
      },
      asyncToc(){
        Net.get({url: Config.URL.article.detail.format(Config.Wiki.toc)}, (resp) => {
          this.toc = resp;
        }, (resp) => {
          if (resp.code === 34) window.location.href = "/404"
        });
      },
      asyncContent(){
        if (!this.id) {
          this.id = Config.Wiki.readme;
        }
        Net.get({url: Config.URL.article.detail.format(this.id)}, (resp) => {//文章信息
          this.article = resp;
          setTimeout(() => {
            const metaTitle = '【Kotlin-CN】' + resp.article.title + ' by ' + resp.author.username;
            this.seekAnchor();
            $("title").html(metaTitle);
          }, 200)
        }, (resp) => {
          if (resp.code === 34) window.location.href = "/404"
        });
      },
      seekAnchor() {//找到锚点并跳转
        const url = window.location.href, idx = url.indexOf("#");
        const anchor = idx !== -1 ? url.substring(idx + 1) : undefined;
        if (!anchor) {
          $('html, body').animate({scrollTop: 0}, 'fast');
        } else {
          const posNormal = $('[name="' + anchor + '"]').position();
          const posDecode = $('[name="' + Util.anchorHash(anchor) + '"]').position();
          const pos = posNormal ? posNormal : posDecode;
          $('html, body').animate({scrollTop: pos ? pos.top : 0}, 'fast');
        }
      }
    },
    computed: {
      showEdit() {
        return this.article && LoginMgr.isLogin && LoginMgr.uid === this.article.author.uid
          || LoginMgr.isAdminRole
      }
    },
  }
</script>

<style scoped lang="less">
  .wiki-root {
    position: absolute;
    width: 1120px;
    padding: 0 16px;
    margin: auto;
    display: flex;
    height: 100%;
    left: 0;
    right: 0;
    overflow: hidden;
    .toc {
      width: 28%;
      margin-right: 2%;
      overflow-y: scroll;
    }
    .content {
      width: 70%;
      overflow-y: scroll;
    }
  }
  @media screen and (max-width: 1152px) {
    .wiki-root{
      left: inherit;
      right: inherit;
      padding: 0;
    }
  }
</style>
