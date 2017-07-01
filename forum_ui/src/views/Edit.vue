<template>
  <div id="edit-root">
    <nav>
      <div class="edit-title">
        <a href="/"><i class="logo"></i></a>
        <div class="right">
          <div class="btn"><span>更多<i class="choice-icon"></i></span>
            <div class="sub-menu">
              <section v-on:click="cancelEdit">放弃编辑</section>
              <section v-on:click="deleteArticle">删除文章</section>
            </div>
          </div>
          <button class="post" v-on:click="publish"> {{ updateMode ? '确认修改' : '确认发布' }}</button>
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
          <!--<li v-on:click="addUndo"><i class="undo"></i></li>-->
          <!--<li v-on:click="addRedo"><i class="redo"></i></li>-->
        </ul>
      </div>
    </nav>
    <div class="main">
      <div class="inside" @keydown.9="tabFn">
        <article-meta :editable="true" :meta.sync="meta">
        </article-meta>
        <textarea name="" class="editor" v-model="input"></textarea>
      </div>
      <div class="outside">
        <article-meta :meta.sync="meta">
        </article-meta>
        <display-panels :content="input"></display-panels>
      </div>
    </div>
    <article-meta-dialog></article-meta-dialog>
    <common-dialog></common-dialog>
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

  import range from '../../static/js/rangeFn.js';

  import InputTag from 'vue-input-tag';
  import DisplayPanels from '../components/DisplayPanels.vue';
  import ArticleMeta from '../components/ArticleMeta.vue';
  import ArticleMetaDialog from '../components/ArticleMetaDialog.vue';
  import Dialog from '../components/Dialog.vue'
  import Login from '../assets/js/LoginMgr'

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
    components: {
      'common-dialog': Dialog,
      'article-meta': ArticleMeta,
      'input-tag': InputTag,
      'article-meta-dialog': ArticleMetaDialog,
      'display-panels': DisplayPanels
    },
    data() {
      const articleId = this.$root.params.id;
      return {
        id: articleId,
        updateMode: articleId && articleId !== '',
        meta: {
          categories: [],
          category: 1,
          title: '',
          tags: '',
        },
        input: '',
      }
    },
    created: function () {
      new Promise((next) => {
        Net.get({url: Config.URL.article.categoryType}, (resp) => {
          this.meta.categories = resp.category;
          if (this.updateMode) next()
        })
      }).then(() => {
        Net.get({url: Config.URL.article.detail.format(this.id)}, (resp) => {
          this.meta.title = resp.article.title;
          this.meta.tags = resp.article.tags;
          this.input = resp.content.content;
        }, (resp) => {
          if (resp.code === 34) window.location.href = "/404"
        });
      });
      Event.on("error", (err) => layer.msg(err));
    },
    methods: {
      /*basic control*/
      cancelEdit() {
        Event.emit('alert', {
          title: '放弃修改',
          text: '确认放弃对【' + this.title + '】的修改?',
          allow_dismiss: true,
          confirm: {
            text: '确定', action: () => window.location.href = "/"
          },
          cancel: {text: '取消', action: () => false},
        });
      },
      deleteArticle(){
        if (this.id === undefined || this.id === '')
          return;

        Event.emit('alert', {
          title: '撤销发布',
          text: '确认撤销发布【' + this.title + '】?',
          allow_dismiss: true,
          confirm: {
            text: '确定', action: () =>
              Net.post({url: Config.URL.article.delete.format(this.id),}, () => window.location.href = "/")
          },
          cancel: {text: '取消', action: () => false},
        })
      },
      publish(){
        if (this.input.trim().length < 30) {
          layer.msg('文章内容不得少于30字');
          return;
        }
        new Promise((next) => {
          if (this.meta.title.trim() === '') {
            Event.emit('edit-meta', {
              meta: this.meta,
              confirm: {
                text: '发布',
                action: (meta) => {
                  window.console.log(meta);
                  this.meta.category = meta.category;
                  this.meta.title = meta.title;
                  this.meta.tags = meta.tag;
                  next()
                }
              }
            })
          } else {
            next()
          }
        }).then(() => {
          Net.post({
            url: this.updateMode ? Config.URL.article.update.format(this.id) : Config.URL.article.post,
            condition: {
              title: this.meta.title,
              category: this.meta.category,
              content: this.input,
              tags: this.meta.tags,
              author: Login.info().uid
            }
          }, (resp) => {
            if (resp.id) this.id = resp.id;
            layer.msg(this.updateMode ? '修改成功' : '发布成功');
            setTimeout(() => {
              window.location.href = '/post/' + this.id
            }, 500)
          });
        });
      },

      /*markdown input control*/
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
      addLink: function () {
        insertContent("[Vue](https://cn.vuejs.org/images/logo.png)", this);
      },
      addImage: function () {
        insertContent("![Vue](https://cn.vuejs.org/images/logo.png)", this);
      },
      addUl: function () {
        insertContent('* ', this);
      },
      addOl: function () {
        insertContent('1. ', this);
      },

      /*unused method*/
      //addRedo: function () {
      //
      //},
      //addUndo: function () {
      //
      //},
      //tabFn: function (evt) {
      //  insertContent("    ", this);
      //  if (evt.preventDefault) {
      //    evt.preventDefault();
      //  } else {
      //    evt.returnValue = false;
      //  }
      //},
      //addHTitle: function (index) {
      //  let tmp = '';
      //  switch (index) {
      //    case 1:
      //      tmp = '# ';
      //      break;
      //    case 2:
      //      tmp = '## ';
      //      break;
      //    case 3:
      //      tmp = '### ';
      //      break;
      //    case 4:
      //      tmp = '#### ';
      //      break;
      //    case 5:
      //      tmp = '##### ';
      //      break;
      //    case 6:
      //      tmp = '###### ';
      //      break;
      //    default:
      //      break;
      //  }
      //  insertContent(tmp, this);
      //},
      //addStrikeThrough: function () {
      //  let textareaDom = document.querySelector('.editor');
      //  let value = textareaDom.value;
      //  let point = range.getCursortPosition(textareaDom);
      //  let lastChart = value.substring(point - 1, point);
      //  insertContent('~~~~', this);
      //  if (lastChart !== '\n' && value !== '') {
      //    range.setCaretPosition(textareaDom, point + 3);
      //  } else {
      //    range.setCaretPosition(textareaDom, point + 2);
      //  }
      //},
    },
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
