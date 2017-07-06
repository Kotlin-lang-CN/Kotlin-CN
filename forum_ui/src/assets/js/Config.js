const CGI = {
  account: "https://debug.kotlin-cn.org/api/account",
  github: 'https://debug.kotlin-cn.org/api/github',
  admin: 'https://debug.kotlin-cn.org/api/admin',
  article: "https://debug.kotlin-cn.org/api/article",
  reply: 'https://debug.kotlin-cn.org/api/reply',
  rss: 'https://debug.kotlin-cn.org/api/rss',
  flower: 'https://debug.kotlin-cn.org/api/flower',
  misc: 'https://debug.kotlin-cn.org/api/misc',
};

const Config = {
  URL: {
    account: {
      register: CGI.account + '/register',
      login: CGI.account + '/login',
      user: CGI.account + '/user/{0}',
      password: CGI.account + '/user/{0}/password',
      update: CGI.account + '/user/{0}/update',
      profile: CGI.account + '/profile',
      profile_update: CGI.account + '/profile/update',
    },
    github: {
      createState: CGI.github + '/state',
      auth: CGI.account + '/auth'
    },
    admin: {
      articleList: CGI.admin + '/article/list',//管理员视角查看所有文章内容
      updateArticleState: CGI.admin + '/article/{0}/state',//更新文章状态
      updateUserState: CGI.admin + '/user/{0}/state',//跟新用户状态
      updateReplyState: CGI.admin + '/reply/{0}/state'//更新回复状态
    },
    article: {
      post: CGI.article + '/post',
      detail: CGI.article + '/post/{0}',
      update: CGI.article + '/post/{0}/update',
      delete: CGI.article + '/post/{0}/delete',

      category: CGI.article + '/category',//获取文章类别列表
      getLatest: CGI.article + '/list',//获取最新文章列表
      getFine: CGI.article + '/fine', //获取精品文章列表
      getCategory: CGI.article + '/category/{0}',//获取特定类别最新文章列表
    },
    reply: {
      count: CGI.reply + '/count',
      article: CGI.reply + '/article/{0}',
      delete: CGI.reply + '/id/{0}/delete',
      user: CGI.reply + '/user/{0}',
    },
    flower: {
      article_star: CGI.flower + '/article/{0}/star',
      article_unstar: CGI.flower + '/article/{0}/unstar',
      article_count: CGI.flower + '/article/star/count',
      reply_star: CGI.flower + '/reply/{0}/star',
      reply_unstar: CGI.flower + '/reply/{0}/unstar',
      reply_count: CGI.flower + '/reply/star/count',
    },
    rss: {
      fine: CGI.rss + "/fine",//精品文章订阅
      latest: CGI.rss + '/latest',//最新文章订阅
    },
    misc: {
      dashboard: CGI.misc + '/dashboard',//网站公告栏
      homeLink: CGI.misc + '/home/link',//首页广告栏链接
    },
  },
  OAuth: {
    github: {
      cgi: 'http://github.com/login/oauth/authorize',
      clientId: '4515da98f829c9feb99f',
      scope: 'user',
    }
  },
  UI: {
    root: '/',
    account: '/account',
    edit: '/edit',
    post: '/post',
    comments: '/comments',
    wiki: '/wiki'
  },
  Wiki: {
    toc: '6287247888258359296',
    readme: '6284215952900517888'
  }
};
export default Config;
