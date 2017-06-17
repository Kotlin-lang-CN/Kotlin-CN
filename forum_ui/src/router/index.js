import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);

export default new Router({
  routes: [{
    path: '/',
    name: 'home',
    component: function (resolve) {
      require(['@/views/Home.vue'], resolve);
    }
  }, {
    path: '/edit',
    name: 'Edit',
    component: function (resolve) {
      require(['@/views/Edit.vue'], resolve);
    }
  }, {
    path: '/edit/:id',
    name: 'EditPost',
    component: function (resolve) {
      require(['@/views/Edit.vue'], resolve);
    }
  }, {
    path: '/post/:id',
    name: 'Topic',
    component: function (resolve) {
      require(['@/views/Post.vue'], resolve);
    }
  }]
})
