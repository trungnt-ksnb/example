CREATE TABLE `dbc_converttask` (
  `task_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_name` varchar(255) DEFAULT NULL,
  `source_config` varchar(1000) DEFAULT NULL,
  `target_config` varchar(1000) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `dbc_scriptqueue` (
  `queue_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20),
  `script_name` varchar(255) DEFAULT NULL,
  `script` varchar(4000) DEFAULT NULL,
  `order_` tinyint(4) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`queue_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `dbc_processlog` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `source_tbl_name` varchar(255) DEFAULT NULL,
  `source_col_name` varchar(255) DEFAULT NULL,
  `source_value` varchar(255) NOT NULL,
  `target_tbl_name` varchar(255) DEFAULT NULL,
  `target_col_name` varchar(255) DEFAULT NULL,
  `target_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;