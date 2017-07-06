<template>
  <app-layout>
    <div class="comments">
      <header>共收到{{ reply.length }}条评论</header>
      <ul>
        <li v-for="value in reply">
          <app-avatar :logo="value.user.logo" :username="value.user.username" :size="'middle'">
          </app-avatar>
          <div class="cont">
            <div>
              <span class="username">{{ value.user.username }}</span>
              <span class="timestamp right">评论于 {{ value.meta.create_time | moment}}</span>
            </div>
            <span v-if="value.meta.state == 1 && !isAdmin">#该评论内容涉嫌违规已被冻结, 申诉请联系管理员#</span>
            <span v-if="value.meta.state == 2 && !isAdmin">#该评论已被删除#</span>
            <display-panels v-if="value.meta.state == 0 || isAdmin" :content="value.content.content"></display-panels>
          </div>
        </li>
      </ul>
      <button class="more" v-on:click="loadMore" v-if="hasMore">加载更多</button>
    </div>
  </app-layout>
</template>

<script>
  import Net from "../assets/js/Net.js";
  import Config from "../assets/js/Config.js";
  import LoginMgr from '../assets/js/LoginMgr.js';

  import DisplayPanels from "../components/DisplayPanels.vue";
  import AppLayout from '../layout/AppMobileSun.vue';
  import Avatar from "../components/Avatar.vue";

  export default {
    components: {
      'app-layout': AppLayout,
      'markdown-comment': Comment,
      "display-panels": DisplayPanels,
      "app-avatar": Avatar
    },
    data () {
      return {
        articleId: this.$root.params.id,
        me: LoginMgr,
        topic: null,
        content: '',
        offset: 0,
        hasMore: true,
        reply: [],
        toReplyContent: '',
      }
    },
    created(){
      this.getReply(0);
    },
    methods: {
      getReply(index){
        const limit = 20;
        Net.get({
          url: Config.URL.reply.article.format(this.articleId),
          condition: {offset: index, limit: limit}
        }, (data) => {
          let list = data.reply;
          this.offset = data.next_offset;
          if (!Array.isArray(list)) return;
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

<style scoped lang="less">
  .comments {
    font-size: .28rem;
    background-color: #fcfcfc;
    color: #999;
    header {
      background-color: white;
      height: 30px;
      padding-left: 15px;
      margin-top: 10px;
      border-bottom: 1px #e3e3e3 solid;
    }
    li {
      padding: 15px 0;
      margin: 0 15px;
      border-bottom: 1px #e3e3e3 solid;
      display: flex;
      span {
        font-size: .28rem;
        display: inline-block;
        margin-right: 4px;
        margin-bottom: 6px;
      }
      .cont {
        margin-left: 8px;
        width: 90%;
      }
    }
  }

</style>
