const config = {
  HOST: 'http://kotlin-cn.org',
  API: '/api',
  URL: {
    account: {
      register: '/account/register',
      login: '/account/login',
      user: '/account/user/{0}',
      password: '/account/user/{0}/password',
      update: '/account/user/{0}/update'
    },
    article: {
      post: '/article/post',
      detail: '/article/post/{0}',
      update: '/article/post/{0}/update',
      delete: '/article/post/{0}/delete',
      reply: '/article/{0}/reply',
      list: '/article/list',//获取最新文章列表
      fine: '/article/fine', //获取精品文章列表
      category: '/article/category/{0}'//获取特定类别最新文章列表
    }
  },
  UI: {
    root: '#/',
    account: '#/account',
    register: '#/register',
    login: '#/login',
    edit: '#/edit',
    topics: '#/topics',
    manager: '#/manager',
    topic: '#/topic'
  }
};
export default config;
