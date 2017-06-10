<template>
  <div class="comment">
    <div class="switcher">
      <button @click="editMode" v-bind:class="{ 'select': editStatus, 'normal': !editStatus }">编辑</button>
      <button @click="previewMode" v-bind:class="{ 'select': !editStatus, 'normal': editStatus }">预览</button>
    </div>
    <div class="content">
      <textarea v-bind:class="{ 'hide': !editStatus }" v-model="input"></textarea>
      <div class="previewContainer markdown-body" v-html="compiledMarkdown"
           v-bind:class="{ 'hide': editStatus }">
      </div>
    </div>
    <button @click="post" class="big-btn">发表评论</button>
  </div>
</template>
<script>
  import Config from "../assets/js/Config.js";
  import LoginMgr from "../assets/js/LoginMgr.js";
  import Util from "../assets/js/Util.js";
  import Net from "../assets/js/Net.js";
  import Event from "../assets/js/Event.js";
  import Vue from 'vue'
  import marked from 'marked'
  import scroll from 'vue-scroll'
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
    name: 'markdown',
    data() {
      return {
        title: '',
        tag: '',
        author: LoginMgr.username,
        input: '',
        editStatus: true,
      }
    },
    props: {
      articleId: ''
    },
    mounted(){
      this.editMode();
    },
    methods: {
      editMode() {
        this.editStatus = true;
      },
      previewMode () {
        this.editStatus = false;
      },
      post(){
        if (this.input.length === 0) {
          layer.msg("评论不能为空");
          return;
        } else if (this.input.length < 10) {
          layer.msg("评论不少于十个字");
          return;
        }

        let request = {
          url: Config.URL.article.reply.format(this.articleId),
          type: "POST",
          condition: {
            content: this.input
          }
        };
        Net.ajax(request, (data) => {
          if (data.id.length > 0) {
            this.input = '';
            Event.emit('comment-change');
            layer.msg("已评论");
          }
        })
      }
    },
    computed: {
      compiledMarkdown: function () {
        return marked(this.input, {
          sanitize: true
        })
      }
    },
    watch: {
      input: function () {
        let data = {};
        data.mdValue = this.input;
        data.htmlValue = marked(this.input, {
          sanitize: true
        });
        //adjust textarea height
        let editText = $('textarea');
        editText.css('height', 'auto').css('height', editText[0].scrollHeight);
      }
    }
  }
</script>
<style lang="scss" scoped>
  @import "../../static/css/reset.scss";
  @import "../../static/css/github-markdown.css";
  @import "../../static/css/atom-one-dark.min.css";

  #app > div {
    text-align: left;
  }

  .comment {
    max-width: 840px;
    background-color: #fcfcfe;
    border: 1px #e4e4e4 solid;

    .switcher {
      margin-top: 20px;
      margin-left: 20px;
      button {
        min-width: 64px;
        font-size: 18px;
      }
      .select {
        color: #2572e5;
        border-bottom: 4px #2572e5 solid;
      }
      .normal {
        color: #999;
        border-bottom: 4px transparent solid;
      }
    }
    .big-btn {
      min-height: 38px;
      line-height: 38px;
      display: block;
      margin-bottom: 40px;
      margin-top: 20px;
      margin-left: 20px;
      min-width: 120px;
      border-radius: 2px;
      font-size: 20px;
      color: white;
      background-color: #2572e5;
    }
    .content {
      margin: 0 20px;
      background: #fff;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-sizing: border-box;
      border: 1px #e4e4e4 solid;

      .hide{
        display: none;
      }

      textarea {
        box-sizing: border-box;
        color: #666;
        padding: 10px;
        width: 100%;
        overflow-y: hidden;
        background: transparent;
        outline: none;
        resize: none;
        min-height: 200px;
      }
      .previewContainer {
        width: 100%;
        min-height: 200px;
        box-sizing: border-box;
        background: #fff;
        overflow: auto;
        padding: 10px;
      }
    }
  }
</style>

