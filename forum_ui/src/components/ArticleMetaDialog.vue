<template>
  <div class="dialog" v-if="show">
    <div class="bg" v-on:click="show = false"></div>
    <div class="cont">
      <input v-model="titleInput" type="text" placeholder="请输入文章标题"/>
      <input v-model="topicInput" type="text" placeholder="请输入／选择一个话题"/>
      <input-tag :tags="tagInput"></input-tag>
      <button v-on:click="save">OK</button>
    </div>
  </div>
</template>
<!--TODO 拦截器-->
<script>
  import InputTag from 'vue-input-tag';
  import Event from "../assets/js/Event.js";
  export default {
    data: function () {
      return {
        show: false,
        titleInput: '',
        topicInput: '',
        tagInput: [],
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
      })
    },
    methods: {
      save(){
        Event.emit('article-meta-update', {
          'category': this.topicInput,
          'title': this.titleInput,
          'tags': this.tagInput.toString(),//TODO
        });
        this.show = false;
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
