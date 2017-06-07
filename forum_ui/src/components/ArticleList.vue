<template>
  <div>
    <div class="content">
      <div class="list" v-for="value in articles">
        <a :href="urlTopic + value.meta.id">
          <div class="footnote">最后由{{ value.last_editor.username }} 回复于 {{ value.meta.last_edit_time | moment}}</div>
          <i>K</i>
          <div class="aside">
            <div class="title">{{ value.meta.title }}</div>
            <div class="tag">{{ value.meta.tags }}</div>
            <div class="footnote right">{{ value.author.username }} 发布于 {{ value.meta.create_time | moment}}</div>
          </div>
        </a>
      </div>
    </div>
    <button v-on:click="loadMore" v-show="hasMore">加载更多</button>
  </div>
</template>

<style scoped lang="less">
  .content {
    .list a {
      display: block;
      padding: 16px 16px;
      text-align: left;
      border-top: 1px solid #f1f1f1;
      background: white;
      .footnote{
        font-size: 12px;
        color: #999;
      }
      i{
        display: inline-block;
        width: 60px;
        height: 60px;
        color: white;
        line-height: 60px;
        background-color: #2D93CA;
        text-align: center;
        font-size: 50px;
        font-style: normal;
        border-radius: 30px;
        vertical-align: top;
        margin-top: 4px;
      }
      .aside{
        display: inline-block;
        padding-top: 10px;
        width: 85%;
        .title{
          line-height: 28px;
          font-size: 24px;
          color: #333;
        }
        .tag{
          display: inline-block;
          font-size: 16px;
          color: #999;
        }
        .right{
          float: right;
        }
      }

    }
    button {
      background-color: #f2f7fd;
      outline: none;
      border: 1px #6ba0f1 solid;
      border-radius: 4px;
      margin: 16px 0;
      display: block;
      text-align: center;
      height: 50px;
      width: 100%;
      padding: 8px;
      color: #6ba0f1;
      font-size: 16px;
    }
  }

</style>
<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";
  import Event from "../assets/js/Event.js";

  export default {
    data() {
      return {
        loading: false,
        urlTopic: Config.UI.topic + '/',
        articles: [],
        offset: 0,
        hasMore: false
      }
    },
    props: {
      requestUrl: ''
    },
    methods: {
      get(url, offset){
        let request = {
          url: url,
          type: "GET",
          condition: {
            'offset': offset,
            'limit': 20
          }
        };
        Net.ajax(request, (data) => {
          if (offset === 0) {
            this.articles = [];
          }
          this.hasMore = data.articles.length !== 0;
          this.articles = this.articles.concat(data.articles);
          this.offset = data.next_offset;
        })
      },
      loadMore(){
        this.get(this.requestUrl, this.offset);
      }
    },
    watch: {
      requestUrl: function (newValue, oldValue) {
        if (newValue !== oldValue) {
          this.get(newValue, 0);
        }
      }
    }
  }
</script>

