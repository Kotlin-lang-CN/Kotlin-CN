/**
 * 用户
 * uid: 用户id
 * name: 用户名
 * password: 用户密码
 * rank: 用户等级 normal - 0, admin - 1
 * forbidden: 封禁状态
 */
CREATE TABLE IF NOT EXISTS accounts (
  uid       INT(16) PRIMARY KEY NOT NULL AUTO_INCREMENT
  COMMENT '用户id',
  name      VARCHAR(255)        NOT NULL
  COMMENT '用户名',
  PASSWORD  VARCHAR(255)        NOT NULL
  COMMENT '用户密码',
  rank      INT(4)              NOT NULL DEFAULT 0
  COMMENT '用户等级',
  forbidden BOOL                NOT NULL DEFAULT FALSE
  COMMENT '用户的封禁状态'
)
  COMMENT '用户表',
  DEFAULT CHARSET utf8,
  ENGINE = Innodb;

/***
 * 文章
 * aid: 文章id
 * author: 作者用户id
 * title: 文章标题
 * description: 文章描述
 * content: 文章内容
 * category: 文章类别 0-默认
 * create_time: 文章创建时间
 * view: 阅读数量
 * flower: 点赞数量
 * comment: 评论 JsonArray 格式字符串, 保存 评论id 列表
 * forbidden: 封禁状态
 */
CREATE TABLE IF NOT EXISTS articles (
  aid         INT(16) PRIMARY KEY NOT NULL AUTO_INCREMENT
  COMMENT '文章id',
  author      INT(16)             NOT NULL
  COMMENT '作者用户id',
  title       VARCHAR(255)        NOT NULL
  COMMENT '文章标题',
  description TEXT                NOT NULL
  COMMENT '文章描述',
  content     TEXT                NOT NULL
  COMMENT '文章内容',
  category    INT(4)              NOT NULL DEFAULT 0
  COMMENT '文章类别',
  create_time DATETIME            NOT NULL DEFAULT NOW()
  COMMENT '文章创建时间',
  view        INT(16)             NOT NULL DEFAULT 0
  COMMENT '阅读数量',
  flower      INT(16)             NOT NULL DEFAULT 0
  COMMENT '点赞数量',
  `comment`   TEXT                         DEFAULT NULL
  COMMENT '评论列表',
  forbidden   BOOL                NOT NULL DEFAULT FALSE
  COMMENT '文章的封禁状态'
)
  COMMENT '文章表',
  DEFAULT CHARSET utf8,
  ENGINE = Innodb;
