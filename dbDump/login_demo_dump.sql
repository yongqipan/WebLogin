-- MySQL dump 10.13  Distrib 8.0.46, for Linux (x86_64)
--
-- Host: localhost    Database: login_demo
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `login_demo`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `login_demo` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `login_demo`;

--
-- Table structure for table `bug`
--

DROP TABLE IF EXISTS `bug`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bug` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '缺陷',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '打开',
  `severity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '一般',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `assignee` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_severity` (`severity`),
  KEY `idx_assignee` (`assignee`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bug`
--

LOCK TABLES `bug` WRITE;
/*!40000 ALTER TABLE `bug` DISABLE KEYS */;
INSERT INTO `bug` VALUES (1,'需要添加bug产品列表','Bug追踪系统应该可以应用在不同的产品上，bug产品列表要求可以动态添加，修改。该属性值不能为空。','新功能','打开','一般','admin','admin','2026-09-03 13:28:17','2026-09-04 12:56:29'),(2,'添加用户管理功能','添加管理员页面实现用户管理：添加，修改，保存等。','新功能','打开','一般','admin','admin','2026-09-03 13:28:25','2026-09-04 09:02:44'),(3,'需要新增bug类型','bug类型为选择列表，可选值为：缺陷，新功能','新功能','已关闭','一般','admin','admin','2026-09-03 14:37:18','2026-09-04 09:04:52'),(4,'需要添加bug首页','用户登录后显示bug首页，首页提供一个输入框仅供通过bug ID查找bug。用户输入bug ID后点击查找按钮，如果找到bug，则在当前页面显示bug详情；如果没有找到bug，则弹出对话框“没有找到bugID <ID>, 请检查！”， 对话框只有“确定”按钮.','新功能','打开','一般','admin','admin','2026-09-04 08:58:38','2026-09-04 09:16:21'),(5,'bug修改历史记录应该显示完整','bug的详细描述太长，bug的修改历史记录没有显示完整的详细描述，而是显示了省略号。','缺陷','已关闭','一般','admin','user1','2026-09-04 08:58:38','2026-09-04 11:25:12'),(6,'要求可以更新bug进展','不改变bug现在所有的属性值，用户要可以更新bug的进展。\n在所有修改历史的最下面添加一个进度更新文本输入框，提交后内容显示在修改历史里。','新功能','打开','一般','admin','admin','2026-09-04 11:01:20','2026-09-04 14:01:50'),(7,'需要支持上传下载附件','bug可能会有详细的文档说明或者截图，用户要可以上传附件到bug，以供开发和测试人员查阅。','新功能','打开','一般','admin','admin','2026-09-04 13:49:25','2026-09-04 13:49:25');
/*!40000 ALTER TABLE `bug` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bug_history`
--

DROP TABLE IF EXISTS `bug_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bug_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bug_id` bigint NOT NULL,
  `operator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `operated_at` datetime NOT NULL,
  `changes` text COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_bug_id` (`bug_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bug_history`
--

LOCK TABLES `bug_history` WRITE;
/*!40000 ALTER TABLE `bug_history` DISABLE KEYS */;
INSERT INTO `bug_history` VALUES (1,1,'admin','2026-09-03 13:28:17','[{\"field\":\"title\",\"old\":null,\"new\":\"登录页偶发 500\"},{\"field\":\"status\",\"old\":null,\"new\":\"打开\"},{\"field\":\"severity\",\"old\":null,\"new\":\"严重\"},{\"field\":\"assignee\",\"old\":null,\"new\":\"user1\"}]'),(2,1,'admin','2026-09-03 13:28:21','[{\"field\":\"title\",\"old\":\"登录页偶发 500\",\"new\":\"登录页偶发 500（复现中）\"},{\"field\":\"status\",\"old\":\"打开\",\"new\":\"处理中\"},{\"field\":\"assignee\",\"old\":\"user1\",\"new\":\"admin\"}]'),(3,2,'admin','2026-09-03 13:28:25','[{\"field\":\"title\",\"old\":null,\"new\":\"标题为空的提示文案错误\"},{\"field\":\"status\",\"old\":null,\"new\":\"打开\"},{\"field\":\"severity\",\"old\":null,\"new\":\"一般\"},{\"field\":\"assignee\",\"old\":null,\"new\":null}]'),(4,1,'admin','2026-09-03 13:56:53','[{\"field\":\"description\",\"old\":\"连续点击登录按钮时出现服务器错误\",\"new\":\"连续点击登录按钮时出现服务器错误\\nbug已修复，请验证！\"},{\"field\":\"status\",\"old\":\"处理中\",\"new\":\"已修复\"}]'),(5,3,'admin','2026-09-03 14:37:18','[{\"field\":\"title\",\"old\":null,\"new\":\"需要新增bug类型\"},{\"field\":\"status\",\"old\":null,\"new\":\"打开\"},{\"field\":\"severity\",\"old\":null,\"new\":\"一般\"},{\"field\":\"assignee\",\"old\":null,\"new\":\"admin\"}]'),(6,1,'admin','2026-09-03 14:43:28','[{\"field\":\"title\",\"old\":\"登录页偶发 500（复现中）\",\"new\":\"需要添加bug产品列表\"},{\"field\":\"description\",\"old\":\"连续点击登录按钮时出现服务器错误\\nbug已修复，请验证！\",\"new\":\"bug产品列表要求可以设置。\"},{\"field\":\"status\",\"old\":\"已修复\",\"new\":\"打开\"},{\"field\":\"severity\",\"old\":\"严重\",\"new\":\"一般\"}]'),(7,2,'admin','2026-09-03 14:46:32','[{\"field\":\"title\",\"old\":\"标题为空的提示文案错误\",\"new\":\"添加用户管理功能\"},{\"field\":\"description\",\"old\":null,\"new\":\"添加管理员页面实现用户管理：添加，修改，保存等。\"},{\"field\":\"assignee\",\"old\":null,\"new\":\"admin\"}]'),(8,4,'admin','2026-09-04 08:58:38','[{\"field\":\"title\",\"old\":null,\"new\":\"支持深色模式\"},{\"field\":\"type\",\"old\":null,\"new\":\"新功能\"},{\"field\":\"status\",\"old\":null,\"new\":\"打开\"},{\"field\":\"severity\",\"old\":null,\"new\":\"一般\"},{\"field\":\"assignee\",\"old\":null,\"new\":null}]'),(9,5,'admin','2026-09-04 08:58:38','[{\"field\":\"title\",\"old\":null,\"new\":\"列表分页显示异常\"},{\"field\":\"type\",\"old\":null,\"new\":\"缺陷\"},{\"field\":\"status\",\"old\":null,\"new\":\"打开\"},{\"field\":\"severity\",\"old\":null,\"new\":\"一般\"},{\"field\":\"assignee\",\"old\":null,\"new\":null}]'),(10,5,'admin','2026-09-04 08:58:45','[{\"field\":\"type\",\"old\":\"缺陷\",\"new\":\"新功能\"},{\"field\":\"status\",\"old\":\"打开\",\"new\":\"处理中\"}]'),(11,5,'admin','2026-09-04 08:58:46','[{\"field\":\"assignee\",\"old\":null,\"new\":\"user1\"}]'),(12,3,'admin','2026-09-04 09:01:27','[{\"field\":\"type\",\"old\":\"缺陷\",\"new\":\"新功能\"},{\"field\":\"status\",\"old\":\"打开\",\"new\":\"处理中\"}]'),(13,2,'admin','2026-09-04 09:02:44','[{\"field\":\"type\",\"old\":\"缺陷\",\"new\":\"新功能\"}]'),(14,1,'admin','2026-09-04 09:02:56','[{\"field\":\"type\",\"old\":\"缺陷\",\"new\":\"新功能\"}]'),(15,3,'admin','2026-09-04 09:04:52','[{\"field\":\"status\",\"old\":\"处理中\",\"new\":\"已关闭\"}]'),(16,4,'admin','2026-09-04 09:16:21','[{\"field\":\"title\",\"old\":\"支持深色模式\",\"new\":\"需要添加bug首页\"},{\"field\":\"description\",\"old\":null,\"new\":\"用户登录后显示bug首页，首页提供一个输入框仅供通过bug ID查找bug。用户输入bug ID后点击查找按钮，如果找到bug，则在当前页面显示bug详情；如果没有找到bug，则弹出对话框“没有找到bugID <ID>, 请检查！”， 对话框只有“确定”按钮.\"},{\"field\":\"assignee\",\"old\":null,\"new\":\"admin\"}]'),(17,5,'admin','2026-09-04 09:25:14','[{\"field\":\"title\",\"old\":\"列表分页显示异常\",\"new\":\"bug修改历史记录应该显示完整\"},{\"field\":\"description\",\"old\":null,\"new\":\"bug的详细描述太长，bug的修改历史记录没有显示完整的详细描述，而是显示了省略号。\"},{\"field\":\"type\",\"old\":\"新功能\",\"new\":\"缺陷\"},{\"field\":\"status\",\"old\":\"处理中\",\"new\":\"打开\"},{\"field\":\"assignee\",\"old\":\"user1\",\"new\":\"admin\"}]'),(18,6,'admin','2026-09-04 11:01:20','[{\"field\":\"title\",\"old\":null,\"new\":\"要求可以更新bug进展\"},{\"field\":\"type\",\"old\":null,\"new\":\"新功能\"},{\"field\":\"status\",\"old\":null,\"new\":\"打开\"},{\"field\":\"severity\",\"old\":null,\"new\":\"一般\"},{\"field\":\"assignee\",\"old\":null,\"new\":\"admin\"}]'),(19,5,'admin','2026-09-04 11:15:14','[{\"field\":\"description\",\"old\":\"bug的详细描述太长，bug的修改历史记录没有显示完整的详细描述，而是显示了省略号。\",\"new\":\"修复说明：Bug 详情页修改历史中，变更值（.change-old/.change-new）原设置 max-width:240px + ellipsis 截断，导致较长字段值（如详细描述）只显示开头并出现省略号。已在 BugDetailView.vue 移除截断样式，改为 pre-wrap 自动换行完整展示，前后端逻辑无需改动。\"},{\"field\":\"status\",\"old\":\"打开\",\"new\":\"已修复\"},{\"field\":\"assignee\",\"old\":\"admin\",\"new\":null}]'),(20,5,'user1','2026-09-04 11:15:27','[{\"field\":\"description\",\"old\":\"修复说明：Bug 详情页修改历史中，变更值（.change-old/.change-new）原设置 max-width:240px + ellipsis 截断，导致较长字段值（如详细描述）只显示开头并出现省略号。已在 BugDetailView.vue 移除截断样式，改为 pre-wrap 自动换行完整展示，前后端逻辑无需改动。\",\"new\":null},{\"field\":\"status\",\"old\":\"已修复\",\"new\":\"已关闭\"}]'),(21,5,'user1','2026-09-04 11:24:50','[{\"field\":\"description\",\"old\":null,\"new\":\"bug的详细描述太长，bug的修改历史记录没有显示完整的详细描述，而是显示了省略号。\"},{\"field\":\"status\",\"old\":\"已关闭\",\"new\":\"已修复\"},{\"field\":\"assignee\",\"old\":null,\"new\":\"user1\"}]'),(22,5,'user1','2026-09-04 11:25:12','[{\"field\":\"status\",\"old\":\"已修复\",\"new\":\"已关闭\"}]'),(23,1,'admin','2026-09-04 12:55:12','[{\"field\":\"description\",\"old\":\"bug产品列表要求可以设置。\",\"new\":\"Bug追踪系统应该可以应用在不同的产品上，bug产品列表要求可以动态添加，修改。\"}]'),(24,1,'admin','2026-09-04 12:56:29','[{\"field\":\"description\",\"old\":\"Bug追踪系统应该可以应用在不同的产品上，bug产品列表要求可以动态添加，修改。\",\"new\":\"Bug追踪系统应该可以应用在不同的产品上，bug产品列表要求可以动态添加，修改。该属性值不能为空。\"}]'),(25,7,'admin','2026-09-04 13:49:25','[{\"field\":\"title\",\"old\":null,\"new\":\"需要支持上传下载附件\"},{\"field\":\"type\",\"old\":null,\"new\":\"新功能\"},{\"field\":\"status\",\"old\":null,\"new\":\"打开\"},{\"field\":\"severity\",\"old\":null,\"new\":\"一般\"},{\"field\":\"assignee\",\"old\":null,\"new\":\"admin\"}]'),(26,6,'admin','2026-09-04 14:01:50','[{\"field\":\"description\",\"old\":\"不改变bug现在所有的属性值，用户要可以更新bug的进展。\",\"new\":\"不改变bug现在所有的属性值，用户要可以更新bug的进展。\\n在所有修改历史的最下面添加一个进度更新文本输入框，提交后内容显示在修改历史里。\"}]');
/*!40000 ALTER TABLE `bug_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','123456'),(2,'user1','123456');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'login_demo'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-04  6:09:13
