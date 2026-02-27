CREATE DATABASE IF NOT EXISTS `library` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `library`;

-- User Table (用户表)
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `name` varchar(255) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '用户密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- Book Table (图书表)
CREATE TABLE IF NOT EXISTS `book` (
  `book_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '书本id',
  `book_name` varchar(255) DEFAULT NULL COMMENT '书名',
  `book_public` varchar(255) DEFAULT NULL COMMENT '书本出版社',
  `book_classify` varchar(255) DEFAULT NULL COMMENT '书籍分类',
  `book_author` varchar(255) DEFAULT NULL COMMENT '书本作者',
  `book_imge` varchar(500) DEFAULT NULL COMMENT '书本图片',
  `book_description` text DEFAULT NULL COMMENT '书本描述',
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- BookClass Table (图书分类表)
CREATE TABLE IF NOT EXISTS `book_class` (
  `class_id` int(11) NOT NULL AUTO_INCREMENT,
  `classify` varchar(255) DEFAULT NULL COMMENT '分类名称',
  PRIMARY KEY (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- BookLike Table (图书收藏/喜欢表)
CREATE TABLE IF NOT EXISTS `book_like` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `book_id` int(11) DEFAULT NULL,
  `book_name` varchar(255) DEFAULT NULL,
  `like_level` int(11) DEFAULT NULL COMMENT '0-不想看, 1-还行, 2-想看',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书收藏表';

-- BookScore Table (图书评分表)
CREATE TABLE IF NOT EXISTS `book_score` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `book_id` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT NULL COMMENT '评分',
  `time` varchar(50) DEFAULT NULL COMMENT '评分时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书评分表';

-- Advice Table (建议/反馈表)
CREATE TABLE IF NOT EXISTS `advice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `info` varchar(500) DEFAULT NULL COMMENT '建议内容',
  `reply` varchar(500) DEFAULT NULL COMMENT '回复内容',
  `user_name` varchar(255) DEFAULT NULL COMMENT '提建议用户',
  `info_time` varchar(50) DEFAULT NULL COMMENT '建议时间',
  `reply_time` varchar(50) DEFAULT NULL COMMENT '回复时间',
  `book_id` varchar(50) DEFAULT NULL COMMENT '关联图书ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='建议反馈表';
