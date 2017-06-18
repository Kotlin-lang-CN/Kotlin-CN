<template>
  <app-layout>
    <div class="topic">
      <article v-if="topic !== null ">
        <header>
          <div>
          <span v-if="topic && categories.length >= topic.article.category" class="category">
            {{ categories[topic.article.category - 1]}}
          </span>{{ topic.article.title }}
            <small class="tag" v-for="tag in topic.article.tags.split(/;/)"> {{ tag }} </small>
          </div>
          <div>
            <label
              v-if="topic.article.create_time !== topic.article.last_edit_time">最近更新于{{ topic.article.last_edit_time | moment}}，
            </label>
            <span>{{ topic.author.username }}</span> 发布于 {{ topic.article.create_time | moment}}
            <a :href="editUrl" v-if="editUrl !== ''" class="edit">编辑</a>
          </div>
        </header>
        <section>
          <display-panels :content="content"></display-panels>
        </section>
        <app-reply :articleId="articleId"></app-reply>
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
  import AppLayout from '../layout/AppWeb.vue'

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
      'display-panels': DisplayPanels
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
        padding-top: 30px;
        padding-bottom: 24px;
        margin-bottom: 20px;
        > div:nth-child(1) {
          font-size: 24px;
          margin-bottom: 12px;
          color: #333;

          .category {
            display: inline-block;
            background-color: #2572e5;
            border-radius: 2px;
            color: white;
            margin-right: 10px;
            padding: 0 7px;
            font-size: 16px;
            vertical-align: top;
            margin-top: 5px;
          }
        }
        > div:nth-child(2) {
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
  }

  @media screen and (max-width: 480px) {
    #app > div.topic {
      margin: 30px 16px 10px 16px;
    }
  }
</style>
