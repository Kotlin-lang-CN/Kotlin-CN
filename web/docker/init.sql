CREATE DATABASE kotlin_china;
USE kotlin_china;

/**
 * 用户信息
 * id: 用户id
 * name: 用户姓名
 * token: 用户token
 * avatar_url: 用户头像
 * email: 用户邮箱
 */
CREATE TABLE IF NOT EXISTS accounts (
  id         INTEGER KEY NOT NULL
  COMMENT '用户id',
  name       TEXT        NOT NULL
  COMMENT '用户名',
  token      TEXT        NOT NULL
  COMMENT '用户token',
  avatar_url TEXT        NOT NULL
  COMMENT '用户头像',
  email      TEXT        NOT NULL
  COMMENT '用户邮箱'
)
  COMMENT '用户',
  DEFAULT CHARSET utf8,
  ENGINE = Innodb;

/***
 * 文章
 * aid: 文章id
 * author: 作者用户id
 * title: 文章标题
 * content: 文章内容
 * tag: 文章分类
 * create_time: 文章创建时间
 * comment: 评论数
 * flower: 点赞数
 */
CREATE TABLE IF NOT EXISTS articles (
  aid         INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT
  COMMENT '文章id',
  author      INTEGER             NOT NULL
  COMMENT '作者用户id',
  title       TEXT                NOT NULL
  COMMENT '文章标题',
  content     TEXT                NOT NULL
  COMMENT '文章内容',
  tag         TEXT                NOT NULL
  COMMENT '文章类别',
  create_time DATETIME            NOT NULL DEFAULT now()
  COMMENT '文章创建时间',
  `comment`   INTEGER             NOT NULL DEFAULT 0
  COMMENT '评论数量',
  flower      INTEGER             NOT NULL DEFAULT 0
  COMMENT '点赞数量'
)
  COMMENT '文章',
  DEFAULT CHARSET utf8,
  ENGINE = Innodb;

/***
 * 评论
 * cid: 评论id
 * aid: 文章id
 * commenter: 评论人id
 * reply: 回复(@)用户id
 * create_time: 创建时间
 * content: 评论内容
 * flower: 点赞数量
 */
CREATE TABLE IF NOT EXISTS comments (
  cid         INTEGER PRIMARY KEY NOT NULL         AUTO_INCREMENT
  COMMENT '评论id',
  aid         INTEGER             NOT NULL
  COMMENT '文章id',
  commenter   INTEGER             NOT NULL
  COMMENT '评论人id',
  reply       INTEGER                              DEFAULT NULL
  COMMENT '被回复人id',
  create_time DATETIME            NOT NULL         DEFAULT now()
  COMMENT '创建时间',
  content     TEXT                NOT NULL
  COMMENT '评论内容',
  flower      INTEGER             NOT NULL         DEFAULT 0
  COMMENT '点赞数量'
)
  COMMENT '评论',
  DEFAULT CHARSET utf8,
  ENGINE = Innodb;

/***
 * 点赞
 * id: 点赞id
 * mode: 点赞类型 0-文章 1-评论
 * oid: 被点赞客体 id
 * actor: 点赞人
 * create_time: 点赞时间
 */
CREATE TABLE IF NOT EXISTS flowers (
  id          INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT
  COMMENT '点赞id',
  `mode`      INT(4)              NOT NULL
  COMMENT '点赞类型 0-文章 1-评论',
  oid         INTEGER             NOT NULL
  COMMENT '客体(如文章)id',
  actor       INTEGER             NOT NULL
  COMMENT '点赞人',
  praised     INTEGER             NOT NULL
  COMMENT '被点赞人',
  create_time DATETIME            NOT NULL DEFAULT now()
  COMMENT '创建时间'
)
  COMMENT '点赞',
  DEFAULT CHARSET utf8,
  ENGINE = Innodb;


/***
 * 消息
 * id: 消息id
 * content: 消息正文
 * title: 消息标题
 * create_time: 创建时间
 * from: 发信人id(系统消息为null)
 * to: 收信人id
 * status: 消息状态(0-未读取,1-已读取)
 */
CREATE TABLE IF NOT EXISTS messages (
  id          INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT
  COMMENT '消息id',
  content     TEXT                NOT NULL
  COMMENT '消息内容',
  title       TEXT                NOT NULL
  COMMENT '消息标题',
  create_time DATETIME            NOT NULL DEFAULT now()
  COMMENT '创建时间',
  `from`      INTEGER                      DEFAULT NULL
  COMMENT '发信人id',
  `to`        INTEGER             NOT NULL
  COMMENT '收信人id',
  `status`    INT(4)              NOT NULL DEFAULT 0
  COMMENT '消息状态'
)
  COMMENT '用户消息',
  DEFAULT CHARSET utf8,
  ENGINE = Innodb;