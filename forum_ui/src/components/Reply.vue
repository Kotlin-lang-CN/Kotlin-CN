<template>
  <div class="reply">
    <header>共收到{{ reply.length }}条评论</header>
    <ul>
      <li v-for="value in reply">
        <app-avatar :avatar="value.user.username"></app-avatar>
        <div class="cont">
          <div><span>{{ value.user.username }}</span><span>{{ value.meta.create_time | moment}}</span></div>
          <display-panels :content="value.content.content"></display-panels>
        </div>
      </li>
    </ul>
    <button class="more" v-on:click="loadMore" v-if="hasMore">加载更多</button>
    <header>回帖</header>
    <markdown-comment
      :articleId="articleId"></markdown-comment>
  </div>
</template>

<style scoped lang="less">
  .reply {
    text-align: left;
    margin-top: 30px;
    border-top: 1px #f1f1f1 solid;

    header {
      font-size: 14px;
      color: #999;
      margin-top: 30px;
      margin-bottom: 20px;
    }
    header:nth-child(2) {
      margin-top: 44px;
    }

    ul {
      border-top: 1px #e1e1e1 solid;
      border-left: 1px #e1e1e1 solid;
      border-right: 1px #e1e1e1 solid;
      background-color: #fcfcfe;
      padding: 0;
      margin: 0;
      li {
        border-bottom: 1px #f1f1f1 solid;
        padding: 16px 20px;
        display: flex;
        span {
          font-size: 12px;
          color: #999;
          display: inline-block;
          margin-right: 4px;
        }
        .cont {
          margin-left: 8px;
        }
      }
    }
    .more {
      background-color: #f2f7fd;
      outline: none;
      border: 1px #6ba0f1 solid;
      border-radius: 4px;
      display: block;
      text-align: center;
      height: 50px;
      line-height: 46px;
      padding: 0;
      width: 100%;
      color: #6ba0f1;
      font-size: 16px;
    }
  }
</style>
<script>
  import Config from "../assets/js/Config.js";
  import Event from "../assets/js/Event.js";
  import Net from "../assets/js/Net.js";
  import Util from "../assets/js/Util.js";
  import Comment from "./Comment.vue";
  import DisplayPanels from "./DisplayPanels.vue";
  import Avatar from "./Avatar.vue";

  export default {
    data () {
      return {
        topic: null,
        content: '',
        offset: 0,
        hasMore: true,
        reply: [],
        toReplyContent: ''
      }
    },
    props: {
      articleId: ''
    },
    components: {
      'markdown-comment': Comment,
      "display-panels": DisplayPanels,
      "app-avatar": Avatar,
    },
    created(){
      this.getReply(0);
      Event.on('comment-change', () => {
        this.getReply(0);
      })
    },
    methods: {
      getReply(index){
        const limit = 20;
        Net.ajax({
          url: Config.URL.article.reply.format(this.articleId),
          type: "GET",
          condition: {
            offset: index,
            limit: limit
          }
        }, (data) => {
          let list = data.reply;
          this.offset = data.next_offset;
          if (!Array.isArray(list))return;
          if (index === 0) this.reply = [];
          this.reply = Array.concat(this.reply, list);
          this.hasMore = list.length >= limit;
        })
      },
      loadMore(){
        this.getReply(this.offset);
      }
    }
  }
</script>

