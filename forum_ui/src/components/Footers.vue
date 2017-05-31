<template>
  <div class="foot" v-show="moduleShow">{{ msg }}</div>
</template>

<script>
  import Event from '../assets/js/Event.js';
  export default {
    name: 'foot',
    data: function () {
      return {
        msg: 'FOOTER',
        moduleShow: true
      }
    },
    created: function () {
      Event.on("fullscreen", (on) => {
        this.moduleShow = !on;
      })
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
