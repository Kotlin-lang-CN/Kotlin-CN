import Vue from 'vue'
import Router from 'vue-router'
import Edit from '@/components/Edit'
import Home from '@/components/Home'
import Account from '@/components/Account'
import Topics from '@/components/Topics'

Vue.use(Router)

export default new Router({
  routes: [{
    path: '/',
    name: 'home',
    component: Home
  }, {
    path: '/edit',
    name: 'Edit',
    component: Edit
  }, {
    path: '/account',
    name: 'Account',
    component: Account
  }, {
    path: '/topics',
    name: 'Topics',
    component: Topics
  }]
})
