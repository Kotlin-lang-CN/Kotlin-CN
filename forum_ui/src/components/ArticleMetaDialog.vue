<template>
  <div class="dialog" v-if="show">
    <div class="bg" v-on:click="show = false"></div>
    <div class="cont">
      <input v-model="titleInput" type="text" placeholder="请输入文章标题"/>
      <select v-model="categoryInput">
        <option v-for="category in categories" :value="category">{{ category }}</option>
      </select>
      <input-tag :tags="tagInput"></input-tag>
      <button v-on:click="save">OK</button>
    </div>
  </div>
</template>
<script>
  import InputTag from 'vue-input-tag';
  import Event from "../assets/js/Event.js";
  import Net from "../assets/js/Net.js";
  import Config from "../assets/js/Config.js";
  export default {
    data: function () {
      return {
        show: false,
        titleInput: '',
        categoryInput: '',
        tagInput: [],
        categories: []
      }
    },
    props: {
      category: '',
      title: '',
      tags: ''
    },
    components: {
      'input-tag': InputTag
    },
    created(){
      Event.on("article-meta-edit", () => {
        this.show = true;
      });
      this.getCategories();
    },
    mounted(){
      this.titleInput = this.title;
      this.categoryInput = this.category;
      if (this.tags !== undefined) {
        this.tagInput = this.tags.split(';');
      }
    },
    methods: {
      save(){
        let tags = '';
        this.tagInput.forEach((t) => {
          tags += t;
          tags += ';'
        });
        tags = tags.substr(0, tags.length - 1);
        Event.emit('article-meta-update', {
          'category': this.categoryInput,
          'title': this.titleInput,
          'tags': tags
        });
        this.show = false;
      },
      getCategories() {
        if (!window.data) window.data = {};
        if (window.data.categories) {
          this.categories = window.data.categories;
        } else {
          Net.get({url: Config.URL.article.categoryType}, (resp) => {
            window.data.categories = resp.category;
            this.categories = resp.category;
          });
        }
      }
    },
    watch: {
      title(){
        this.titleInput = this.title;
      },
      category(){
        this.categoryInput = this.category;
      },
      tags(){
        if (this.tags !== undefined) {
          this.tagInput = this.tags.split(';');
        }
      }
    }
  }
</script>
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
      left: 50%;
      top: 300px;
      width: 480px;
      margin-left: -240px;
      background: white;
      border: 1px #f1f1f1 solid;
      box-shadow: 0 0 10px #2e8ded;
      input {
        display: block;
      }
    }
  }
</style>
