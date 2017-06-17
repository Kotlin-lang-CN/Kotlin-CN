<template>
  <app-layout>
    <div class="topic">
      <article v-if="topic !== null ">
        <header>
          <section>
            <app-avatar :avatar="topic.author.username" :size="'small'"></app-avatar>
            <span class="name">{{topic.author.username}}</span>
            <a :href="editUrl" v-if="editUrl !== ''" class="edit">编辑</a>
            <i class="fine"></i>
          </section>
          <section>
            <span v-if="topic && categories.length >= topic.article.category" class="category">
            {{ categories[topic.article.category - 1]}}
          </span>
            <span class="title">{{ topic.article.title }}</span>
          </section>
          <section>
            <span class="tag" v-for="tag in topic.article.tags.split(/;/)">{{ '#' + tag + '&nbsp' }}</span>
          </section>
          <section class="right">发布于 {{ topic.article.create_time | moment}}</section>
        </header>

        <section>
          <display-panels :content="content"></display-panels>
        </section>
      </article>

      <div class="footer"><i class="icon-com"></i>评论</div>
    </div>
  </app-layout>
</template>

<style scoped lang="less">
  .fine {
    display: inline-block;
    width: 30px;
    height: 30px;
    background: url(../assets/img/fine.png) no-repeat;
  }

  div.topic {
    text-align: left;
    margin: 0 auto 10px auto;
    article {
      max-width: 840px;
      padding: 0 16px;
      header {
        border-bottom: 1px #e4e4e4 solid;
        padding-top: 30px;
        padding-bottom: 24px;
        margin-bottom: 20px;
        .edit {
          padding: 0 20px;
          color: red;
          display: inline-block;
        }
      }
    }
  }

  .footer {
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
      display: inline-block;
      width: 20px;
      height: 20px;
      border: 1px #ccc solid;
      margin-top: 10px;
      vertical-align: top;
      margin-right: 4px;
    }
  }
</style>


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
        id: '',
        topic: null,
        content: '',
        reply: [],
        toReplyContent: '',
        articleId: '',
        editUrl: '',
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
      Event.on('update', () => {
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
          this.renderEditUrl()
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
