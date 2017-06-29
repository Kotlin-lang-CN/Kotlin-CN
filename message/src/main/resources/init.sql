CREATE TABLE IF NOT EXISTS message (
  id          VARCHAR(128) PRIMARY KEY NOT NULL
  COMMENT '消息id',
  type        TINYINT                  NOT NULL
  COMMENT '消息解析类型',
  create_time BIGINT                   NOT NULL
  COMMENT '创建时间戳',
  content     TEXT
  COMMENT '消息体',
  creator     BIGINT                   NOT NULL DEFAULT 0
  COMMENT '创建者',
  acceptor    BIGINT                   NOT NULL DEFAULT 0
  COMMENT '消息接受者'
)
  COMMENT '账号',
  DEFAULT CHARSET utf8mb4,
  ENGINE = Innodb;
