<template>
  <div>
    <div class="content">
      <div class="list" v-for="value in articles">
        <a :href="urlTopic + value.meta.id">
          <div><span>{{ value.meta.tags }}</span><b>{{ value.meta.title }}</b></div>
          <div><span>{{ value.author.username }}</span>at {{ value.meta.last_edit_time | moment}}
          </div>
        </a>
      </div>
    </div>
    <button v-on:click="loadMore" v-show="hasMore">查看更多...</button>
  </div>
</template>

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
<style scoped lang="less">
  .content {
    .list a {
      display: block;
      padding: 16px 16px;
      text-align: left;
      border-top: 1px solid #f1f1f1;
      background: white;
      > div:nth-child(1) {
        margin-bottom: 8px;
        span {
          background: aliceblue;
          padding: 6px 3px;
          border-radius: 6px;
          margin-right: 8px;
        }
      }
      > div:nth-child(2) {
        color: #666;
        font-size: 12px;
        span {
          color: #999;
          display: inline-block;
          padding-right: 6px;
        }
      }
    }
    button {
      background: white;
      outline: none;
      border: none;
      margin: 16px;
      display: block;
      text-align: left;
      padding: 8px;
      color: #eb5424;
      font-size: 14px;
    }
  }

</style>
