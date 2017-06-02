import Vue from 'vue'
import App from './App'
import router from './router'
import _ from './assets/js/filter.js'

Vue.config.productionTip = false;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  render: h => h(App)
});
