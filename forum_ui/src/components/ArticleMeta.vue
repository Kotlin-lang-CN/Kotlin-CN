<template>
  <div class="meta">
    <div class="title">
      <span class="category">
        {{ meta.categories.length > meta.category ? meta.categories[meta.category - 1] : ''}}
      </span>{{ meta.title }}
      <small class="tag" v-for="tag in meta.tags.split(/;/)"> #{{ tag }} </small>
    </div>
    <i v-if="editable" v-on:click="modify"></i>
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
      meta: {
        category: '',
        title: '',
        tags: '',
        categories: [],
      },
      editable: false,
    },
    methods: {
      modify(){
        Event.emit('edit-meta', {
          meta: this.meta,
          confirm: {
            text: '确认修改',
            action: (meta) => {
              this.meta.category = meta.category;
              this.meta.title = meta.title;
              this.meta.tags = meta.tag;
            }
          }
        })
      }
    }
  }
</script>
<style scoped lang="less">
  .meta {
    position: relative;
    color: #666;
    border-bottom: 1px #e4e4e4 solid;
    padding: 30px 0 30px 0;
    margin-bottom: 16px;
    font-size: 24px;

    .title {
      color: #333;
      font-weight: bolder;
      display: inline-block;
      font-size: 30px;
    }
    .category {
      display: inline-block;
      background-color: #2572e5;
      border-radius: 2px;
      color: white;
      margin-right: 5px;
      padding: 0 7px;
      font-size: 16px;
      vertical-align: top;
      margin-top: 12px;
    }
    .tag {
      font-size: 10px;
      display: inline-block;
      background-color: #c9dcf5;
      color: #333;
      padding: 0 2px;
      vertical-align: top;
      margin-top: 15px;
      line-height: 20px;
      margin-right: 8px;
    }
    .author {
      font-size: 20px;
      color: #999;
    }
    i {
      position: absolute;
      right: 0;
      top: 40px;
      width: 36px;
      height: 36px;
      cursor: pointer;
      display: block;
      background: url(../assets/img/edit-title.png) no-repeat center;
      background-size: 50% 50%;
    }
    i:hover {
      border-bottom: 1px #ccc solid;
    }
  }
</style>
