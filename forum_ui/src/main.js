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

Array.prototype.indexOf = function(val) {
  for (let i = 0; i < this.length; i++) {
    if (this[i] === val) return i;
  }
  return -1;
};

Array.prototype.remove = function(val) {
  const index = this.indexOf(val);
  if (index > -1) {
    this.splice(index, 1);
  }
};

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
