<template>
  <div class="mdContainer" :class="{ fullPage: fullPageStatus }">
    <div class="navContainer" v-if="navStatus">
      <div class="nameContainer">Kotlin-CN</div>
      <div class="markContainer">
        <ul class="markListGroup">
          <li class="markListItem" @click="addStrong" title="strong"><b>B</b></li>
          <li class="markListItem" @click="addItalic" title="italic"><i>I</i></li>
          <li class="markListItem" @click="addStrikethrough" title="strikethrough"><i class="fa fa-strikethrough"
                                                                                      aria-hidden="true"></i></li>
          <li class="markListItem" @click="addHTitle(1)" title="H1-title">H1</li>
          <li class="markListItem" @click="addHTitle(2)" title="H2-title">H2</li>
          <li class="markListItem" @click="addHTitle(3)" title="H3-title">H3</li>
          <li class="markListItem" @click="addHTitle(4)" title="H4-title">H4</li>
          <li class="markListItem" @click="addHTitle(5)" title="H5-title">H5</li>
          <li class="markListItem" @click="addHTitle(6)" title="H6-title">H6</li>
          <li class="markListItem" @click="addLine" title="line">一</li>
          <li class="markListItem" @click="addQuote" title="quote"><i class="fa fa-quote-left" aria-hidden="true"></i>
          </li>
          <li class="markListItem" @click="addCode"><i class="fa fa-code" aria-hidden="true"></i></li>
          <li class="markListItem" @click="addLink"><i class="fa fa-link" aria-hidden="true"></i></li>
          <li class="markListItem" @click="addImage"><i class="fa fa-picture-o" aria-hidden="true"></i></li>
          <li class="markListItem" @click="addTable" title="table"><i class="fa fa-table" aria-hidden="true"></i></li>
          <li class="markListItem" @click="addUl" title="ul-list"><i class="fa fa-list-ul" aria-hidden="true"></i></li>
          <li class="markListItem" @click="addOl" title="ol-list"><i class="fa fa-list-ol" aria-hidden="true"></i></li>
          <li class="markListItem" @click="previewFn" title="preview"><i class="fa fa-eye-slash" aria-hidden="true"></i>
          </li>
          <li class="markListItem" @click="previewAllFn" title="previewAll"><i class="fa fa-eye" aria-hidden="true"></i>
          <li>
            <button v-on:click="postCancel">放弃编辑</button>
          </li>
          <li>
            <button v-on:click="postArticle">发布</button>
          </li>
        </ul>
      </div>
    </div>
    <div class="meta">
      <input-tag :tags="tag"></input-tag>
      <input v-model="title" type="text" name="text" placeholder="标题"/>
      <div>Author:{{ author }}</div>
    </div>
    <div class="mdBodyContainer">
      <div class="editContainer" v-if="editStatus">
        <textarea name="" class="mdEditor" @keydown.9="tabFn" v-scroll="editScroll" v-model="input"></textarea>
      </div>
      <div class="previewContainer markdown-body" v-scroll="previewScroll" v-html="compiledMarkdown"
           v-if="previewStatus">
      </div>
    </div>
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
  import InputTag from 'vue-input-tag'

  Vue.use(scroll);
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

  function insertContent(val, that) {
    let textareaDom = document.querySelector('.mdEditor');
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
    that.input = document.querySelector('.mdEditor').value;
  }
  export default {
    name: 'markdown',
    data() {
      return {
        articleId: '',
        title: '',
        tag: [],
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
      'input-tag': InputTag
    },
    created: function () {
      Event.emit('fullscreen', true);
      if (!this.editStatus && !this.previewStatus) {
        this.editStatus = true;
        this.previewStatus = true;
      }
      this.articleId = this.$root.params.id;
      if (this.articleId !== undefined && this.articleId !== '') {
        this.updateMode = true;
        this.getArticle();
      }
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
        let textareaDom = document.querySelector('.mdEditor');
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
        let textareaDom = document.querySelector('.mdEditor');
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
        let textareaDom = document.querySelector('.mdEditor');
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
        let textareaDom = document.querySelector('.mdEditor');
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
        let textareaDom = document.querySelector('.mdEditor');
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
      previewFn: function () {
        if (!this.editStatus) {
          this.editStatus = true;
          this.previewStatus = !this.previewStatus;
        } else {
          this.previewStatus = !this.previewStatus;
        }
      },
      previewAllFn: function () {
        if (!this.editStatus && this.previewStatus) {
          this.editStatus = true;
          this.previewStatus = true;
        } else {
          this.editStatus = false;
          this.previewStatus = true;
        }
      },
      previewScroll: function (e, position) {
        if (this.maxEditScrollHeight !== 0) {
          let topPercent = position.scrollTop / this.maxPreviewScrollHeight;
          document.querySelector('.mdEditor').scrollTop = this.maxEditScrollHeight * topPercent;
        }
      },
      editScroll: function (e, position) {
        if (this.maxPreviewScrollHeight !== 0) {
          let topPercent = position.scrollTop / this.maxEditScrollHeight;
          document.querySelector('.previewContainer').scrollTop = this.maxPreviewScrollHeight * topPercent;
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
            category: 1,
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
    computed: {
      compiledMarkdown: function () {
        return marked(this.input, {
          sanitize: true
        })
      }
    },
    destroyed(){
      Event.emit('fullscreen', false);
    },
    watch: {
      input: function () {
        let data = {};
        data.mdValue = this.input;
        data.htmlValue = marked(this.input, {
          sanitize: true
        });
        this.$emit('childevent', data);
        let maxEditScrollHeight = document.querySelector('.mdEditor').scrollHeight - document.querySelector('.mdEditor').clientHeight;
        let maxPreviewScrollHeight = document.querySelector('.previewContainer').scrollHeight - document.querySelector('.previewContainer').clientHeight;
        this.maxEditScrollHeight = maxEditScrollHeight;
        this.maxPreviewScrollHeight = maxPreviewScrollHeight;
      }
    }
  }
</script>

<style lang="scss" scoped>
  @import "../../static/css/reset.scss";
  @import "../../static/css/github-markdown.css";
  @import "../../static/css/atom-one-dark.min.css";

  div.mdContainer {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    text-align: left;
  }

  .mdContainer {
    width: 100%;
    height: 100%;
    background: lightblue;
    &.fullPage {
      position: fixed;
      z-index: 1000;
      left: 0;
      top: 0;
    }
    .navContainer {
      width: 100%;
      height: 36px;
      background: #fff;
      box-sizing: border-box;
      border-bottom: 1px solid #eee;
      display: flex;
      justify-content: flex-start;
      align-items: center;
      padding: 0 10px;
      .nameContainer {
        color: lightblue;
        margin-right: 10px;
        cursor: pointer;
      }
      .markContainer {
        width: auto;
        height: 100%;
        margin-left: 0px;
        ul.markListGroup {
          height: 100%;
          width: auto;
          display: flex;
          justify-content: flex-start;
          align-items: center;
          li.markListItem {
            list-style: none;
            width: 20px;
            height: 20px;
            margin: 0 2px;
            display: flex;
            justify-content: center;
            align-items: center;
            cursor: pointer;
            font-size: 12px;
            color: #333;
            &:hover {
              background: #eee;
            }
          }
        }
      }
    }
    .mdBodyContainer {
      width: 100%;
      height: calc(100% - 36px);
      background: #fff;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-sizing: border-box;
    }
  }

  .editContainer {
    height: 100%;
    width: 100%;
    box-sizing: border-box;
    border-right: 1px solid #ddd;
    color: #666;
    padding: 10px;
    .mdEditor {
      height: 100%;
      width: 100%;
      background: transparent;
      outline: none;
      resize: none;
    }
  }

  .previewContainer {
    width: 100%;
    height: 100%;
    box-sizing: border-box;
    background: #fff;
    overflow: auto;
    padding: 10px;
  }
</style>
