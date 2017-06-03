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
        <div class="previewContainer markdown-body">
          <div v-html="compiledMarkdown" ></div>
        </div>
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
  import hljs from '../../static/js/highlight.min.js'
  import range from '../../static/js/rangeFn.js'

  marked.setOptions({
    renderer: new marked.Renderer(),
    gfm: true,
    tables: true,
    breaks: false,
    pedantic: false,
    sanitize: true,
    smartLists: true,
    smartypants: false,
    highlight: function (code) {
      return hljs.highlightAuto(code).value
    }
  });
  export default {
    data () {
      return {
        id: '',
        topic: null,
        content: '',
        reply: [],
        toReplyContent: '',
        articleId: ''
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
        return marked(this.content, {
          sanitize: true
        })
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
<style scoped lang="less">
  #app > div {
    text-align: left;
  }

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
<style lang="scss" scoped>
  @import "../../static/css/reset.scss";
  @import "../../static/css/github-markdown.css";
  @import "../../static/css/atom-one-dark.min.css";
  #app > div {
    text-align: left;
  }
</style>
