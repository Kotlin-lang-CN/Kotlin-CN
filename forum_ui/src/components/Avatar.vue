<template>
  <i class="avatar" :style="color">{{ text }}</i>
</template>
<script>
  export default {
    data: function () {
      return {
        colors: ["#94a8c9", "#9caebe", "#bdc0c5", "black", "gray"],
        text: '',
        color: ''
      }
    },
    props: {
      avatar: ''
    },
    mounted(){
      if (this.avatar.length > 0) {
        this.text = this.avatar.charAt(0).toUpperCase();
        this.color = "background-color:"
          + this.colors[this.avatar.charCodeAt(0) % this.colors.length];
      }
    }
  }
</script>
<style scoped>
  .avatar {
    display: inline-block;
    width: 44px;
    min-width: 44px;
    height: 44px;
    color: white;
    line-height: 50px;
    text-align: center;
    font-size: 40px;
    font-style: normal;
    border-radius: 30px;
    vertical-align: top;
  }
</style>
