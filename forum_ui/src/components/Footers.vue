<template>
  <div class="foot" v-show="moduleShow">{{ msg }}</div>
</template>

<script>
  import Event from '../assets/js/Event.js';
  export default {
    name: 'foot',
    data: function () {
      return {
        msg: 'Not Routinism，Think Different'.toUpperCase(),
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
    margin: 90px auto 0px auto;
    color: #999;
  }
</style>
