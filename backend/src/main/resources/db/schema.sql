-- 海龟汤推理游戏数据库建表脚本
-- 使用前请先创建数据库: CREATE DATABASE turtle_soup DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户主键',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码（bcrypt加密）',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 海龟汤故事表
CREATE TABLE IF NOT EXISTS `puzzle` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '题目主键',
    `title`       VARCHAR(200) NOT NULL COMMENT '故事标题',
    `content`     TEXT         NOT NULL COMMENT '谜面（故事描述）',
    `answer`      TEXT         NOT NULL COMMENT '真相答案（AI判断依据）',
    `used_count`  INT          NOT NULL DEFAULT 0 COMMENT '被游玩次数',
    `difficulty`  VARCHAR(20)  NOT NULL DEFAULT '中等' COMMENT '难度：简单/中等/困难',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='海龟汤故事表';

-- 游戏会话表
CREATE TABLE IF NOT EXISTS `game_session` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '会话ID',
    `user_id`    BIGINT       NOT NULL COMMENT '用户ID',
    `puzzle_id`  BIGINT       NOT NULL COMMENT '题目ID',
    `status`     VARCHAR(20)  NOT NULL DEFAULT 'IN_PROGRESS' COMMENT '状态：IN_PROGRESS/SOLVED/ABANDONED',
    `start_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    `end_time`   DATETIME     DEFAULT NULL COMMENT '结束时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_puzzle_id` (`puzzle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏会话表';

-- 对话日志表
CREATE TABLE IF NOT EXISTS `chat_log` (
    `id`            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `session_id`    BIGINT   NOT NULL COMMENT '会话ID',
    `user_question` TEXT     NOT NULL COMMENT '用户的问题',
    `ai_reply`      VARCHAR(50) NOT NULL COMMENT 'AI回答（是/否/无关紧要）',
    `create_time`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提问时间',
    PRIMARY KEY (`id`),
    KEY `idx_session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话日志表';
