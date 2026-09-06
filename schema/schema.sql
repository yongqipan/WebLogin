-- ============================================================
-- WebLogin 登录系统 数据库初始化脚本
-- 数据库: login_demo
-- 说明  : 项目业务库结构。casdoor 库由 Casdoor 应用自动创建，不在此文件。
-- ============================================================

CREATE DATABASE IF NOT EXISTS `login_demo`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `login_demo`;

-- ------------------------------------------------------------
-- 用户表
-- role: admin=管理员, user=普通用户
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id`       bigint       NOT NULL AUTO_INCREMENT,
  `username` varchar(50)  COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role`     varchar(20)  COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 初始种子数据（测试账号）
--   admin / 123456 (管理员)
--   user1 / 123456 (普通用户)
-- ------------------------------------------------------------
INSERT IGNORE INTO `user` (`username`, `password`, `role`) VALUES
  ('admin', '123456', 'admin'),
  ('user1', '123456', 'user');

-- ------------------------------------------------------------
-- Bug 表（bug 追踪子系统）
-- type    : 缺陷 / 新功能
-- status  : 打开 / 处理中 / 已修复 / 已关闭
-- severity: 轻微 / 一般 / 严重 / 致命
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `bug` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `title`       varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text         NULL,
  `type`        varchar(20)  COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '缺陷',
  `status`      varchar(20)  COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '打开',
  `severity`    varchar(20)  COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '一般',
  `creator`     varchar(50)  COLLATE utf8mb4_unicode_ci NOT NULL,
  `assignee`    varchar(50)  COLLATE utf8mb4_unicode_ci NULL,
  `created_at`  datetime     NOT NULL,
  `updated_at`  datetime     NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_severity` (`severity`),
  KEY `idx_assignee` (`assignee`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Bug 字段级变更历史表
-- changes: JSON 数组，如
--   [{"field":"status","old":"打开","new":"处理中"}]
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `bug_history` (
  `id`          bigint      NOT NULL AUTO_INCREMENT,
  `bug_id`      bigint      NOT NULL,
  `operator`    varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `operated_at` datetime    NOT NULL,
  `changes`     text        NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_bug_id` (`bug_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
