<template>
  <div class="app-root">
    <input type="checkbox" id="drawer-toggle" name="drawer-toggle"/>
    <label for="drawer-toggle" id="drawer-toggle-label"></label>

    <header id="root-header">
      <span class="logo"><b>Kotlin</b> CHINA</span>
    </header>

    <nav id="drawer">
      <app-drawer></app-drawer>
    </nav>

    <div ref="page" id="page-content">
      <div class="app">
        <slot></slot>
      </div>
      <app-foot></app-foot>
    </div>

    <app-login></app-login>
  </div>
</template>

<script>
  import Event from '../assets/js/Event.js';
  import Footer from '../components/Footers.vue';
  import Login from '../componentsMobile/Login.vue';
  import Drawer from '../componentsMobile/Drawer.vue';

  export default {
    name: 'app',
    components: {
      'app-foot': Footer,
      'app-login': Login,
      'app-drawer': Drawer
    },
    data(){
      return {
        isTop: true
      }
    },
    mounted() {
      Event.on("error", (err) => {
        layer.msg(err)
      });

      this.page = this.$refs.page;
      this.page.addEventListener('scroll', () => {
        if (this.page.scrollTop === 0) {
          this.isTop = true;
          Event.emit('page-scroll', this.isTop);
        } else {
          if (this.isTop) {
            this.isTop = false;
            Event.emit('page-scroll', this.isTop);
          }
        }
      }, false);
    }
  }
</script>

<style scoped>
  .app-root {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    overflow: hidden;
  }

  .app-root * {
    -webkit-box-sizing: border-box;
    -moz-box-sizing: border-box;
    -o-box-sizing: border-box;
    box-sizing: border-box;
    -webkit-transition: .25s ease-in-out;
    -moz-transition: .25s ease-in-out;
    -o-transition: .25s ease-in-out;
    transition: .25s ease-in-out;
    margin: 0;
    padding: 0;
    -webkit-text-size-adjust: none;
  }

  #root-header {
    width: 100%;
    position: fixed;
    background-color: white;
    left: 0;
    z-index: 0;
  }

  #drawer {
    position: fixed;
    top: 0;
    left: -300px;
    height: 100%;
    width: 300px;
    background: #46546c;
    overflow-x: hidden;
    overflow-y: scroll;
    padding: 20px;
    -webkit-overflow-scrolling: touch;
  }

  #drawer-toggle-label {
    -webkit-touch-callout: none;
    -webkit-user-select: none;
    -khtml-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    left: 0;
    height: 60px;
    width: 50px;
    position: fixed;
    background: rgba(255, 255, 255, .0);
    z-index: 1;
  }

  #drawer-toggle-label:before {
    content: '';
    display: block;
    position: absolute;
    height: 2px;
    width: 24px;
    background: #8d8d8d;
    left: 15px;
    top: 23px;
    box-shadow: 0 6px 0 #8d8d8d, 0 12px 0 #8d8d8d;
  }

  #page-content {
    width: 100%;
    height: calc(100% - 50px);
    -webkit-overflow-scrolling: touch;
    box-sizing: border-box;
    position: absolute;
    top: 60px;
    bottom: 0;
    right: 0;
    left: 0;
  }

  @media all and (-webkit-transform-3d) {
    #page-content {
      overflow-y: scroll;
      overflow-x: hidden;
    }
  }

  #drawer-toggle {
    position: absolute;
    opacity: 0;
  }

  #drawer-toggle:checked ~ #drawer-toggle-label {
    height: 100%;
    width: calc(100% - 300px);
    background: rgba(255, 255, 255, .8);
  }

  #drawer-toggle:checked ~ #drawer-toggle-label,
  #drawer-toggle:checked ~ #root-header {
    left: 300px;
  }

  #drawer-toggle:checked ~ #drawer {
    left: 0;
  }

  #drawer-toggle:checked ~ #page-content {
    margin-left: 300px;
  }

  @media screen and (max-width: 480px) {
    #drawer-toggle:checked ~ #drawer-toggle-label {
      height: 100%;
      width: 50px;
    }

    #drawer-toggle:checked ~ #drawer-toggle-label,
    #drawer-toggle:checked ~ #root-header {
      left: calc(100% - 50px);
    }

    #drawer-toggle:checked ~ #drawer {
      width: calc(100% - 50px);
      padding: 20px;
    }

    #drawer-toggle:checked ~ #page-content {
      margin-left: calc(100% - 50px);
    }
  }

  .logo {
    color: #6f6f6f;
    font-size: 20px;
    line-height: 60px;
    height: 60px;
    padding-left: 60px;
  }

  .logo b {
    color: #2572e5;
  }
</style>

<!--global style-->
<style lang="less">
  html {
    font-size: 13.333vw;
  }
  body {
    margin: 0;
    font: 16px "Avenir", Helvetica, Arial, sans-serif;
    .app {
      -webkit-font-smoothing: antialiased;
      -moz-osx-font-smoothing: grayscale;
      text-align: center;
      color: #2c3e50;
    }
    a {
      text-decoration: none;
      font-weight: normal;
      color: #333;
    }
    ul {
      margin: 0;
      padding: 0;
    }

    li {
      list-style: none;
      padding: 0;
      margin: 0;
    }
    textarea {
      outline: none;
      border-radius: 8px;
    }
    .button, button {
      line-height: 36px;
      height: 36px;
      color: #2572e5;
      padding: 6px 12px;
      background: transparent;
      outline: none;
      border: none;
      font-size: 16px;
    }
  }
</style>
