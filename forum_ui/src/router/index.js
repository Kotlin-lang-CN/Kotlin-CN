import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

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
    path: '/account',
    name: 'Account',
    component: function (resolve) {
      require(['@/views/Account.vue'], resolve);
    }
  }, {
    path: '/register',
    name: 'Register',
    component: function (resolve) {
      require(['@/views/Register.vue'], resolve);
    }
  }, {
    path: '/login',
    name: 'Login',
    component: function (resolve) {
      require(['@/views/Login.vue'], resolve);
    }
  }, {
    path: '/topic/:id',
    name: 'Topic',
    component: function (resolve) {
      require(['@/views/Topic.vue'], resolve);
    }
  },{
    path: '/topics',
    name: 'Topics',
    component: function (resolve) {
      require(['@/views/Topics.vue'], resolve);
    }
  }, {
    path: '/manager',
    name: 'Manager',
    component: function (resolve) {
      resolve(['@/views/Manager.vue'], resolve)
    }
  }
  ]
})
