CREATE TABLE `dbc_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(500) DEFAULT NULL,
  `full_name` varchar(1000) DEFAULT NULL,
  `password` varchar(1000) DEFAULT NULL,
  `passwordencrypted` varchar(1000) DEFAULT NULL,
  `email` varchar(500) DEFAULT NULL,
  `address` varchar(1000) DEFAULT NULL,
  `telno` varchar(500) DEFAULT NULL,
  `imageurl` varchar(2000) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `dbc_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) DEFAULT NULL,
  `type_` tinyint(4) DEFAULT NULL,
  `subtype` tinyint(4) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `description` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `dbc_user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


INSERT INTO dbc_user (user_id, user_name, full_name, password, passwordencrypted, email, address, telno, imageurl, status )
VALUES (1, 'admin', 'Administrator', '123456', '$2a$10$cohMAOy3jujZ9PqoEAf0CeGUytbbworRerS7o8uCF9QEXeiBPry8G', 'admin@dbc.com', '', '', '', 1);

INSERT INTO dbc_role (role_id, name, type_, subtype, status, description)
VALUES (1, 'admin', 1, 1, 1, 'admin');

INSERT INTO dbc_user_role (user_id, role_id) VALUES (1,1);
