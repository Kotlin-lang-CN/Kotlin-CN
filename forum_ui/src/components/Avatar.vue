<template>
  <i class="avatar" :style="color"
     v-bind:class=" {'middle' : avatarSize==='middle' ,'small' : avatarSize === 'small','big' : avatarSize === 'big'}">{{ text
    }}</i>
</template>
<script>
  export default {
    data: function () {
      return {
        colors: ["#bdc0c5", "#c5d3e8", "#93a7c8", "#9baebe"],
        avatarSize: 'big',
        text: '',
        color: ''
      }
    },
    props: {
      avatar: '',
      size: ''
    },
    mounted(){
      if (this.size === 'small' || this.size === 'middle') {
        this.avatarSize = this.size;
      }
      if (this.avatar && this.avatar.length > 0) {
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
    color: white;
    text-align: center;
    font-style: normal;
    vertical-align: top;
    cursor: default;
  }

  .small {
    line-height: 32px;
    font-size: 24px;
    border-radius: 16px;
    width: 32px;
    min-width: 32px;
    height: 32px;
  }

  .middle {
    line-height: 49px;
    font-size: 40px;
    border-radius: 22px;
    width: 44px;
    min-width: 44px;
    height: 44px;
  }

  .big {
    line-height: 70px;
    font-size: 48px;
    border-radius: 30px;
    width: 60px;
    min-width: 60px;
    height: 60px;
  }
</style>
