<template>
  <div id="edit-root">
    <nav>
      <div class="edit-title">
        <div class="logo"><b>Kotlin</b>China</div>
        <div class="right">
          <button class="give-up" v-on:click="postCancel">放弃编辑</button>
          <button class="post" v-on:click="postArticle">发布新话题</button>
        </div>
      </div>
      <div class="edit-operation">
        <ul>
          <li v-on:click="addStrong"><b>B</b></li>
          <li v-on:click="addItalic"><i>I</i></li>
          <li v-on:click="addLine">一</li>
          <li v-on:click="addQuote"><i></i></li>
          <li v-on:click="addCode"><i></i></li>
          <li v-on:click="addLink"><i></i></li>
          <li v-on:click="addImage"><i></i></li>
          <li v-on:click="addTable"><i></i></li>
          <li v-on:click="addUl"><i></i></li>
          <li v-on:click="addOl"><i></i></li>
        </ul>
      </div>
    </nav>
    <div class="main">
      <div class="inside" @keydown.9="tabFn" v-scroll="editScroll">
        <article-meta :category="category" :title="title" :tags="tags" :editable="true"></article-meta>
        <textarea name="" class="editor" v-model="input"></textarea>
      </div>
      <div class="outside" v-scroll="previewScroll">
        <article-meta :category="category" :title="title" :tags="tags"></article-meta>
        <display-panels :content="input"></display-panels>
      </div>
    </div>
    <article-meta-dialog :category="category" :title="title" :tags="tags"></article-meta-dialog>
  </div>
</template>
<script>
  import Config from "../assets/js/Config.js";
  import LoginMgr from "../assets/js/LoginMgr.js";
  import Util from "../assets/js/Util.js";
  import Net from "../assets/js/Net.js";
  import Event from "../assets/js/Event.js";
  import Vue from 'vue';
  import marked from 'marked';

  import scroll from 'vue-scroll';
  import range from '../../static/js/rangeFn.js';

  import InputTag from 'vue-input-tag';
  import DisplayPanels from '../components/DisplayPanels.vue';
  import ArticleMeta from '../components/ArticleMeta.vue';
  import ArticleMetaDialog from '../components/ArticleMetaDialog.vue';

  Vue.use(scroll);
  function insertContent(val, that) {
    let textareaDom = document.querySelector('.editor');
    let value = textareaDom.value;
    let point = range.getCursortPosition(textareaDom);
    let lastChart = value.substring(point - 1, point);
    let lastFourCharts = value.substring(point - 4, point);
    if (lastChart !== '\n' && value !== '' && lastFourCharts !== '    ') {
      val = '\n' + val;
      range.insertAfterText(textareaDom, val);
    } else {
      range.insertAfterText(textareaDom, val);
    }
    that.input = document.querySelector('.editor').value;
  }
  export default {
    data() {
      return {
        articleId: '',
        category:1,
        title: 'title',
        tags: '',
        updateMode: false,
        author: LoginMgr.username,
        input: '',

        editStatus: true,
        previewStatus: true,
        fullPageStatus: false,
        navStatus: true,
        icoStatus: true,
        maxEditScrollHeight: 0,
        maxPreviewScrollHeight: 0
      }
    },
    components: {
      ArticleMeta,
      'input-tag': InputTag,
      'article-meta': ArticleMeta,
      'article-meta-dialog': ArticleMetaDialog,
      'display-panels': DisplayPanels
    },
    created: function () {
      this.articleId = this.$root.params.id;
      if (this.articleId !== undefined && this.articleId !== '') {
        this.updateMode = true;
        this.getArticle();
      }
      Event.on('article-meta-update',(obj)=>{
          this.category = obj.category;
          this.title = obj.title;
          this.tag = obj.tag;
      })
    },
    methods: {
      tabFn: function (evt) {
        insertContent("    ", this);
        if (evt.preventDefault) {
          evt.preventDefault();
        } else {
          evt.returnValue = false;
        }
      },
      addImage: function (evt) {
        insertContent("![Vue](https://cn.vuejs.org/images/logo.png)", this);
      },
      addHTitle: function (index) {
        let tmp = '';
        switch (index) {
          case 1:
            tmp = '# ';
            break;
          case 2:
            tmp = '## ';
            break;
          case 3:
            tmp = '### ';
            break;
          case 4:
            tmp = '#### ';
            break;
          case 5:
            tmp = '##### ';
            break;
          case 6:
            tmp = '###### ';
            break;
          default:
            break;
        }
        insertContent(tmp, this);
      },
      addCode: function () {
        let textareaDom = document.querySelector('.editor');
        let value = textareaDom.value;
        let point = range.getCursortPosition(textareaDom);
        let lastChart = value.substring(point - 1, point);
        insertContent('```\n\n```', this);
        if (lastChart !== '\n' && value !== '') {
          range.setCaretPosition(textareaDom, point + 5);
        } else {
          range.setCaretPosition(textareaDom, point + 4);
        }
      },
      addStrikethrough: function () {
        let textareaDom = document.querySelector('.editor');
        let value = textareaDom.value;
        let point = range.getCursortPosition(textareaDom);
        let lastChart = value.substring(point - 1, point);
        insertContent('~~~~', this);
        if (lastChart !== '\n' && value !== '') {
          range.setCaretPosition(textareaDom, point + 3);
        } else {
          range.setCaretPosition(textareaDom, point + 2);
        }
      },
      addStrong: function () {
        let textareaDom = document.querySelector('.editor');
        let value = textareaDom.value;
        let point = range.getCursortPosition(textareaDom);
        let lastChart = value.substring(point - 1, point);
        insertContent('****', this);
        if (lastChart !== '\n' && value !== '') {
          range.setCaretPosition(textareaDom, point + 3);
        } else {
          range.setCaretPosition(textareaDom, point + 2);
        }
      },
      addItalic: function () {
        let textareaDom = document.querySelector('.editor');
        let value = textareaDom.value;
        let point = range.getCursortPosition(textareaDom);
        let lastChart = value.substring(point - 1, point);
        insertContent('**', this);
        if (lastChart !== '\n' && value !== '') {
          range.setCaretPosition(textareaDom, point + 2);
        } else {
          range.setCaretPosition(textareaDom, point + 1);
        }
      },
      addLine: function () {
        insertContent('\n----\n', this);
      },
      addLink: function () {
        insertContent("[Vue](https://cn.vuejs.org/images/logo.png)", this);
      },
      addQuote: function () {
        let textareaDom = document.querySelector('.editor');
        let value = textareaDom.value;
        let point = range.getCursortPosition(textareaDom);
        let lastChart = value.substring(point - 1, point);
        insertContent('> ', this);
        if (lastChart !== '\n' && value !== '') {
          range.setCaretPosition(textareaDom, point + 3);
        } else {
          range.setCaretPosition(textareaDom, point + 2);
        }
      },
      addTable: function () {
        insertContent('\nheader 1 | header 2\n', this);
        insertContent('---|---\n', this);
        insertContent('row 1 col 1 | row 1 col 2\n', this);
        insertContent('row 2 col 1 | row 2 col 2\n\n', this);
      },
      addUl: function () {
        insertContent('* ', this);
      },
      addOl: function () {
        insertContent('1. ', this);
      },
      previewScroll: function (e, position) {
        if (this.maxEditScrollHeight !== 0) {
          let topPercent = position.scrollTop / this.maxPreviewScrollHeight;
          document.querySelector('.inside').scrollTop = this.maxEditScrollHeight * topPercent;
        }
      },
      editScroll: function (e, position) {
        if (this.maxPreviewScrollHeight !== 0) {
          let topPercent = position.scrollTop / this.maxEditScrollHeight;
          document.querySelector('.outside').scrollTop = this.maxPreviewScrollHeight * topPercent;
        }
      },
      getArticle(){
        let request = {
          url: Config.URL.article.detail.format(this.articleId),
          type: "GET",
          condition: {}
        };
        Net.ajax(request, (data) => {
          this.title = data.article.title;
          this.tag = data.article.tags.split(';');
          this.input = data.content.content;
        })
      },
      postCancel(){
        history.back();
      },
      postArticle(){
        if (this.title.trim().length === 0
          || this.tag.length === 0
          || this.input.trim().length === 0) {
          layer.msg('文章信息不完整');
          return;
        }
        let url = Config.URL.article.post;
        if (this.updateMode) {
          url = Config.URL.article.update.format(this.articleId);
        }
        let tags = '';
        this.tag.forEach((t) => {
          tags += t;
          tags += ';'
        });
        tags = tags.substr(0, tags.length - 1);
        Net.post({
          url: url,
          condition: {
            title: this.title,
            category: this.category,
            content: this.input,
            tags: tags,
            author: LoginMgr.uid
          }
        }, (data) => {
          //TODO
          this.articleId = data.id;
          history.back();
        });
      },
    },
    watch: {
      input: function () {
        let data = {};
        this.$emit('childevent', data);
        let maxEditScrollHeight = document.querySelector('.inside').scrollHeight - document.querySelector('.inside').clientHeight;
        let maxPreviewScrollHeight = document.querySelector('.outside').scrollHeight - document.querySelector('.outside').clientHeight;
        this.maxEditScrollHeight = maxEditScrollHeight;
        this.maxPreviewScrollHeight = maxPreviewScrollHeight;
      }
    }
  }
</script>

<style lang="less" scoped>
  body {
    position: relative;
    margin: 0;
    font: 16px "Avenir", Helvetica, Arial, sans-serif;
    color: #666;
    #edit-root {
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
      nav {
        position: fixed;
        height: 116px;
        min-width: 360px;
        width: 100%;

        .edit-title {
          margin: auto;
          max-width: 1120px;
          padding: 0 16px;
          height: 86px;
          line-height: 38px;
          color: #333;
          .logo {
            display: inline-block;
            font-size: 20px;
            padding: 24px 0;
            b {
              color: #2572e5;
              display: inline-block;
              padding-right: 4px;
            }
          }
          .right {
            display: inline-block;
            float: right;
            cursor: pointer;
            padding: 24px 0;
            button {
              line-height: 26px;
              height: 38px;
              color: #333;
              padding: 6px 12px;
              background: transparent;
              outline: none;
              border: none;
              font-size: 16px;
            }
            .post {
              background-color: #2572e5;
              color: white;
            }
          }
        }
        .edit-operation {
          border-top: 1px #f1f1f1 solid;
          text-align: center;
          ul {
            list-style: none;
            margin: 0;
            padding: 0;
            width: auto;
            li {
              float: left;
              width: 30px;
              height: 30px;
              border-right: 1px #ccc solid;
            }
            li:hover {
              background-color: #f9f9f9;
            }
          }
        }

      }
      .main {
        position: absolute;
        top: 116px;
        left: 0;
        right: 0;
        bottom: 0;
        display: flex;

        textarea {
          outline: none;
          border: none;
          resize: none;
          width: 100%;
          background-color: transparent;
          font-size: 15px;
          color: #666;

          height: 20000px;
          overflow: hidden;
        }
        .inside {
          background-color: #f6f7f8;
        }
        .inside,
        .outside {
          width: 100%;
          height: 100%;
          box-sizing: border-box;
          overflow: auto;
          padding: 10px;
        }
      }
    }
  }
</style>
