<template>
  <div class="topic">
    <article v-if="topic !== null ">
      <header>
        <div>{{ topic.article.title }}</div>
        <div><span>{{ topic.article.tags }}</span></div>
        <div>
          <span>{{ topic.author.username }}</span>AT<span>{{ topic.article.last_edit_time }}</span>
        </div>
      </header>
      <section>
        <div v-html="compiledMarkdown"></div>
      </section>
      <div class="to-reply">
        <textarea></textarea>
      </div>
      <div class="reply">
        <header>评论</header>
        <div class="item" v-for="value in reply">
          <div><span>{{ value.user.username }}</span> at <span>{{ value.meta.create_time }}</span></div>
          <div class="cont">{{value.text_conten.content}}</div>
        </div>
      </div>
    </article>
  </div>
</template>
<script>
  import Config from "../assets/js/Config.js";
  import Event from "../assets/js/Event.js";
  import Util from "../assets/js/Util.js";
  import Net from "../assets/js/Net.js";
  import marked from 'marked';
  export default {
    data () {
      return {
        id: '',
        topic: null,
        content: '',
        reply: {}
      }
    },
    created(){
      let id = this.$route.params.id;
      this.id = id;
      let request = {
        url: Config.URL.article.detail.format(id),
        type: "GET",
        condition: {}
      };
      Net.ajax(request, (data) => {
        this.topic = data;
        this.content = data.content.content;
      })
    },
    computed: {
      compiledMarkdown: function () {
        return marked(this.content, {sanitize: true})
      }
    },
    methods: {}
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
  .to-reply{
    margin-top: 60px;
    textarea{
      width: 400px;
      min-height: 160px;
      padding: 10px;
      font-size: 14px;
      color: #666;
      resize: vertical;
    }
  }
  .reply{
    text-align: left;
    margin-top: 30px;
    header{
      font-size: 20px;
      margin-bottom: 8px;
    }
    .item{
      border-top: 1px #f1f1f1 solid;
      padding: 16px 0px;
    }
    .cont{
      padding: 16px;
    }
  }
</style>
