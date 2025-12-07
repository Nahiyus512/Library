/*
 Navicat Premium Dump SQL

 Source Server         : firstConnect
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : tushu

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 07/12/2025 21:52:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for advice
-- ----------------------------
DROP TABLE IF EXISTS `advice`;
CREATE TABLE `advice`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `reply` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_name` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `info_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `reply_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of advice
-- ----------------------------
INSERT INTO `advice` VALUES (5, '这是一个测试', '会反复测试', '1111', '2024-07-03 18:28:17', '2024-07-04 14:59:36');
INSERT INTO `advice` VALUES (6, '比你好', '好个屁', '1111', '2024-07-03 18:28:17', '2024-07-04 15:00:39');
INSERT INTO `advice` VALUES (7, 'Hello你好呀！', 'hello你好呀', '1111', '2024-07-03 19:14:06', '2024-07-05 13:56:25');
INSERT INTO `advice` VALUES (8, '怎么了？', '没啥。', '1111', '2024-07-03 19:15:17', '2024-07-04 15:00:32');
INSERT INTO `advice` VALUES (9, '1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111112222222222222222222222222222222222222222222222222222222222211111111111111111111111111111111111111111111111111111', '太长啦', '1111', '2024-07-03 19:26:31', '2024-07-04 15:00:55');
INSERT INTO `advice` VALUES (22, '我借的书什么时候还？', NULL, '1111', '2024-07-04 15:05:38', NULL);
INSERT INTO `advice` VALUES (23, '新用户来啦。', '欢迎欢迎', '2222', '2024-07-04 15:07:00', '2024-07-04 15:18:22');
INSERT INTO `advice` VALUES (24, '看看看看', '滚！！！', 'xiaowei', '2024-07-30 15:39:18', '2024-07-30 15:39:55');

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`  (
  `book_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `book_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书名',
  `book_price` double NULL DEFAULT NULL COMMENT '书本价格',
  `book_public` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书本出版社',
  `book_classify` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书籍分类',
  `book_author` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书本作者',
  `book_imge` varchar(220) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书本图片',
  `book_description` varchar(220) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书本描述',
  `addtime` date NULL DEFAULT NULL,
  `book_num` int NULL DEFAULT NULL COMMENT '书本库存',
  PRIMARY KEY (`book_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES ('1', '消失的影子', 99.8, '科大出版社', '悬疑', '东野四郎', 'book/1f1ede00-6fa6-4df8-9825-2e613bf56d0b.png', '午午', NULL, 9);
INSERT INTO `book` VALUES ('10', '开爱爱奥斯丁', 5.5, '你好出版社', '科普', '小脚', 'book/61d8fd28-fccc-4e01-a6a7-9d00657e430a.png', '好看', NULL, 1);
INSERT INTO `book` VALUES ('11', '计算机科学', 10, '清华出版社', '计算机', '余名', 'book/103b08d1-c204-4a7a-93e6-bbaae3f08165.png', '奥妙无穷', NULL, 1);
INSERT INTO `book` VALUES ('12', 'C++从入门到精通', 99, '未知出版社', '计算机', '小A', 'book/631e9f0d-d358-4f59-a536-c9bdbeca5105.png', '安安', NULL, 3);
INSERT INTO `book` VALUES ('17', '你好世界', 9.99, '小鸟出版社', '科普', NULL, 'book/4a5179e6-184a-4bab-9f75-5dd8737c06ce.png', '不可描述', NULL, 1);
INSERT INTO `book` VALUES ('19', 'AI与教育', 89, '人名自由出版社', '计算机', NULL, 'book/1c57e260-9d64-4071-81f0-2db9e9835301.png', '牛蛙', NULL, 10);
INSERT INTO `book` VALUES ('1e12eb33879148b382cee8e1ea619bb8', '被讨厌的勇气', 99, '心理出版社', '科普', NULL, '', '教你：如何不被他人评价控制', NULL, 22);
INSERT INTO `book` VALUES ('2', '明天你好', 33, '小鸟出版社', '自然', '小鸟', 'book/c5528361-4829-4cf3-b85c-87d3297a260e.png', '有', NULL, 1);
INSERT INTO `book` VALUES ('20', '曾国藩大传', 10, '人民历史教育出版社', '历史文学', NULL, 'book/3b08ec05-df01-4be4-9e00-9ad03b438bbf.png', '曾国藩英勇事迹', NULL, 12);
INSERT INTO `book` VALUES ('21', '聚乳酸', 99.99, '小鸟出版社', '自然', NULL, 'book/2e9b361e-ac47-492a-948a-6cd283b7f729.png', '', NULL, 8);
INSERT INTO `book` VALUES ('22', '美丽的历程', 92.2, '科学出版社', '自然', NULL, 'book/6ae4d24a-3dd3-4c7e-b6f4-2547b696d187.png', '描述人生', NULL, 4);
INSERT INTO `book` VALUES ('3', '面向对象', 66.76, '清华出版社', '计算机', '九九九', 'book/ee8037ac-6ef2-4081-a264-5bba25ec8a9e.png', '有', NULL, 2);
INSERT INTO `book` VALUES ('4', '从百草园到三味书屋', 44.4, '周树人出版社', '自然', '周树人', 'book/a96d98f7-85e8-45db-854a-5f9eaed097d2.png', '周树人的得意之作', NULL, 10);
INSERT INTO `book` VALUES ('5', '科比自传', 32.6, '篮球出版社', '生活', '科比', 'book/db2f78c0-873a-40cf-9409-5ca4acf09820.png', '大哥自传', NULL, 4);
INSERT INTO `book` VALUES ('51cff9826a8e4f1894ae29ae36e11ea1', '自我成长 & 情绪管理', 22, '成长出版社', '科普', NULL, '', '如果你经常：  情绪波动大  想太多  对未来焦虑  ', NULL, 99);
INSERT INTO `book` VALUES ('6', '操作系统', 55.4, '电子工业出版社', '知识', '马克', 'book/28309074-7c13-4082-85dd-4aebd188d1cc.png', '啦啦啦', NULL, 2);
INSERT INTO `book` VALUES ('7', '十万个为什么', 9.9, '科普出版社', '科普', '小鸡', 'book/6b258194-f207-44db-94ec-a02de5fa1621.png', '误', NULL, 1);
INSERT INTO `book` VALUES ('8', '海洋生物', 99, '海洋出版社', '恐怖', '大海', 'book/4cac97f9-5188-49bf-8156-0b9b58cacbe5.png', 'aaa', NULL, 3);
INSERT INTO `book` VALUES ('9', '海洋生物典藏版', 88.88, '海洋出版社', '自然', '大海2', 'book/1cb58f92-4fb7-4245-af92-ff2fe368ae1c.png', '无', NULL, 0);
INSERT INTO `book` VALUES ('b463f7a75bc64aa68f78770bf9166c00', '影响力', 68, '心理出版社', '科普', NULL, 'book/d616333f-fcbe-43f0-b20f-67c0c574e57d.png', '为什么你会忍不住答应别人', NULL, 88);
INSERT INTO `book` VALUES ('dad75bb007224e5a8c954150c30d91a7', '这才是心理学', 52, '心理出版社', '科普', NULL, 'book/04ed275d-0a42-4163-b73c-52b2fdda397f.png', '防止被“伪心理学”骗  教你：如何判断“心理学是不是在胡说” 👉 适合：提高认知水平', NULL, 99);
INSERT INTO `book` VALUES ('dbd88038ed6d4b5db721ab1a1d370c42', '思考，快与慢', 45, '心理出版社', '科普', NULL, 'book/62a74810-816b-4bfc-b34a-b4d6eabec6a3.png', '解释：人为什么会冲动、拖延、做错决策', NULL, 77);
INSERT INTO `book` VALUES ('f979f7344ffd443db3ded6e22cd60415', '心理学与生活', 66, '百科全书', '科普', NULL, 'book/a9e04ef8-6784-4d64-b70c-da4520fd3912.png', '情绪、性格、行为、学习、记忆、人际关系全涵盖  大学生、公务员、考研都在用', NULL, 100);

-- ----------------------------
-- Table structure for book_borrow
-- ----------------------------
DROP TABLE IF EXISTS `book_borrow`;
CREATE TABLE `book_borrow`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '借阅用户id',
  `book_id` int NULL DEFAULT NULL COMMENT '借阅书本id',
  `book_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '借阅书名',
  `borrow_time` double NULL DEFAULT NULL COMMENT '借阅天数',
  `begin_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_borrow
-- ----------------------------
INSERT INTO `book_borrow` VALUES (2, '2222', 1, '消失的影子', 5, '2024-06-15 17:29:56', '2024-06-20 17:29:56');
INSERT INTO `book_borrow` VALUES (3, 'admin', 1, '消失的影子', 5, '2024-06-15 17:32:22', '2024-06-20 17:32:22');
INSERT INTO `book_borrow` VALUES (4, 'xiaoniao', 1, '消失的影子', 5, '2024-06-15 21:46:57', '2024-06-20 21:46:57');
INSERT INTO `book_borrow` VALUES (5, '1', 12, 'C++从入门到精通', 5, '2024-06-15 21:46:57', '2024-06-20 21:46:57');
INSERT INTO `book_borrow` VALUES (6, 'admin', 5, '科比自传', 5, '2024-06-17 19:33:53', '2024-06-22 19:33:53');
INSERT INTO `book_borrow` VALUES (7, '1', 10, '开爱爱奥斯丁', 5, '2024-06-19 16:04:31', '2024-06-24 16:04:31');
INSERT INTO `book_borrow` VALUES (10, 'xiaowei', 2, '明天你好', 205, '2024-07-30 15:38:20', '2025-02-20 15:38:20');
INSERT INTO `book_borrow` VALUES (11, '1111', 1, '消失的影子', 5, '2024-08-03 22:32:43', '2024-08-08 22:32:43');

-- ----------------------------
-- Table structure for book_class
-- ----------------------------
DROP TABLE IF EXISTS `book_class`;
CREATE TABLE `book_class`  (
  `classId` int NOT NULL,
  `classify` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`classId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_class
-- ----------------------------
INSERT INTO `book_class` VALUES (1, '悬疑');
INSERT INTO `book_class` VALUES (2, '自然');
INSERT INTO `book_class` VALUES (3, '生活');
INSERT INTO `book_class` VALUES (4, '工业');
INSERT INTO `book_class` VALUES (5, '科普');
INSERT INTO `book_class` VALUES (6, '知识');

-- ----------------------------
-- Table structure for book_score
-- ----------------------------
DROP TABLE IF EXISTS `book_score`;
CREATE TABLE `book_score`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `book_id` int NOT NULL,
  `score` double NOT NULL,
  `time` date NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_score
-- ----------------------------
INSERT INTO `book_score` VALUES (1, 1001, 1, 5, '2023-04-11');
INSERT INTO `book_score` VALUES (2, 1001, 2, 4, '2023-04-11');
INSERT INTO `book_score` VALUES (3, 1001, 3, 1, '2023-04-11');
INSERT INTO `book_score` VALUES (4, 1001, 4, 4, '2023-04-11');
INSERT INTO `book_score` VALUES (5, 1001, 5, 2, '2023-04-11');
INSERT INTO `book_score` VALUES (6, 1001, 6, 3, '2023-04-11');
INSERT INTO `book_score` VALUES (7, 1001, 7, 1, '2023-04-11');
INSERT INTO `book_score` VALUES (8, 1001, 8, 2, '2023-04-11');
INSERT INTO `book_score` VALUES (9, 1001, 9, 5, '2023-04-11');
INSERT INTO `book_score` VALUES (10, 1001, 10, 4, '2023-04-11');
INSERT INTO `book_score` VALUES (11, 1002, 1, 4, '2023-04-11');
INSERT INTO `book_score` VALUES (12, 1002, 2, 3, '2023-04-11');
INSERT INTO `book_score` VALUES (13, 1002, 3, 3, '2023-04-11');
INSERT INTO `book_score` VALUES (14, 1002, 4, 2, '2023-04-11');
INSERT INTO `book_score` VALUES (15, 1002, 5, 1, '2023-04-11');
INSERT INTO `book_score` VALUES (16, 1002, 6, 1, '2023-04-11');
INSERT INTO `book_score` VALUES (17, 1002, 7, 2, '2023-04-11');
INSERT INTO `book_score` VALUES (18, 1002, 8, 1, '2023-04-11');
INSERT INTO `book_score` VALUES (19, 1002, 9, 2, '2023-04-11');
INSERT INTO `book_score` VALUES (20, 1002, 10, 1, '2023-04-11');
INSERT INTO `book_score` VALUES (21, 1003, 1, 5, '2023-04-11');
INSERT INTO `book_score` VALUES (22, 1003, 2, 4, '2023-04-11');
INSERT INTO `book_score` VALUES (23, 1003, 9, 5, '2023-04-11');
INSERT INTO `book_score` VALUES (24, 1003, 10, 4, '2023-04-11');
INSERT INTO `book_score` VALUES (25, 1004, 4, 3, NULL);
INSERT INTO `book_score` VALUES (26, 1004, 5, 4, NULL);
INSERT INTO `book_score` VALUES (27, 1004, 9, 1, NULL);
INSERT INTO `book_score` VALUES (28, 1005, 1, 1, NULL);
INSERT INTO `book_score` VALUES (29, 1005, 3, 3, NULL);
INSERT INTO `book_score` VALUES (30, 1005, 5, 5, NULL);
INSERT INTO `book_score` VALUES (31, 1006, 3, 4, NULL);
INSERT INTO `book_score` VALUES (32, 1006, 8, 3, NULL);
INSERT INTO `book_score` VALUES (33, 1006, 9, 1, NULL);
INSERT INTO `book_score` VALUES (34, 1007, 1, 5, NULL);
INSERT INTO `book_score` VALUES (35, 1007, 2, 4, NULL);
INSERT INTO `book_score` VALUES (36, 1007, 3, 1, NULL);
INSERT INTO `book_score` VALUES (37, 1035, 10, 4, '2024-06-19');
INSERT INTO `book_score` VALUES (47, 17, 10, 1, '2024-06-24');
INSERT INTO `book_score` VALUES (48, 17, 1, 5, '2024-06-24');
INSERT INTO `book_score` VALUES (51, 1036, 5, 5, '2024-07-04');
INSERT INTO `book_score` VALUES (52, 1037, 2, 3, '2024-07-30');
INSERT INTO `book_score` VALUES (53, 1035, 11, 4, '2024-08-03');
INSERT INTO `book_score` VALUES (54, 1035, 6, 3, '2024-08-03');
INSERT INTO `book_score` VALUES (55, 1035, 1, 2, '2024-08-03');

-- ----------------------------
-- Table structure for score
-- ----------------------------
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score`  (
  `user_id` int NOT NULL,
  `books_id` int NULL DEFAULT NULL,
  `score` int NULL DEFAULT NULL,
  `time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of score
-- ----------------------------
INSERT INTO `score` VALUES (1001, 10, 4, '2023-4-18');
INSERT INTO `score` VALUES (1002, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(12) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '用户账号',
  `password` varchar(12) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '用户密码',
  `age` int NULL DEFAULT NULL COMMENT '用户年龄',
  `address` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '用户住址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1038 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (17, 'xiaoniao', '111', 18, '湖南省长沙市');
INSERT INTO `user` VALUES (1001, 'admin', '123', 18, '无名市');
INSERT INTO `user` VALUES (1002, 'zhangsan', '111', 11, 'China');
INSERT INTO `user` VALUES (1003, 'aa0', '123a0', 18, NULL);
INSERT INTO `user` VALUES (1004, 'aa1', '123a1', 19, NULL);
INSERT INTO `user` VALUES (1005, 'aa2', '123a2', 20, NULL);
INSERT INTO `user` VALUES (1006, 'aa3', '123a3', 21, NULL);
INSERT INTO `user` VALUES (1007, 'aa4', '123a4', 22, NULL);
INSERT INTO `user` VALUES (1008, 'xiao', '1122', 18, NULL);
INSERT INTO `user` VALUES (1009, 'wuwu', '222', NULL, NULL);
INSERT INTO `user` VALUES (1010, 'kk', '5566', NULL, NULL);
INSERT INTO `user` VALUES (1011, 'kkkk', '5555', NULL, NULL);
INSERT INTO `user` VALUES (1012, 'xiao1', '122', 11, NULL);
INSERT INTO `user` VALUES (1013, 'xiao2', '1223', 121, NULL);
INSERT INTO `user` VALUES (1017, 'ooo', '123a', 14, '华纳');
INSERT INTO `user` VALUES (1018, 'test01', '123', NULL, NULL);
INSERT INTO `user` VALUES (1020, 'test02', '123', NULL, NULL);
INSERT INTO `user` VALUES (1022, '123456', '123456', NULL, NULL);
INSERT INTO `user` VALUES (1024, 'ooo2', '123a', 145, '华纳');
INSERT INTO `user` VALUES (1033, '小鸟', '1', 22, '湖北武汉');
INSERT INTO `user` VALUES (1034, '李四', '1234', 18, NULL);
INSERT INTO `user` VALUES (1035, '1111', '1112', 18, '武钢');
INSERT INTO `user` VALUES (1036, '2222', '2222', NULL, NULL);
INSERT INTO `user` VALUES (1037, 'xiaowei', '12345', 14, '邵阳');

SET FOREIGN_KEY_CHECKS = 1;
