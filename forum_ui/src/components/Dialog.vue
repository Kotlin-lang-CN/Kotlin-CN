<template>
  <div class="dialog" v-if="show">
    <div class="bg" v-on:click="doDismiss"></div>
    <div class="dialog-content">
      <h3>{{ dialog.title }}</h3>
      <p>{{ dialog.text }}</p>
      <button v-on:click="doConfirm">{{ dialog.confirm.text }}</button>
      <button v-on:click="doCancel">{{ dialog.cancel.text }}</button>
    </div>
  </div>
</template>

<script>
  import Event from '../assets/js/Event'

  export default {
    data() {
      return {
        show: false,
        dialog: {
          title: '',
          text: '',
          allow_dismiss: true,
          confirm: {text: '确定', action: undefined},
          cancel: {text: '取消', action: undefined},
        }
      }
    },
    created() {
      Event.on('alert', (dialog) => {
        this.dialog = dialog;
        this.show = true;
      })
    },
    methods: {
      doConfirm() {
        if (this.dialog.confirm.action) this.dialog.confirm.action();
        this.show = false
      },
      doCancel() {
        if (this.dialog.cancel.action) this.dialog.cancel.action();
        this.show = false
      },
      doDismiss() {
        if (this.allow_dismiss) doCancel();
      }
    }
  }
</script>

<style scoped>
  .dialog .dialog-content {
    margin-left: calc(50% - 200px);
  }
</style>

<style scoped lang="less">
  .dialog {
    position: fixed;
    z-index: 3;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;

    .bg {
      position: absolute;
      background: #000000;
      filter: alpha(opacity=50);
      -moz-opacity: 0.5;
      opacity: 0.5;
      width: 100%;
      height: 100%;
    }

    .dialog-content {
      position: absolute;
      width: 400px;
      margin-top: 200px;
      padding: 40px;
      box-sizing: border-box;
      background: white;
      border: 1px #f1f1f1 solid;
      box-shadow: 0 0 3px #2572e5;
    }
  }
</style>
