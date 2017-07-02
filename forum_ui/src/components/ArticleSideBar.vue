<template>
  <div class="side-bar">
    <div class="part">共 {{flowers}} 个赞</div>
    <div class="button" v-if="!me.isLogin || !star_state" v-on:click="star">赞</div>
    <div class="button" v-if="me.isLogin && !!star_state" v-on:click="unstar">取消赞</div>
    <span class="button" v-on:click="scrollToTop">回到顶部</span>
    <span class="button" v-on:click="scrollToButton">去评论</span>
  </div>
</template>

<script>
  import Login from '../assets/js/LoginMgr'
  import Net from '../assets/js/Net'
  import Config from '../assets/js/Config'
  import Event from '../assets/js/Event'

  export default {
    data() {
      return {
        me: Login,
        flowers: 0,
        star_state: undefined,
      }
    },
    props: {id: '',},
    created() {
      this.getStarState();
      Event.on('login', this.getStarState);
      this.getCount();
    },
    methods: {
      scrollToTop() {
        $('html, body').animate({scrollTop: 0}, 'fast');
      },
      scrollToButton() {
        $('html, body').animate({scrollTop: 100000}, 'fast');
      },
      getStarState() {
        if (!Login.isLogin) {
          this.star_state = undefined;
          return
        }
        Net.get({url: Config.URL.flower.starArticle.format(this.id)}, (resp) => this.star_state = resp.has_star)
      },
      getCount() {
        Net.get({
          url: Config.URL.flower.countArticle,
          condition: {ids: this.id}
        }, (resp) => this.flowers = resp.data[this.id])
      },
      star() {
        Login.require(() => {
          Net.post({url: Config.URL.flower.starArticle.format(this.id)}, () => {
            this.star_state = true;
            this.getCount()
          })
        })
      },
      unstar() {
        Login.require(() => {
          Net.post({url: Config.URL.flower.unstarArticle.format(this.id)}, () => {
            this.star_state = false;
            this.getCount()
          })
        })
      }
    }
  }
</script>

<style scoped lang="less">
  .side-bar {
    border: 1px #f1f1f1 solid;
    border-radius: 10px;
    padding-top: 10px;
    background-color: white;
  }
</style>
