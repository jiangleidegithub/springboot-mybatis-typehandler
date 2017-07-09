/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50626
Source Host           : localhost:3306
Source Database       : datatest

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2017-07-09 20:49:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `json` longtext,
  `array` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '蒋磊', '{\"height\":\"jianglei\",\"array\":\"123\"}', '[{\"height\":\"jianglei\",\"array\":\"123\"},{\"height\":\"jianglei\",\"array\":\"123\"},{\"height\":\"jianglei\",\"array\":\"123\"}]');
