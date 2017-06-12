<template>
  <div class="meta">
    <div class="category">{{ category }}</div>
    <button v-if="editable" v-on:click="showDialog"><i></i></button>
    <div>{{ title }}</div>
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
      category:'',
      title:'',
      tags:'',
      editable: false,
    },
    methods: {
      showDialog(){
        Event.emit('article-meta-edit','');
      }
    }
  }
</script>
<style scoped lang="less">
  .meta {
    color: #666;
    border-bottom: 1px #ccc solid;
    padding: 16px;
    margin-bottom: 16px;
    line-height: 20px;

    .category{
      color: #333;
      display: inline-block;
    }
    .author{
      font-size: 12px;
      color: #999;
    }
    button{
        float: right;
    }
  }
</style>
