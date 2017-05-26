const config = {
  HOST: 'http://kotlin-cn.org',
  API: '/api',
  URL: {
    account: {
      register: '/account/register',
      login: '/account/login',
      user: '/account/user/{1}',
      password: '/account/user/{1}/password',
      update: '/account/user/{1}/update'
    },
    article: {
      post: '/article/post',
      detail: '/article/{1}',
      update: '/article/{1}/update',
      reply: '/article/{1}/reply',
      delete: '/article/{1}/delete',
      list: '/article/list',
      category: '/article/category/{1}',
      fine: '/article/fine'
    }
  },
  ui: {
    root: '#/',
    account: '#/account',
    edit: '#/edit',
    topics: '#/topics',
    manager: '#/manager'
  }
};
export default config;
