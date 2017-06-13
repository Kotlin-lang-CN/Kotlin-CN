<template>
  <div class="meta">
    <div class="category">{{ category }}</div>
    <button v-if="editable" v-on:click="showDialog"><i></i></button>
    <div class="title">{{ title }}</div>
    <div class="author">{{ author }}</div>
  </div>
</template>
<script>
  import LoginMgr from '../assets/js/LoginMgr.js';
  import Event from '../assets/js/Event.js';

  export default {
    data: function () {
      return {
        author: LoginMgr.info().username
      }
    },
    props: {
      category: '',
      title: '',
      tags: '',
      editable: false,
    },
    methods: {
      showDialog(){
        Event.emit('article-meta-edit', 'edit');
      }
    }
  }
</script>
<style scoped lang="less">
  .meta {
    position: relative;
    color: #666;
    border-bottom: 1px #e4e4e4 solid;
    padding: 70px 0 30px 0;
    margin-bottom: 16px;
    font-size: 24px;

    .category {
      color: #333;
      font-weight: bolder;
      display: inline-block;
    }
    .title {
      margin: 15px 0;
    }
    .author {
      font-size: 20px;
      color: #999;
    }
    button {
      position: absolute;
      right: 0;
      top: 60px;
      width: 36px;
      height: 36px;
      outline: none;
      border: 1px #ccc solid;
      background: white;
      cursor: pointer;
    }
  }
</style>
