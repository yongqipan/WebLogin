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
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id`       bigint       NOT NULL AUTO_INCREMENT,
  `username` varchar(50)  COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 初始种子数据（测试账号）
--   admin / 123456
--   user1 / 123456
-- ------------------------------------------------------------
INSERT INTO `user` (`username`, `password`) VALUES
  ('admin', '123456'),
  ('user1', '123456');
