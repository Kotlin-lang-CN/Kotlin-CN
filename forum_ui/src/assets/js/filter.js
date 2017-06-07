import Vue from 'vue';
import moment from 'moment';

moment.locale('zh-cn');
Vue.filter('moment', function (value, formatString) {
  //formatString = formatString || 'YYYY-MM-DD HH:mm:ss';
  //return moment(value).format(formatString);
  return moment(value).startOf('day').fromNow();
});


