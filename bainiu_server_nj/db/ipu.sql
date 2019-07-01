/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50018
Source Host           : localhost:3306
Source Database       : ipu

Target Server Type    : MYSQL
Target Server Version : 50018
File Encoding         : 65001

Date: 2015-03-22 23:32:41
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `TABLE_IPU_MEMBER`
-- ----------------------------
DROP TABLE IF EXISTS `TABLE_IPU_MEMBER`;
CREATE TABLE `TABLE_IPU_MEMBER` (
  `STAFF_ID` int(11) NOT NULL,
  `STAFF_NAME` varchar(64) default NULL,
  `PASSWORD` varchar(10) default NULL,
  PRIMARY KEY  (`STAFF_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of TABLE_IPU_MEMBER
-- ----------------------------
INSERT INTO `TABLE_IPU_MEMBER` VALUES ('66009', '黄波', '000000');
INSERT INTO `TABLE_IPU_MEMBER` VALUES ('71335', '阳彪', '123456');
