<template>
  <div class="foot">{{ msg }}</div>
</template>

<script>
  export default {
    name: 'foot',
    data: function () {
      return {
        msg: 'FOOTER'
      }
    }
  }
</script>

<style scoped>
  div {
    min-width: 320px;
    text-align: center;
    margin: auto;
    color: #999;
    margin-top: 48px;
  }
</style>
