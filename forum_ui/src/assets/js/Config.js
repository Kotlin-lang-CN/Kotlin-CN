const CGI = {
  account: "//localhost:8081/api",
  article: "//localhost:8083/api"
};

const Config = {
  URL: {
    account: {
      register: CGI.account + '/account/register',
      login: CGI.account + '/account/login',
      user: CGI.account + '/account/user/{0}',
      password: CGI.account + '/account/user/{0}/password',
      update: CGI.account + '/account/user/{0}/update'
    },
    github: {
      createState: CGI.account + '/github/state',
      auth: CGI.account + '/github/auth'
    },
    admin: {
      articleList: CGI.account + '/admin/article/list',//管理员视角查看所有文章内容
      updateArticleState: CGI.account + '/admin/article/{0}/state',//更新文章状态
      updateUserState: CGI.account + '/admin/user/{0}/state',//跟新用户状态
      updateReplyState: CGI.account + '/admin/reply/{0}/state'//更新回复状态
    },
    article: {
      post: CGI.article + '/article/post',
      detail: CGI.article + '/article/post/{0}',
      update: CGI.article + '/article/post/{0}/update',
      delete: CGI.article + '/article/post/{0}/delete',
      reply: CGI.article + '/article/{0}/reply',
      deleteReply: CGI.article + '/article/reply/{0}/delete',
      list: CGI.article + '/article/list',//获取最新文章列表
      fine: CGI.article + '/article/fine', //获取精品文章列表
      category: CGI.article + '/article/category/{0}',//获取特定类别最新文章列表
      categoryType: CGI.article + '/article/category'//获取文章类别列表
    },
    rss: {
      fine: CGI.article + "/rss/fine",//精品文章订阅
    },
    misc: {
      dashboard: CGI.article + '/misc/dashboard',//网站公告栏
      homeLink: CGI.article + '/misc/home/link',//首页广告栏链接
    },
  },
  OAuth: {
    github: {
      cgi: 'http://github.com/login/oauth/authorize',
      clientId: 'ed1760e81a41e5553b0d',
      scope: 'user',
    }
  },
  UI: {
    root: '/',
    account: '/account',
    edit: '/edit',
    post: '/post'
  },
  MUI: {
    root: '/m/',
    account: '/m/account',
    edit: '/m/edit',
    post: '/m/post'
  }

};
export default Config;
