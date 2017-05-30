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
        <textarea v-model="toReplyContent"></textarea>
        <div class="button" v-on:click="toReply">好了</div>
      </div>
      <div class="reply">
        <header>评论</header>
        <div class="item" v-for="value in reply">
          <div><span>{{ value.user.username }}</span> at <span>{{ value.meta.create_time }}</span></div>
          <div class="cont">{{value.content.content}}</div>
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
        reply: [],
        toReplyContent:''
      }
    },
    created(){
      this.id = this.$route.params.id;

      this.getArticle();

      debugger;
      this.getReply(0);
    },
    computed: {
      compiledMarkdown: function () {
        return marked(this.content, {sanitize: true})
      }
    },
    methods: {
      getArticle(){
        let url = Config.URL.article.detail.format(this.id);
        let request = {
          url: url,
          type: "GET",
          condition: {}
        };
        Net.ajax(request, (data) => {
          this.topic = data;
          this.content = data.content.content;
        })
      },
      getReply(index){
        let url = Config.URL.article.reply.format(this.id);
        let request = {
          url: url,
          type: "GET",
          condition: {
            offset: index,
            limit: 0
          }
        };
        Net.ajax(request, (data) => {
          debugger;
          let list = data.reply;
          if (!Array.isArray(list))return;
          if(index === 0){this.reply = [];}
          this.reply = Array.concat(this.reply, list);
          //TODO 循环取的时候长度有问题
          //if (list.length > 0) {
          //  this.getReply(this.reply.length);
          //}
        })
      },
      toReply(){
        if (this.toReplyContent.length === 0) {
          Event.emit("error", '评论不能为空');
          return;
        }
        let url = Config.URL.article.reply.format(this.id);
        let request = {
          url: url,
          type: "POST",
          condition: {
            content: this.toReplyContent
          }
        };
        Net.ajax(request, (data) => {
          if (data.length > 0) {
            this.getReply(0);
          }
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

  .to-reply {
    max-width: 600px;
    margin-top: 90px;
    text-align: left;
    textarea {
      width: 75%;
      min-height: 160px;
      padding: 10px;
      font-size: 14px;
      color: #666;
      resize: vertical;
    }
    .button {
      padding: 3px 18px;
      display: inline-block;
      vertical-align: top;
    }
  }

  .reply {
    text-align: left;
    margin-top: 30px;
    header {
      font-size: 20px;
      margin-bottom: 8px;
    }
    .item {
      border-top: 1px #f1f1f1 solid;
      padding: 16px 0px;
    }
    .cont {
      padding: 16px;
    }
  }
</style>
