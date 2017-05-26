const config = {
  HOST: 'http://kotlin-cn.org',
  API: '/api',
  url: {
    account: {
      register: '/account/register',
      login: '/account/login',
      user: '/account/user/%s',
      password: '/account/user/%s/password',
      update: '/account/user/%s/update'
    },
    article: {
      post: '/article/post',
      detail: '/article/post/%s',
      update: '/article/post/%s/update',
      reply: '/article/post/%s/reply',
      delete: '/article/post/%s/delete',
      list: '/article/list',
      category: '/article/category/%s',
      fine: '/article/fine'
    }
  }
};
export default config;
