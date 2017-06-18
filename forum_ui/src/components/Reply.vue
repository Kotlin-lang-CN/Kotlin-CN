<template>
  <div class="reply">
    <header>共收到{{ reply.length }}条评论</header>
    <ul>
      <li v-for="value in reply">
        <app-avatar :avatar="value.user.username" :size="'middle'"></app-avatar>
        <div class="cont">
          <div>
            <span>{{ value.user.username }}</span>
            <span>{{ value.meta.create_time | moment}}</span>
            <small v-if="showDelete(value) && value.meta.state == 0" v-on:click="deleteArticle(value)" class="delete">
              删除
            </small>
            <select v-on:change="updateState(value.meta)" v-model="value.meta.state" class="right" v-if="isAdmin">
              <option v-for="option in options" v-bind:value="option.value">
                {{ option.text }}
              </option>
            </select>
          </div>
          <span v-if="value.meta.state == 1 && !isAdmin">#该评论内容涉嫌违规已被冻结, 申诉请联系管理员#</span>
          <span v-if="value.meta.state == 2 && !isAdmin">#该评论已被删除#</span>
          <display-panels v-if="value.meta.state == 0 || isAdmin" :content="value.content.content"></display-panels>
        </div>
      </li>
    </ul>
    <button class="more" v-on:click="loadMore" v-if="hasMore">加载更多</button>
    <header>回帖</header>
    <markdown-comment :articleId="articleId"></markdown-comment>
  </div>
</template>

<script>
  import Config from "../assets/js/Config.js";
  import Event from "../assets/js/Event.js";
  import Net from "../assets/js/Net.js";
  import Util from "../assets/js/Util.js";
  import Comment from "./Comment.vue";
  import DisplayPanels from "./DisplayPanels.vue";
  import Avatar from "./Avatar.vue";
  import LoginMgr from '../assets/js/LoginMgr.js';

  export default {
    data () {
      return {
        isAdmin: LoginMgr.isAdmin(),
        topic: null,
        content: '',
        offset: 0,
        hasMore: true,
        reply: [],
        toReplyContent: '',
        options: [
          {text: '正常', value: 0},
          {text: '冻结', value: 1},
          {text: '删除', value: 2}
        ],
      }
    },
    props: {
      articleId: ''
    },
    components: {
      'markdown-comment': Comment,
      "display-panels": DisplayPanels,
      "app-avatar": Avatar
    },
    created(){
      this.getReply(0);
      Event.on('comment-change', () => this.getReply(0));
      Event.on('login', () => {
        this.isAdmin = LoginMgr.isAdmin();
        if (this.isAdmin) this.getReply(0);
      });
    },
    methods: {
      getReply(index){
        const limit = 20;
        Net.get({
          url: Config.URL.article.reply.format(this.articleId),
          condition: {offset: index, limit: limit}
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
      },
      updateState(reply) {
        Net.post({
          url: Config.URL.admin.updateReplyState.format(reply.id),
          condition: {state: reply.state},
        }, () => {
          window.console.log("success!")
        }, () => {
          this.get(this.requestUrl, 0)
        })
      },
      showDelete(reply) {
        let info = LoginMgr.info();
        return !info.isAdminRole && info.isLogin && info.uid === reply.user.uid
      },
      deleteArticle(reply){
        Net.post({url: Config.URL.article.deleteReply.format(reply.meta.id)}, () => reply.meta.state = 2)
      }
    }
  }
</script>

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
          width: 90%;
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

    li:hover .delete {
      display: inline-block;
    }
    .delete {
      color: red;
      display: none;
    }
  }
</style>

