<!--markdown文本 最小展示控件-->
<template>
  <div class="display">
    <div class="previewContainer markdown-body" v-html="compiledMarkdown"></div>
  </div>
</template>
<script>
  import marked from 'marked';
  import hljs from '../../static/js/highlight.min.js'
  import range from '../../static/js/rangeFn.js'
  marked.setOptions({
    renderer: new marked.Renderer(),
    gfm: true,
    tables: true,
    breaks: false,
    pedantic: false,
    sanitize: true,
    smartLists: true,
    smartypants: false,
    highlight: function (code) {
      return hljs.highlightAuto(code).value
    }
  });
  export default {
    props: {
      content: ''
    },
    computed: {
      compiledMarkdown: function () {
        return marked(this.content, {
          sanitize: true
        })
      }
    },
    methods: {}
  }
</script>

<style lang="scss" scoped>
  @import "../../static/css/reset.scss";
  @import "../../static/css/github-markdown.css";
  @import "../../static/css/atom-one-dark.min.css";
  .display{
    text-align: left;
  }
</style>
