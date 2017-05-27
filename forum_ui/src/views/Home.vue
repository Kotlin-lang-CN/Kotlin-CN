<template>
  <div class="value-post">
    <div>精华帖</div>
    <ul>
      <li>
        <div>
          <div><span>TAB</span>LONG LONG TITLE</div>
          <div><span>SOMEONE</span>LATS POST AT 'WHEN'</div>
        </div>
        <div class="reply">18</div>
      </li>
    </ul>
    <div><a :href="urlTopics" title="">查看更多...</a></div>
  </div>
</template>
<script>
  import Config from '../assets/js/Config.js'
  export default {
    name: 'hello',
    data: function () {
      return {
        urlTopics:Config.UI.topics,
        msg: 'TODO:Home'
      }
    }
  }
</script>
<style scoped>

</style>
