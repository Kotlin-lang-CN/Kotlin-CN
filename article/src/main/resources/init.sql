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
  state          TINYINT               NOT NULL DEFAULT 0
  COMMENT '文章状态',
  content_id     BIGINT                NOT NULL
  COMMENT '文章内容',
  INDEX title_index (title(128)),
  INDEX author_index(author),
  INDEX category_index(category)
)
  COMMENT '文章',
  DEFAULT CHARSET utf8mb4,
  ENGINE = Innodb;

CREATE TABLE IF NOT EXISTS reply (
  id            BIGINT PRIMARY KEY NOT NULL
  COMMENT '回复id',
  reply_pool_id VARCHAR(128)       NOT NULL
  COMMENT '评论池id',
  owner_uid     BIGINT             NOT NULL
  COMMENT '评论人id',
  create_time   BIGINT             NOT NULL
  COMMENT '创建时间',
  state         TINYINT            NOT NULL DEFAULT 0
  COMMENT '评论状态',
  content_id    BIGINT             NOT NULL
  COMMENT '评论内容',
  alias_id      BIGINT             NOT NULL DEFAULT 0
  COMMENT '关联评论id'
)
  COMMENT '用户评论',
  DEFAULT CHARSET utf8mb4,
  ENGINE = Innodb;

CREATE TABLE IF NOT EXISTS text_content (
  id           BIGINT PRIMARY KEY    NOT NULL
  COMMENT '用户id',
  content      TEXT                  NOT NULL
  COMMENT '数据内容',
  serialize_id VARCHAR(128)          NOT NULL
  COMMENT '文本关联序列化id',
  create_time  BIGINT                NOT NULL
  COMMENT '数据创建时间',
  INDEX serialize_index(serialize_id(128))
)
  COMMENT '文本内容',
  DEFAULT CHARSET utf8mb4,
  ENGINE = Innodb;