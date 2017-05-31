<template>
  <div>
    <div class="list" v-for="value in articles">
      <a :href="urlTopic + value.meta.id">
        <div><span>{{ value.meta.tags }}</span><b>{{ value.meta.title }}</b></div>
        <div><span>{{ value.author.username }}</span>LATS-EDIT BY {{ value.last_editor.username
          }} AT {{ value.meta.last_edit_time }}
        </div>
      </a>
    </div>
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
        articles: []
      }
    },
    props: {
      requestUrl: '',
      requestOffset: 0
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
          this.articles = data.articles;
        })
      }
    },
    watch: {
      requestUrl: function (newValue, oldValue) {
        if (newValue !== oldValue) {
          this.get(newValue, 0);
        }
      },
      requestOffset:function(newValue,oldValue){
        if (newValue !== oldValue) {
          this.get(this.requestUrl, newValue);
        }
      }
    }
  }
</script>
<style scoped lang="less">
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
</style>
