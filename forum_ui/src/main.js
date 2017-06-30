import page from 'page'
import routes from './router/routes'
import Event from './assets/js/Event.js'
import moment from 'moment';
import Vue from 'vue'

Vue.config.productionTip = false;

moment.locale('zh-cn');
Vue.filter('moment', function (value) {
  return moment(value).fromNow();
});

function isMobile() {
  let ua = navigator.userAgent;
  return ua.match(/(Android)[\s\/]+([\d\.]+)/) !== null
    || ua.match(/(iPad|iPhone|iPod)\s+OS\s([\d_\.]+)/) !== null
    || ua.match(/(Windows\s+Phone)\s([\d\.]+)/) !== null;
}

Array.prototype.indexOf = function (val) {
  for (let i = 0; i < this.length; i++) {
    if (this[i] === val) return i;
  }
  return -1;
};

Array.prototype.remove = function (val) {
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
  let Component;
  if (isMobile() && !route.startsWith("/m")) {
    Component = require(routes["/m" + route] + '.vue');
  } else {
    Component = require(routes[route] + '.vue');
  }
  page(route, (ctx) => {
      app.$root.params = ctx.params;
      app.ViewComponent = Component;
      Event.emit('route-update', '');
    }
  )
});

page('*', () => app.ViewComponent = require('./views/404.vue'));

page();
