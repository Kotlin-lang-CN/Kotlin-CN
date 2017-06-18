<template>
  <div id="edit-root">
    <nav>
      <div class="edit-title">
        <a href="/"><i class="logo"></i></a>
        <div class="right">
          <div class="btn"><span>更多<i class="choice-icon"></i></span>
            <div class="sub-menu">
              <section v-on:click="postCancel">放弃编辑</section>
              <section v-on:click="postDelete">删除文章</section>
            </div>
          </div>
          <button class="post" v-on:click="preparePostArticle" v-if="updateMode">确认修改</button>
          <button class="post" v-on:click="preparePostArticle" v-if="!updateMode">发布新话题</button>
        </div>
      </div>

      <div class="edit-operation">
        <ul>
          <li v-on:click="addStrong"><i class="strong"></i></li>
          <li v-on:click="addItalic"><i class="italic"></i></li>
          <li v-on:click="addLine"><i class="line"></i></li>
          <li v-on:click="addQuote"><i class="quote"></i></li>
          <li v-on:click="addCode"><i class="code"></i></li>
          <li v-on:click="addLink"><i class="link"></i></li>
          <li v-on:click="addImage"><i class="image"></i></li>
          <li v-on:click="addUl"><i class="un-order-list"></i></li>
          <li v-on:click="addOl"><i class="order-list"></i></li>
          <li v-on:click="addUndo"><i class="undo"></i></li>
          <li v-on:click="addRedo"><i class="redo"></i></li>
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
    <app-login></app-login>
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
  import Login from '../components/Login.vue';

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
        category: '',
        title: 'title',
        tags: '',
        updateMode: false,
        author: LoginMgr.username,
        input: '',
        categories: [],

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
      'app-login': Login,
      'display-panels': DisplayPanels
    },
    created: function () {
      this.articleId = this.$root.params.id;
      if (this.articleId !== undefined && this.articleId !== '') {
        this.updateMode = true;
        this.getArticle();
      }
      Event.on('article-meta-update', (obj) => {
        this.category = obj.category;
        this.title = obj.title;
        this.tags = obj.tags;
      });
      Event.on('article-meta-post', () => {
        this.postArticle();
      });
      this.getCategories();
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
      addUl: function () {
        insertContent('* ', this);
      },
      addOl: function () {
        insertContent('1. ', this);
      },
      addRedo: function () {

      },
      addUndo: function () {

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
        Net.get({
          url: Config.URL.article.detail.format(this.articleId),
          condition: {}
        }, (data) => {
          this.title = data.article.title;
          this.tags = data.article.tags;
          this.input = data.content.content;
          this.getCategories(data.article.category - 1);
        }, (resp) => {
          if (resp.code === 34) window.location.href = "/404"
        });
      },
      postCancel(){
        history.back();
      },
      postDelete(){
        if (this.articleId === undefined)return;
        Net.post({
          url: Config.URL.article.delete.format(this.articleId),
        }, (resp) => {
          if (resp.code === 0) {
            window.location.href = "/";
          }
        })
      },
      preparePostArticle(){
        Event.emit('article-meta-edit', 'post');
      },
      postArticle(){
        if (this.input.trim().length < 30) {
          layer.msg('文章文字不少于三十字');
          return;
        }
        let url = Config.URL.article.post;
        if (this.updateMode) {
          url = Config.URL.article.update.format(this.articleId);
        }
        let targetCategory = 0;
        for (let i = 0; i < this.categories.length; i++) {
          if (this.category === this.categories[i]) {
            targetCategory = i + 1;
          }
        }
        LoginMgr.require(() => {
          Net.post({
            url: url,
            condition: {
              title: this.title,
              category: targetCategory,
              content: this.input,
              tags: this.tags,
              author: LoginMgr.info().uid
            }
          }, (data) => {
            layer.msg('发布成功');
            this.articleId = data.id;
            history.back();
          });
        });
      },
      getCategories(index) {
        if (index === undefined) index = 0;
        if (!window.data) window.data = {};
        if (window.data.categories) {
          this.categories = window.data.categories;
          this.category = this.categories[index];
        } else {
          Net.get({url: Config.URL.article.categoryType}, (resp) => {
            window.data.categories = resp.category;
            this.categories = resp.category;
            this.category = this.categories[index];
          });
        }
      }
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
  .strong {
    background: url(../assets/img/toolbar_blod.png) no-repeat center;
  }

  .italic {
    background: url(../assets/img/toolbar_Italic.png) no-repeat center;
  }

  .line {
    background: url(../assets/img/toolbar_cut_off_rule.png) no-repeat center;
  }

  .quote {
    background: url(../assets/img/toolbar_paragraph_reference.png) no-repeat center;
  }

  .code {
    background: url(../assets/img/toolbar_pre.png) no-repeat center;
  }

  .link {
    background: url(../assets/img/toolbar_a.png) no-repeat center;
  }

  .image {
    background: url(../assets/img/toolbar_image.png) no-repeat center;
  }

  .un-order-list {
    background: url(../assets/img/toolbar_unordered_list.png) no-repeat center;
  }

  .order-list {
    background: url(../assets/img/toolbar_ordered_list.png) no-repeat center;
  }

  .redo {
    background: url(../assets/img/toolbar_next.png) no-repeat center;
  }

  .undo {
    background: url(../assets/img/toolbar_back.png) no-repeat center;
  }

  body {
    position: relative;
    margin: 0;
    font: 16px "Avenir", Helvetica, Arial, sans-serif;
    color: #666;
    #edit-root {
      min-width: 1050px;
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
      nav {
        min-width: 1050px;
        position: fixed;
        height: 140px;
        width: 100%;
        .edit-title {
          margin: auto;
          padding: 0 160px;
          height: 80px;
          line-height: 38px;
          color: #333;

          .logo {
            display: inline-block;
            width: 192px;
            height: 45px;
            margin-top: 21px;
            background: url(../assets/img/logo_big.png) no-repeat center;
            background-size: 99% 99%;
          }
          .right {
            display: inline-block;
            float: right;
            button {
              cursor: pointer;
              margin: 21px 0;
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

          .btn {
            position: relative;
            vertical-align: top;
            margin-right: 60px;
            height: 80px;
            display: inline-block;
            text-align: center;

            .sub-menu {
              display: none;
            }
            > span {
              margin-right: auto;
              display: block;
              line-height: 80px;
              min-height: 80px;
              width: 78px;
              i {
                margin-top: 24px;
              }
              .choice-icon {
                display: inline-block;
                width: 18px;
                height: 18px;
                vertical-align: bottom;
                margin-bottom: 12px;
                background: url(../assets/img/choice-icon.png) no-repeat;
              }
            }
            > span:hover {
              background-color: #f9f9f9;
            }
          }
          .btn:hover .sub-menu {
            margin-top: -15px;
            position: absolute;
            background-color: white;
            right: -1px;
            width: 182px;
            height: 88px;
            display: block;
            box-shadow: 0 0 10px #ccc;
            section {
              height: 44px;
              line-height: 52px;
              color: #333;
            }
            section:hover {
              background-color: #f8fbff;
              color: #2572e5;
            }
            section:nth-child(1) {
              border-bottom: 1px #e4e4e4 solid;
            }
          }
        }
        .edit-operation {
          border-top: 1px #f1f1f1 solid;
          padding: 0 160px;
          text-align: center;
          ul {
            list-style: none;
            margin: 0;
            padding: 0;
            width: auto;
            i {
              cursor: pointer;
              display: inline-block;
              width: 36px;
              height: 36px;
              background-size: 50% 50%;
            }
            i:hover {
              background-color: #f9f9f9;
            }
            li {
              float: left;
              width: 36px;
              height: 36px;
              margin: 12px;
            }
            li:hover {
              background-color: #f9f9f9;
            }
          }
        }
      }
      .main {
        position: absolute;
        top: 140px;
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
          width: 100%;
          height: 100%;
          box-sizing: border-box;
          overflow: auto;
          background-color: #f6f7f8;
          padding-left: 160px;
          padding-right: 54px;
        }
        .outside {
          width: 100%;
          height: 100%;
          box-sizing: border-box;
          overflow: auto;
          padding-right: 160px;
          padding-left: 54px;
        }
      }
    }
  }
</style>
