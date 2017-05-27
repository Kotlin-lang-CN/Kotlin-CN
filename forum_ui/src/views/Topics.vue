<template>
  <div>
    <div class="sub-nav">
      <div v-on:click="selectDefault">默认</div>
      <div v-on:click="selectFine">优质</div>
      <div v-on:click="selectNew">最新</div>
      <div v-on:click="selectCustom">自定义</div>
    </div>
    <div class="content">
      <div class="post">
        <div class="list" v-for="value in articles">
          <a :href="urlTopic + value.meta.id">
            <div><span>{{ value.meta.tags }}</span><b>{{ value.meta.title }}</b></div>
            <div><span>{{ value.author.username }}</span>LATS-EDIT BY {{ value.last_editor.username
              }} AT {{ value.meta.last_edit_time }}
            </div>
          </a>
        </div>
        <div class="page">
          <div>ONE</div>
          <div>TWO</div>
          <div>THREE</div>
        </div>
      </div>
      <div class="right-nav">
        <div class="button" v-on:click="createPost">发布新话题</div>
      </div>
    </div>
  </div>
</template>
<style scoped lang="less">
  .sub-nav {
    min-width: 320px;
    background: beige;
    > div {
      display: inline-block;
      padding: 8px 10px;
    }
  }

  .page {
    > div {
      display: inline-block;
      padding: 16px;
    }
  }

  .content {
    padding: 16px;
    box-sizing: border-box;
  }

  .list a {
    display: block;
    padding: 16px 16px;
    text-align: left;
    border-bottom: 1px solid #f1f1f1;
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

  .post {
    float: left;
    width: 75%;
  }

  .right-nav {
    padding: 8px 0;
    background: white;
    float: right;
    width: 23%;
  }

  .button {
    line-height: 36px;
    border-radius: 5px;
    height: 36px;
    background: #eb5424;
    border-color: #f1f1f1;
    color: white;
    margin: 0 6px;
  }

</style>
<script>
  import Config from "../assets/js/Config.js";
  import Net from "../assets/js/Net.js";

  export default {
    data() {
      return {
        msg: 'TODO:Topics',
        urlTopic: Config.UI.topic + '/',
        articles: {}
      }
    },
    created() {
      this.get(Config.URL.article.list, 0);
    },
    methods: {
      selectDefault(){
        this.get(Config.URL.article.list, 0);
      },
      selectFine(){
        this.get(Config.URL.article.fine, 0);
      },
      selectNew(){
        this.get(Config.URL.article.list, 0);
      },
      selectCustom(){
        let id = "custom";
        this.get(Config.URL.article.category.format(id), 0);
      },
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
      },
      createPost(){
        location.href = Config.UI.edit;
      }
    }
  }
</script>

