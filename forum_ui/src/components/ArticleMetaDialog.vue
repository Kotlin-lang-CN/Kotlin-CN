<template>
  <div class="dialog" v-if="show">
    <div class="bg" v-on:click="show = false"></div>
    <div class="cont">
      <input v-model="dialog.meta.title" type="text" placeholder="请输入文章标题"/>
      <!--suppress HtmlFormInputWithoutLabel -->
      <select v-model="dialog.meta.category">
        <!--suppress CommaExpressionJS -->
        <option v-for="(category, offset) in dialog.meta.categories" v-bind:value="offset + 1">{{ category }}</option>
      </select>
      <input-tag :tags="tag"></input-tag>
      <button v-on:click="execute">{{ dialog.confirm.text }}</button>
    </div>
  </div>
</template>
<script>
  import InputTag from 'vue-input-tag';
  import Event from "../assets/js/Event.js";
  import Net from "../assets/js/Net.js";
  import Config from "../assets/js/Config.js";

  export default {
    components: {'input-tag': InputTag},
    data: function () {
      return {
        show: false,
        tag: [],
        dialog: {
          meta: {
            title: '',
            category: 1,
            tags: [],
            categories: [],
          },
          confirm: {text: '', action: undefined}
        }
      }
    },
    created(){
      Event.on('edit-meta', (args) => {
        window.console.log(args);
        this.dialog.meta = args.meta;
        this.dialog.confirm = args.confirm;
        this.tag = [];
        if (args.meta.tags !== undefined) {
          args.meta.tags.split(';').forEach((t) => {
            if (t.length > 0) this.tag.push(t)
          });
        }
        this.show = true
      });
    },
    methods: {
      getTags(){
        let result = '';
        this.tag.forEach((t) => {
          result += t;
          result += ';';
        });
        return result.substr(0, result.length - 1);
      },
      execute(){
        if (this.dialog.confirm.action) {
          this.dialog.confirm.action({
            title: this.dialog.meta.title,
            category: this.dialog.meta.category,
            tag: this.getTags()
          })
        }
        this.show = false;
      }
    }
  }
</script>
<style scoped>
  .dialog .cont {
    margin-left: calc(50% - 253px);
  }

  @media screen and (max-width: 480px) {
    .dialog div.cont {
      margin-left: calc(50% - 153px);
      margin-top: 10%;
      width: 300px;
      padding: 16px;
    }
  }
</style>
<style scoped lang="less">
  .dialog {
    position: fixed;
    z-index: 3;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    .bg {
      position: absolute;
      background: #000000;
      filter: alpha(opacity=50);
      -moz-opacity: 0.5;
      opacity: 0.5;
      width: 100%;
      height: 100%;
    }
    .cont {
      position: absolute;
      width: 506px;
      margin-top: 200px;
      padding: 40px;
      box-sizing: border-box;
      background: white;
      border: 1px #f1f1f1 solid;
      box-shadow: 0 0 3px #2572e5;

      input {
        display: block;
        margin: 20px auto 10px auto;
        border: 1px #ddd solid;
        border-radius: 3px;
        outline: none;
        width: 100%;
        height: 60px;
        font-size: 20px;
        padding: 0 10px;
        box-sizing: border-box;
      }
      input:hover {
        -webkit-box-shadow: 0 0 3px #2e8ded;
      }
      input::-webkit-input-placeholder {
        color: #999;
      }
      input::-moz-placeholder {
        color: #999;
      }
      input:-webkit-autofill {
        -webkit-box-shadow: 0 0 0 1000px #fff inset;
      }

      .vue-input-tag-wrapper {
        margin: 20px 0 30px 0;
        padding: 8px 16px;
        line-height: 30px;
      }

      button {
        cursor: pointer;
        margin-bottom: 30px;
        width: 100%;
        height: 60px;
        background-color: #2572e5;
        color: white;
        font-size: 24px;
        border: none;
        outline: none;
      }
    }
  }
</style>
