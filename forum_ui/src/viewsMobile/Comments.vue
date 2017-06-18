<template>
  <app-layout>
    <div class="comments">
      <header>共收到{{ reply.length }}条评论</header>
      <ul>
        <li v-for="value in reply">
          <app-avatar :avatar="value.user.username" :size="'middle'"></app-avatar>
          <div class="cont">
            <div>
              <span>{{ value.user.username }}</span>
              <span>{{ value.meta.create_time | moment}}</span>
              <small v-if="showDelete(value)" v-on:click="deleteArticle(value)">删除</small>
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
    </div>
    <div class="input-box">
      <input type="text" name="input" placeholder="评论"/>
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
    data () {
      return {
        id: '',
        articleId: '',
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
    components: {
      AppLayout,
      "display-panels": DisplayPanels,
      "app-avatar": Avatar
    },
    mounted(){
      this.articleId = this.id;
    },
    created(){
      this.id = this.$root.params.id;
      this.getReply(0);
    },
    methods: {
      getReply(index){
        const limit = 20;
        Net.get({
          url: Config.URL.article.reply.format(this.id),
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
  .comments {
    font-size: .28rem;
    background-color: #fcfcfc;
    color: #999;
    header {
      background-color: white;
      line-height: 32px;
      height: 32px;
      padding-left: 15px;
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

  //TODO under-coding
  .input-box {
    display: none;
    position: fixed;
    bottom: 0;
    right: 0;
    left: 0;
    height: 45px;
    line-height: 45px;
    padding: 5px 15px;
    border-top: 1px #e3e3e3 solid;
    background-color: #fafbfc;
    input {
      border-radius: 2px;
      display: block;
      box-sizing: border-box;
      width: 100%;
      height: 100%;
      font-size: .28rem;
      color: #999;
      padding: 0 10px;
    }
  }
</style>
