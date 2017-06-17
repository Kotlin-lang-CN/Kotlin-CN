import Vue from 'vue'
import page from 'page'
import routes from './router/routes'
import Event from './assets/js/Event.js'
import moment from 'moment';

Vue.config.productionTip = false;

moment.locale('zh-cn');
Vue.filter('moment', function (value) {
  return moment(value).fromNow();
});

const app = new Vue({
  el: '#app',
  data: {
    ViewComponent: {render: h => h('div', 'loading...')}
  },
  render (h) {
    return h(this.ViewComponent)
  }
});

Object.keys(routes).forEach(route => {
  const Component = require('./views/' + routes[route] + '.vue');
  page(route, (ctx) => {
      app.$root.params = ctx.params;
      app.ViewComponent = Component;
      Event.emit('update','');
    }
  )
});

page('*', () => app.ViewComponent = require('./views/404.vue'));

page();
