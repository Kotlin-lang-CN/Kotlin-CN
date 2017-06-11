const config = {
  HOST: 'http://localhost:8080',
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
      category: '/article/category/{0}',//获取特定类别最新文章列表
      categoryType: '/article/category'//获取文章类别列表
    },
    rss: {
      fine: "/rss/fine",//精品文章订阅
    },
    misc: {
      dashboard: '/misc/dashboard'
    },
    admin: {
      articleList: '/admin/article/list',//管理员视角查看所有文章内容
      updateArticleState: '/admin/article/{0}/state',//更新文章状态
      updateUserState: '/admin/user/{0}/state',//跟新用户状态
      updateReplyState: '/admin/reply/{0}/state'//更新回复状态
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
