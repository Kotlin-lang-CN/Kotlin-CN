<template>
  <div class="reply">
    <div class="comments">
      <header>评论</header>
      <div class="item" v-for="value in reply">
        <div><span>{{ value.user.username }}</span> at <span>{{ value.meta.create_time | moment}}</span></div>
        <div class="cont">{{value.content.content}}</div>
      </div>
    </div>
    <markdown-comment
      :articleId="articleId"></markdown-comment>
  </div>
</template>
<script>
  import Config from "../assets/js/Config.js";
  import Event from "../assets/js/Event.js";
  import Net from "../assets/js/Net.js";
  import Util from "../assets/js/Util.js";
  import MdComment from "../components/MdComment.vue";

  export default {
    data () {
      return {
        topic: null,
        content: '',
        reply: [],
        toReplyContent: ''
      }
    },
    props: {
      articleId: ''
    },
    components: {
      'markdown-comment': MdComment
    },
    created(){
      this.getReply(0);
      Event.on('comment-change', () => {
        this.getReply(0);
      })
    },
    methods: {
      getReply(index){
        let request = {
          url: Config.URL.article.reply.format(this.articleId),
          type: "GET",
          condition: {
            offset: index,
            limit: 0
          }
        };
        Net.ajax(request, (data) => {
          let list = data.reply;
          if (!Array.isArray(list))return;
          if (index === 0) {
            this.reply = [];
          }
          this.reply = Array.concat(this.reply, list);
          //TODO 循环取的时候长度有问题
          //if (list.length > 0) {
          //  this.getReply(this.reply.length);
          //}
        })
      }
    }
  }
</script>
<style scoped lang="less">
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
