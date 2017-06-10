<template>
  <div class="topic">
    <article v-if="topic !== null ">
      <header>
        <div>
          <span v-if="topic && categories.length >= topic.article.category">
            [{{ categories[topic.article.category - 1]}}]
          </span>{{ topic.article.title }}
          <small class="tag" v-for="tag in topic.article.tags.split(/;/)"> {{ tag }} </small>
        </div>
        <div>
          <span>{{ topic.author.username }}</span>于
          <span>{{ topic.article.create_time | moment}}</span>创建,
          <i v-if="topic.article.create_time !== topic.article.last_edit_time">
            最近更新于 <span>{{ topic.article.last_edit_time | moment}}</span>
          </i>
          <a :href="editUrl" v-if="editUrl !== ''" class="button">编辑</a>
        </div>
      </header>
      <section>
        <display-panels :content="content"></display-panels>
      </section>
      <app-reply :articleId="articleId"></app-reply>
    </article>
  </div>
</template>

<script>
  import LoginMgr from "../assets/js/LoginMgr.js";
  import Config from "../assets/js/Config.js";
  import Event from "../assets/js/Event.js";
  import Net from "../assets/js/Net.js";

  import DisplayPanels from '../components/DisplayPanels.vue';
  import Reply from '../components/Reply.vue';
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
      'app-reply': Reply,
      'display-panels': DisplayPanels
    },
    created(){
      this.id = this.$route.params.id;
      this.getArticle();
      this.getCategories()
    },
    mounted(){
      this.articleId = this.id;
    },
    methods: {
      getArticle(){
        let request = {
          url: Config.URL.article.detail.format(this.id),
          type: "GET",
          condition: {}
        };
        Net.ajax(request, (data) => {
          this.topic = data;
          this.content = data.content.content;
          if (LoginMgr.info() && (LoginMgr.info().uid === data.author.uid || LoginMgr.isAdmin())) {
            this.editUrl = Config.UI.edit + "/" + this.articleId;
          }
        })
      },
      getCategories() {
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
    }
  }
</script>
<style scoped lang="less">
  div.topic {
    text-align: left;
    margin: 30px auto 10px auto;
    article {
      max-width: 840px;
      padding: 0 16px;
      header {
        margin-bottom: 28px;
        > div:nth-child(1) {
          font-size: 25px;
        }
        > div:nth-child(2) {
          font-size: 16px;
        }
        span {
          color: #999;
          display: inline-block;
          margin-right: 6px;
          margin-left: 6px;
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
