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
  import Util from '../assets/js/Util'

  const renderer = new marked.Renderer();

  renderer.heading = function (text, level) {
    const anchor = Util.anchorHash(text);
    return '<h{0}><a name="{1}" class="anchor" href="#{1}"><svg aria-hidden="true" class="octicon octicon-link" height="16" version="1.1" viewBox="0 0 16 16" width="16"><path fill-rule="evenodd" d="M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z"></path></svg></a>{2}</h{0}>'
      .format(level, anchor, text)
  };

  marked.setOptions({
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
          sanitize: true,
          renderer: renderer
        })
      }
    }
  }
</script>

<style lang="scss" scoped>
  @import "../../static/css/reset.scss";
  @import "../../static/css/github-markdown.css";
  @import "../../static/css/atom-one-dark.min.css";

  .display {
    text-align: left;
  }

  .anchor {

  }
</style>
