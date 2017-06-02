<template>
  <div class="topic">
    <article v-if="topic !== null ">
      <header>
        <div>{{ topic.article.title }}</div>
        <div><span>{{ topic.article.tags }}</span></div>
        <div>
          <span>{{ topic.author.username }}</span>at<span>{{ topic.article.last_edit_time | moment}}</span>
        </div>
      </header>
      <section>
        <div v-html="compiledMarkdown"></div>
      </section>
      <app-reply :articleId="articleId"></app-reply>
    </article>
  </div>
</template>
<script>
  import Config from "../assets/js/Config.js";
  import Event from "../assets/js/Event.js";
  import Net from "../assets/js/Net.js";
  import marked from 'marked';
  import Reply from '../components/Reply.vue';
  export default {
    data () {
      return {
        id: '',
        topic: null,
        content: '',
        reply: [],
        toReplyContent: '',
        articleId:''
      }
    },
    components: {
      'app-reply': Reply
    },
    created(){
      this.id = this.$route.params.id;

      this.getArticle();
    },
    mounted(){
      this.articleId = this.id;
    },
    computed: {
      compiledMarkdown: function () {
        return marked(this.content, {sanitize: true})
      }
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
        })
      }
    }
  }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="less">
  .topic {
    max-width: 600px;
    margin: 30px auto 10px auto;
    header {
      > div:nth-child(1) {
        font-size: 25px;
      }
      > div:nth-child(3) {
        font-size: 12px;
      }
      span {
        color: #999;
        display: inline-block;
        margin-right: 6px;
        margin-left: 6px;
      }
    }
  }
</style>
