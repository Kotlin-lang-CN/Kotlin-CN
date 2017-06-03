<template>
  <div class="topic">
    <article v-if="topic !== null ">
      <header>
        <div><span>{{ topic.article.tags }}</span>{{ topic.article.title }}</div>
        <div>
          <span>{{ topic.author.username }}</span>于
          <span>{{ topic.article.last_edit_time | moment}}</span>写下
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
        editUrl: ''
      }
    },
    components: {
      DisplayPanels,
      'app-reply': Reply,
      'display-panels': DisplayPanels
    },
    created(){
      this.id = this.$route.params.id;
      this.getArticle();
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
          if (LoginMgr.uid === data.article.author) {
            this.editUrl = Config.UI.edit + "/" + this.articleId;
          }
        })
      }
    }
  }
</script>
<style scoped lang="less">
  #app > div.topic {
    text-align: left;
    max-width: 600px;
    margin: 30px auto 10px auto;
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

  @media screen and (max-width: 480px) {
    #app > div.topic {
      margin: 30px 16px 10px 16px;
    }
  }
</style>
