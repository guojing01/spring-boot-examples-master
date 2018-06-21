CREATE TABLE `sys_permission` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `available` bit(1) DEFAULT NULL,
   `name` varchar(255) DEFAULT NULL,
   `parent_id` bigint(20) DEFAULT NULL,
   `parent_ids` varchar(255) DEFAULT NULL,
   `permission` varchar(255) DEFAULT NULL,
   `resource_type` enum('menu','button') DEFAULT NULL,
   `url` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `sys_role` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `available` bit(1) DEFAULT NULL,
   `description` varchar(255) DEFAULT NULL,
   `role` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `sys_role_permission` (
   `role_id` int(11) NOT NULL,
   `permission_id` int(11) NOT NULL,
   KEY `FKomxrs8a388bknvhjokh440waq` (`permission_id`),
   KEY `FK9q28ewrhntqeipl1t04kh1be7` (`role_id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `sys_user_role` (
   `uid` int(11) NOT NULL,
   `role_id` int(11) NOT NULL,
   KEY `FKhh52n8vd4ny9ff4x9fb8v65qx` (`role_id`),
   KEY `FKgkmyslkrfeyn9ukmolvek8b8f` (`uid`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `user_info` (
   `uid` int(11) NOT NULL AUTO_INCREMENT,
   `name` varchar(255) DEFAULT NULL,
   `password` varchar(255) DEFAULT NULL,
   `salt` varchar(255) DEFAULT NULL,
   `state` tinyint(4) NOT NULL,
   `username` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`uid`),
   UNIQUE KEY `UK_f2ksd6h8hsjtd57ipfq9myr64` (`username`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8



INSERT INTO `user_info` (`uid`,`username`,`name`,`password`,`salt`,`state`) VALUES ('1', 'admin', '管理员', 'd3c59d25033dbf980d29554025c23a75', '8d78869f470951332959580424d4bf4f', 0);
INSERT INTO `sys_permission` (`id`,`available`,`name`,`parent_id`,`parent_ids`,`permission`,`resource_type`,`url`) VALUES (1,0,'用户管理',0,'0/','userInfo:view','menu','userInfo/userList');
INSERT INTO `sys_permission` (`id`,`available`,`name`,`parent_id`,`parent_ids`,`permission`,`resource_type`,`url`) VALUES (2,0,'用户添加',1,'0/1','userInfo:add','button','userInfo/userAdd');
INSERT INTO `sys_permission` (`id`,`available`,`name`,`parent_id`,`parent_ids`,`permission`,`resource_type`,`url`) VALUES (3,0,'用户删除',1,'0/1','userInfo:del','button','userInfo/userDel');
INSERT INTO `sys_role` (`id`,`available`,`description`,`role`) VALUES (1,0,'管理员','admin');
INSERT INTO `sys_role` (`id`,`available`,`description`,`role`) VALUES (2,0,'VIP会员','vip');
INSERT INTO `sys_role` (`id`,`available`,`description`,`role`) VALUES (3,1,'test','test');
INSERT INTO `sys_role_permission` VALUES ('1', '1');
INSERT INTO `sys_role_permission` (`permission_id`,`role_id`) VALUES (1,1);
INSERT INTO `sys_role_permission` (`permission_id`,`role_id`) VALUES (2,1);
INSERT INTO `sys_role_permission` (`permission_id`,`role_id`) VALUES (3,2);
INSERT INTO `sys_user_role` (`role_id`,`uid`) VALUES (1,1);