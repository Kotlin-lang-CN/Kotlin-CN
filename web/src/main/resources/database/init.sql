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
  password  VARCHAR(255)        NOT NULL
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
 * 初始 admin 用户: username->admin@kotlin.tech; password->rootadmin
 */
# INSERT INTO accounts (name, password, rank) values ('admin@kotlin.tech', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyb290YWRtaW4ifQ.QnxCvCCSxCRik7P3U2gus7xPHxwLNhV9SgZDNhNmS-O3hTzvQqbUjZnvyRdXBjwa3EQwWlxMt-kzhrrwWorrQw', 1);

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
 * comment: 评论数量
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
  `comment`   INT(16)             NOT NULL DEFAULT 0
  COMMENT '评论数量',
  forbidden   BOOL                NOT NULL DEFAULT FALSE
  COMMENT '文章的封禁状态'
)
  COMMENT '文章表',
  DEFAULT CHARSET utf8,
  ENGINE = Innodb;

/***
 * 评论
 * cid: 评论id
 * aid: 文章id
 * commenter: 评论人id
 * reply: 回复用户ID
 * create_time: 创建时间
 * content: 评论内容
 * flower: 点赞数量
 * delete: 删除状态
 * forbidden: 封禁状态
 */
CREATE TABLE IF NOT EXISTS comments (
  cid         INT(16) PRIMARY KEY NOT NULL         AUTO_INCREMENT
  COMMENT '评论id',
  aid         INT(16)             NOT NULL
  COMMENT '文章id',
  commenter   INT(16)             NOT NULL
  COMMENT '评论人id',
  reply       INT(16)                              DEFAULT NULL
  COMMENT '被回复人',
  create_time DATETIME            NOT NULL         DEFAULT NOW()
  COMMENT '评论建立时间',
  content     TEXT                NOT NULL
  COMMENT '评论内容',
  flower      INT(16)             NOT NULL         DEFAULT 0
  COMMENT '点赞数量',
  `delete`    BOOL                NOT NULL         DEFAULT FALSE
  COMMENT '被删除',
  forbidden   BOOL                NOT NULL         DEFAULT FALSE
  COMMENT '被封禁'
)
  COMMENT '文章表',
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
  id          INT(16) PRIMARY KEY NOT NULL AUTO_INCREMENT
  COMMENT '点赞id',
  `mode`      INT(4)              NOT NULL
  COMMENT '点赞类型 0-文章 1-评论',
  oid         INT(16)             NOT NULL
  COMMENT '客体(如文章)id',
  actor       INT(16)             NOT NULL
  COMMENT '点赞人',
  create_time DATETIME            NOT NULL DEFAULT NOW()
  COMMENT '创建时间'
)
  COMMENT '点赞表',
  DEFAULT CHARSET utf8,
  ENGINE = Innodb;