/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.34-log : Database - tushu
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`tushu` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `tushu`;

/*Table structure for table `advice` */

DROP TABLE IF EXISTS `advice`;

CREATE TABLE `advice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `info` varchar(255) DEFAULT NULL,
  `reply` varchar(255) DEFAULT NULL,
  `user_name` varchar(22) DEFAULT NULL,
  `info_time` varchar(32) DEFAULT NULL,
  `reply_time` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4;

/*Data for the table `advice` */

insert  into `advice`(`id`,`info`,`reply`,`user_name`,`info_time`,`reply_time`) values (5,'这是一个测试','会反复测试','1111','2024-07-03 18:28:17','2024-07-04 14:59:36'),(6,'比你好','好个屁','1111','2024-07-03 18:28:17','2024-07-04 15:00:39'),(7,'Hello你好呀！','hello你好呀','1111','2024-07-03 19:14:06','2024-07-05 13:56:25'),(8,'怎么了？','没啥。','1111','2024-07-03 19:15:17','2024-07-04 15:00:32'),(9,'1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111112222222222222222222222222222222222222222222222222222222222211111111111111111111111111111111111111111111111111111','太长啦','1111','2024-07-03 19:26:31','2024-07-04 15:00:55'),(22,'我借的书什么时候还？',NULL,'1111','2024-07-04 15:05:38',NULL),(23,'新用户来啦。','欢迎欢迎','2222','2024-07-04 15:07:00','2024-07-04 15:18:22'),(24,'看看看看','滚！！！','xiaowei','2024-07-30 15:39:18','2024-07-30 15:39:55');

/*Table structure for table `book` */

DROP TABLE IF EXISTS `book`;

CREATE TABLE `book` (
  `book_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '书本id',
  `book_name` varchar(32) NOT NULL COMMENT '书名',
  `book_price` double DEFAULT NULL COMMENT '书本价格',
  `book_public` varchar(32) DEFAULT NULL COMMENT '书本出版社',
  `book_classify` varchar(20) DEFAULT NULL COMMENT '书籍分类',
  `book_author` varchar(12) DEFAULT NULL COMMENT '书本作者',
  `book_imge` varchar(220) DEFAULT NULL COMMENT '书本图片',
  `book_description` varchar(220) DEFAULT NULL COMMENT '书本描述',
  `addtime` date DEFAULT NULL,
  `book_num` int(11) DEFAULT NULL COMMENT '书本库存',
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4;

/*Data for the table `book` */

insert  into `book`(`book_id`,`book_name`,`book_price`,`book_public`,`book_classify`,`book_author`,`book_imge`,`book_description`,`addtime`,`book_num`) values (1,'消失的影子',99.9,'科大出版社','悬疑','东野四郎','http://localhost:8080/common/download?name=消失的影子.png','午午',NULL,9),(2,'明天你好',33,'小鸟出版社','自然','小鸟','http://localhost:8080/common/download?name=明天你好.png','有',NULL,1),(3,'面向对象',66.76,'清华出版社','计算机','九九九','http://localhost:8080/common/download?name=java面向对象.png','有',NULL,2),(4,'从百草园到三味书屋',44.4,'周树人出版社','自然','周树人','http://localhost:8080/common/download?name=从百草园到三味书屋.png','周树人的得意之作',NULL,10),(5,'科比自传',32.6,'篮球出版社','生活','科比','http://localhost:8080/common/download?name=科比自传.png','大哥自传',NULL,4),(6,'操作系统',55.4,'电子工业出版社','知识','马克','http://localhost:8080/common/download?name=操作系统.png','啦啦啦',NULL,2),(7,'十万个为什么',9.9,'科普出版社','科普','小鸡','http://localhost:8080/common/download?name=十万个为什么.png','误',NULL,1),(8,'海洋生物',99,'海洋出版社','恐怖','大海','http://localhost:8080/common/download?name=海洋生物.png','aaa',NULL,3),(9,'海洋生物典藏版',88.88,'海洋出版社','自然','大海2','http://localhost:8080/common/download?name=海洋生物典藏版.png','无',NULL,0),(10,'开爱爱奥斯丁',5.5,'你好出版社','科普','小脚','http://localhost:8080/common/download?name=奥斯汀传.png','好看',NULL,1),(11,'计算机科学',10,'清华出版社','计算机','余名','http://localhost:8080/common/download?name=计算机科学.png','奥妙无穷',NULL,1),(12,'C++从入门到精通',99,'未知出版社','计算机','小A','http://localhost:8080/common/download?name=C从入门到精通.png','安安',NULL,3),(17,'你好世界',9.99,'小鸟出版社','科普',NULL,'http://localhost:8080/common/download?name=你好世界.png','不可描述',NULL,1),(19,'AI与教育',89,'人名自由出版社','计算机',NULL,'http://localhost:8080/common/download?name=4f642e45-8a4b-446e-bc22-93defc43f55e.jpeg','牛蛙',NULL,10),(20,'曾国藩大传',10,'人民历史教育出版社','历史文学',NULL,'http://localhost:8080/common/download?name=e59bec67-dd65-4b5b-9d1d-0d51ce643fd2.jpeg','曾国藩英勇事迹',NULL,12),(21,'聚乳酸',99.99,'小鸟出版社','自然',NULL,'http://localhost:8080/common/download?name=d9929a0c-17c2-4ffe-9af0-bfb439aee6c9.jpeg','',NULL,8),(22,'美丽的历程',92.2,'科学出版社','自然',NULL,'http://localhost:8080/common/download?name=14fe39fd-36b8-4273-b2ca-2252b399fb2a.png','描述人生',NULL,4);

/*Table structure for table `book_borrow` */

DROP TABLE IF EXISTS `book_borrow`;

CREATE TABLE `book_borrow` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(22) DEFAULT NULL COMMENT '借阅用户id',
  `book_id` int(11) DEFAULT NULL COMMENT '借阅书本id',
  `book_name` varchar(32) DEFAULT NULL COMMENT '借阅书名',
  `borrow_time` double DEFAULT NULL COMMENT '借阅天数',
  `begin_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;

/*Data for the table `book_borrow` */

insert  into `book_borrow`(`id`,`user_name`,`book_id`,`book_name`,`borrow_time`,`begin_time`,`end_time`) values (2,'2222',1,'消失的影子',5,'2024-06-15 17:29:56','2024-06-20 17:29:56'),(3,'admin',1,'消失的影子',5,'2024-06-15 17:32:22','2024-06-20 17:32:22'),(4,'xiaoniao',1,'消失的影子',5,'2024-06-15 21:46:57','2024-06-20 21:46:57'),(5,'1',12,'C++从入门到精通',5,'2024-06-15 21:46:57','2024-06-20 21:46:57'),(6,'admin',5,'科比自传',5,'2024-06-17 19:33:53','2024-06-22 19:33:53'),(7,'1',10,'开爱爱奥斯丁',5,'2024-06-19 16:04:31','2024-06-24 16:04:31'),(10,'xiaowei',2,'明天你好',205,'2024-07-30 15:38:20','2025-02-20 15:38:20'),(11,'1111',1,'消失的影子',5,'2024-08-03 22:32:43','2024-08-08 22:32:43');

/*Table structure for table `book_class` */

DROP TABLE IF EXISTS `book_class`;

CREATE TABLE `book_class` (
  `classId` int(11) NOT NULL,
  `classify` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`classId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `book_class` */

insert  into `book_class`(`classId`,`classify`) values (1,'悬疑'),(2,'自然'),(3,'生活'),(4,'工业'),(5,'科普'),(6,'知识');

/*Table structure for table `book_score` */

DROP TABLE IF EXISTS `book_score`;

CREATE TABLE `book_score` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `score` double NOT NULL,
  `time` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=latin1;

/*Data for the table `book_score` */

insert  into `book_score`(`id`,`user_id`,`book_id`,`score`,`time`) values (1,1001,1,5,'2023-04-11'),(2,1001,2,4,'2023-04-11'),(3,1001,3,1,'2023-04-11'),(4,1001,4,4,'2023-04-11'),(5,1001,5,2,'2023-04-11'),(6,1001,6,3,'2023-04-11'),(7,1001,7,1,'2023-04-11'),(8,1001,8,2,'2023-04-11'),(9,1001,9,5,'2023-04-11'),(10,1001,10,4,'2023-04-11'),(11,1002,1,4,'2023-04-11'),(12,1002,2,3,'2023-04-11'),(13,1002,3,3,'2023-04-11'),(14,1002,4,2,'2023-04-11'),(15,1002,5,1,'2023-04-11'),(16,1002,6,1,'2023-04-11'),(17,1002,7,2,'2023-04-11'),(18,1002,8,1,'2023-04-11'),(19,1002,9,2,'2023-04-11'),(20,1002,10,1,'2023-04-11'),(21,1003,1,5,'2023-04-11'),(22,1003,2,4,'2023-04-11'),(23,1003,9,5,'2023-04-11'),(24,1003,10,4,'2023-04-11'),(25,1004,4,3,NULL),(26,1004,5,4,NULL),(27,1004,9,1,NULL),(28,1005,1,1,NULL),(29,1005,3,3,NULL),(30,1005,5,5,NULL),(31,1006,3,4,NULL),(32,1006,8,3,NULL),(33,1006,9,1,NULL),(34,1007,1,5,NULL),(35,1007,2,4,NULL),(36,1007,3,1,NULL),(37,1035,10,4,'2024-06-19'),(47,17,10,1,'2024-06-24'),(48,17,1,5,'2024-06-24'),(51,1036,5,5,'2024-07-04'),(52,1037,2,3,'2024-07-30'),(53,1035,11,4,'2024-08-03'),(54,1035,6,3,'2024-08-03'),(55,1035,1,2,'2024-08-03');

/*Table structure for table `score` */

DROP TABLE IF EXISTS `score`;

CREATE TABLE `score` (
  `user_id` int(8) NOT NULL,
  `books_id` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `time` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `score` */

insert  into `score`(`user_id`,`books_id`,`score`,`time`) values (1001,10,4,'2023-4-18'),(1002,NULL,NULL,NULL);

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(12) DEFAULT NULL COMMENT '用户账号',
  `password` varchar(12) DEFAULT NULL COMMENT '用户密码',
  `age` int(11) DEFAULT NULL COMMENT '用户年龄',
  `address` varchar(36) DEFAULT NULL COMMENT '用户住址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1038 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`name`,`password`,`age`,`address`) values (17,'xiaoniao','111',18,'湖南省长沙市'),(1001,'admin','123',18,'无名市'),(1002,'zhangsan','111',11,'China'),(1003,'aa0','123a0',18,NULL),(1004,'aa1','123a1',19,NULL),(1005,'aa2','123a2',20,NULL),(1006,'aa3','123a3',21,NULL),(1007,'aa4','123a4',22,NULL),(1008,'xiao','1122',18,NULL),(1009,'wuwu','222',NULL,NULL),(1010,'kk','5566',NULL,NULL),(1011,'kkkk','5555',NULL,NULL),(1012,'xiao1','122',11,NULL),(1013,'xiao2','1223',121,NULL),(1017,'ooo','123a',14,'华纳'),(1018,'test01','123',NULL,NULL),(1020,'test02','123',NULL,NULL),(1022,'123456','123456',NULL,NULL),(1024,'ooo2','123a',145,'华纳'),(1033,'小鸟','1',22,'湖北武汉'),(1034,'李四','1234',18,NULL),(1035,'1111','1112',18,'武钢'),(1036,'2222','2222',NULL,NULL),(1037,'xiaowei','12345',14,'邵阳');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
