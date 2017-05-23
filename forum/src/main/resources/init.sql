CREATE TABLE IF NOT EXISTS account (
  id          BIGINT PRIMARY KEY    NOT NULL
  COMMENT '用户id',
  password    TEXT                  NOT NULL
  COMMENT '密码(加密值)',
  last_login  BIGINT                NOT NULL
  COMMENT '上次登录时间',
  state       TINYINT               NOT NULL DEFAULT 0 -- NORMAL --
  COMMENT '账号状态',
  role        TINYINT               NOT NULL DEFAULT 0 -- NORMAL --
  COMMENT '账号角色',
  create_time BIGINT                NOT NULL
  COMMENT '创建时间'
)
  COMMENT '账号',
  DEFAULT CHARSET utf8,
  ENGINE = Innodb;

CREATE TABLE IF NOT EXISTS user_info (
  uid         BIGINT PRIMARY KEY    NOT NULL
  COMMENT '用户id',
  username    VARCHAR(128)          NOT NULL
  COMMENT '用户名',
  logo        VARCHAR(512)          NOT NULL
  COMMENT '用户logo',
  email       VARCHAR(128)          NOT NULL
  COMMENT '用户email',
  email_state TINYINT               NOT NULL DEFAULT 0 -- NONE --
  COMMENT '用户email验证',
  INDEX name_index (username(128)),
  INDEX email_index(email(128))
)
  COMMENT '用户基本信息',
  DEFAULT CHARSET utf8,
  ENGINE = Innodb;

CREATE TABLE IF NOT EXISTS article (
  id             BIGINT PRIMARY KEY    NOT NULL
  COMMENT '用户id',
  title          VARCHAR(128)          NOT NULL
  COMMENT '密码(加密值)',
  author         BIGINT                NOT NULL
  COMMENT '上次登录时间',
  create_time    BIGINT                NOT NULL
  COMMENT '创建时间',
  category       INTEGER               NOT NULL
  COMMENT '文章分类id',
  tags           TEXT                  NOT NULL
  COMMENT '文章标签',
  last_edit_time BIGINT                NOT NULL
  COMMENT '上次编辑时间',
  last_edit_uid  BIGINT                NOT NULL
  COMMENT '上次编辑人',
  state          TINYINT               NOT NULL DEFAULT 0 -- NORMAL --
  COMMENT '文章状态',
  INDEX title_index (title(128)),
  INDEX author_index(author),
  INDEX category_index(category)
)
  COMMENT '账号',
  DEFAULT CHARSET utf8,
  ENGINE = Innodb;