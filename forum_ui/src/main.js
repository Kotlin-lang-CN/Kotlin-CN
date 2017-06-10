import Vue from 'vue'
import App from './App.vue'
import router from './router'
import moment from 'moment';

Vue.config.productionTip = false;

moment.locale('zh-cn');
Vue.filter('moment', function (value) {
  //formatString = formatString || 'YYYY-MM-DD HH:mm:ss';
  //return moment(value).format(formatString);
  return moment(value).startOf('day').fromNow();
});

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  render: h => h(App)
});
