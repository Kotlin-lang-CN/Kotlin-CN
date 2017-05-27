<template>
  <div>
    <article v-if="topic !== null ">
      <header>
        <div><span>{{ topic.article.tags }}</span><b>{{ topic.article.title }}</b></div>
        <div><span>{{ topic.author.username }}</span>LATS-EDIT BY {{ topic.last_editor.username
          }} AT {{ topic.article.last_edit_time }}
        </div>
      </header>
      <section>
        <!--<div v-html="compiledMarkdown"></div>-->
        {{ topic.content.content }}
      </section>
    </article>
  </div>
</template>
<script>
  import Config from "../assets/js/Config.js";
  import Event from "../assets/js/Event.js";
  import Util from "../assets/js/Util.js";
  import Net from "../assets/js/Net.js";
//  import marked from 'marked';
  export default {
    data () {
      return {
        topic: null,
        content:'',
        id: ''
      }
    },
    created(){
      let id = this.$route.params.id;
      this.id = id;
      let request = {
        url: Config.URL.article.detail.format(id),
        type: "GET",
        condition: {}
      };
      Net.ajax(request, (data) => {
        this.topic = data;
        //this.content = "dsdsd";
      })
    },
//    computed: {
//      compiledMarkdown: function () {
//        return marked(this.content, { sanitize: true })
//      }
//    },
    methods:{

    }
  }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
