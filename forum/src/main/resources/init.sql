CREATE TABLE IF NOT EXISTS `account` (
  `id`          BIGINT(20) NOT NULL
  COMMENT '用户id',
  `password`    MEDIUMTEXT NOT NULL
  COMMENT '密码(加密值)',
  `last_login`  BIGINT(20) NOT NULL
  COMMENT '上次登录时间',
  `state`       TINYINT(4) NOT NULL DEFAULT '0'
  COMMENT '账号状态',
  `role`        TINYINT(4) NOT NULL DEFAULT '0'
  COMMENT '账号角色',
  `create_time` BIGINT(20) NOT NULL
  COMMENT '创建时间',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='账号';

CREATE TABLE IF NOT EXISTS `article` (
  `id`             BIGINT(20)   NOT NULL
  COMMENT '用户id',
  `title`          VARCHAR(128) NOT NULL
  COMMENT '密码(加密值)',
  `author`         BIGINT(20)   NOT NULL
  COMMENT '上次登录时间',
  `create_time`    BIGINT(20)   NOT NULL
  COMMENT '创建时间',
  `category`       INT(11)      NOT NULL
  COMMENT '文章分类id',
  `tags`           MEDIUMTEXT   NOT NULL
  COMMENT '文章标签',
  `last_edit_time` BIGINT(20)   NOT NULL
  COMMENT '上次编辑时间',
  `last_edit_uid`  BIGINT(20)   NOT NULL
  COMMENT '上次编辑人',
  `state`          TINYINT(4)   NOT NULL DEFAULT '0'
  COMMENT '文章状态',
  `content_id`     BIGINT(20)   NOT NULL
  COMMENT '文章内容',
  PRIMARY KEY (`id`),
  KEY `title_index` (`title`),
  KEY `author_index` (`author`),
  KEY `category_index` (`category`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='文章';

CREATE TABLE IF NOT EXISTS `flower` (
  `id`             BIGINT(20)   NOT NULL
  COMMENT '点赞id',
  `flower_pool_id` VARCHAR(128) NOT NULL
  COMMENT '点赞池id',
  `owner`          BIGINT(20)   NOT NULL
  COMMENT '点赞人',
  `create_time`    BIGINT(20)   NOT NULL
  COMMENT '点赞创建时间',
  PRIMARY KEY (`id`),
  KEY `flower_pool_index` (`flower_pool_id`),
  KEY `flower_owner_index` (`owner`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='点赞';

CREATE TABLE IF NOT EXISTS `github_user_info` (
  `uid`            BIGINT(20)   NOT NULL
  COMMENT '用户id',
  `access_token`   MEDIUMTEXT   NOT NULL
  COMMENT 'github token',
  `id`             BIGINT(20)   NOT NULL
  COMMENT 'github id',
  `name`           MEDIUMTEXT   NOT NULL
  COMMENT 'github username',
  `email`          VARCHAR(128) NOT NULL
  COMMENT 'github email',
  `avatar`         VARCHAR(512) NOT NULL
  COMMENT '用户logo',
  `login`          VARCHAR(128) NOT NULL
  COMMENT 'github 登录用户名',
  `blog`           MEDIUMTEXT   NOT NULL
  COMMENT '个人博客',
  `location`       MEDIUMTEXT   NOT NULL
  COMMENT '地址',
  `follower_count` INT(11)      NOT NULL DEFAULT '0'
  COMMENT 'github follower数',
  `company`        MEDIUMTEXT   NOT NULL
  COMMENT '公司信息',
  PRIMARY KEY (`uid`),
  KEY `github_id` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='github用户';

CREATE TABLE IF NOT EXISTS `profile` (
  `uid`         BIGINT(20) NOT NULL
  COMMENT '用户id',
  `gender`      TINYINT(4) DEFAULT '0'
  COMMENT '性别',
  `github`      TEXT COMMENT 'github地址',
  `blog`        TEXT COMMENT '博客地址',
  `company`     TEXT COMMENT '公司',
  `location`    TEXT COMMENT '位置',
  `description` TEXT COMMENT '个人描述',
  `education`   TEXT COMMENT '教育经历',
  PRIMARY KEY (`uid`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='用户资料';

CREATE TABLE IF NOT EXISTS `reply` (
  `id`            BIGINT(20)   NOT NULL
  COMMENT '回复id',
  `reply_pool_id` VARCHAR(128) NOT NULL
  COMMENT '评论池id',
  `owner_uid`     BIGINT(20)   NOT NULL
  COMMENT '评论人id',
  `create_time`   BIGINT(20)   NOT NULL
  COMMENT '创建时间',
  `state`         TINYINT(4)   NOT NULL DEFAULT '0'
  COMMENT '评论状态',
  `content_id`    BIGINT(20)   NOT NULL
  COMMENT '评论内容',
  `alias_id`      BIGINT(20)   NOT NULL DEFAULT '0'
  COMMENT '关联评论id',
  PRIMARY KEY (`id`),
  KEY `reply_owner_index` (`owner_uid`),
  KEY `reply_pool_index` (`reply_pool_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='用户评论';

CREATE TABLE IF NOT EXISTS `text_content` (
  `id`           BIGINT(20)   NOT NULL
  COMMENT '用户id',
  `content`      MEDIUMTEXT   NOT NULL
  COMMENT '数据内容',
  `serialize_id` VARCHAR(128) NOT NULL
  COMMENT '文本关联序列化id',
  `create_time`  BIGINT(20)   NOT NULL
  COMMENT '数据创建时间',
  PRIMARY KEY (`id`),
  KEY `serialize_index` (`serialize_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='文本内容';

CREATE TABLE IF NOT EXISTS `user_info` (
  `uid`         BIGINT(20)   NOT NULL
  COMMENT '用户id',
  `username`    VARCHAR(128) NOT NULL
  COMMENT '用户名',
  `logo`        VARCHAR(512) NOT NULL
  COMMENT '用户logo',
  `email`       VARCHAR(128) NOT NULL
  COMMENT '用户email',
  `email_state` TINYINT(4)   NOT NULL DEFAULT '0'
  COMMENT '用户email验证',
  PRIMARY KEY (`uid`),
  KEY `name_index` (`username`),
  KEY `email_index` (`email`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='用户基本信息';
