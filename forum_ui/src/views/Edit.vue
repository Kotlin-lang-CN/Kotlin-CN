<template>
  <div class="edit">
    <div class="meta">
      <div>
        <input v-model="title" type="text" name="text" placeholder="标题"/>
        <input v-model="tag" type="text" name="tag" placeholder="标签"/>
      </div>
      <div class="button" v-on:click="postArticle">好了</div>
    </div>
    <div class="editor">
      <div><textarea :value="input" @input="update"></textarea></div>
      <div v-html="compiledMarkdown"></div>
    </div>
  </div>
</template>
<script>
  import Config from "../assets/js/Config.js";
  import LoginMgr from "../assets/js/LoginMgr.js";
  import Event from "../assets/js/Event.js";
  import Util from "../assets/js/Util.js";
  import Net from "../assets/js/Net.js";
  import marked from 'marked';
  import _ from 'lodash';
  export default {
    data() {
      return {
        title: '',
        tag: '',
        input: 'Write here'
      }
    },
    computed: {
      compiledMarkdown: function () {
        return marked(this.input, {sanitize: true})
      }
    },
    methods: {
      postArticle(){
        if (this.title.trim().length === 0
          || this.tag.trim().length === 0
          || this.input.trim().length === 0) {
          Event.emit("error", '文章信息不完整');
          return;
        }
        let request = {
          url: Config.URL.article.post,
          type: "POST",
          condition: {
            title: this.title,
            category: 1,
            content: this.input,
            tags: this.tag,
            author: LoginMgr.uid
          }
        };
        Net.ajax(request, (data) => {
          let articleId = data.id;
          //TODO
          //preview
        });
      },
      update: _.debounce(function (e) {
        this.input = e.target.value
      }, 300)
    }
  }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
  /**TODO***/
  /***换页面根模版**/
  /*.header {*/
  /*display: none;*/
  /*}*/
  /*.foot {*/
  /*display: none;*/
  /*}*/
</style>
<style scoped lang="less">
  #app > div.edit {
    max-width: 100%;
  }

  .meta {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    background: white;

    height: 70px;
    /*max-width: 600px;*/
    margin: auto;
    > div {
      display: inline-block;
    }
    > div:nth-child(1) {
      height: 70px;
      width: 80%;
      line-height: 30px;
    }
    > div input {
      display: block;
      line-height: 30px;
      padding: 0;
      margin: 0;
      width: 100%;
      border: none;
      outline: none;
    }
    .button {
      vertical-align: top;
    }
  }

  .editor {
    position: absolute;
    top: 70px;
    bottom: 0;
    left: 0;
    right: 0;
    font-family: 'Helvetica Neue', Arial, sans-serif;
    color: #333;
    textarea {
      border-radius: 0;
      border-right: 1px solid #ccc;
      resize: none;
      outline: none;
      background-color: #f6f6f6;
      font-size: 14px;
      font-family: 'Monaco', courier, monospace;
      width: 100%;
      height: 100%;
      padding: 20px;
      box-sizing: border-box;
    }
    > div {
      background-color: #f3f5f6;
      position: absolute;
      right: 50%;
      left: 0;
      top: 0;
      bottom: 0;
    }
    > div:nth-child(2) {
      left: 50%;
      right: 0;
      padding: 20px;
    }
    code {
      color: #f66;
    }
  }
</style>
