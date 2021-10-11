DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`       int NOT NULL AUTO_INCREMENT,
    `country_id` int NOT NULL,
    `user_name` varchar(255) NOT NULL,
    `email`    varchar(255) NOT NULL,
    `gender` tinyint(4) NOT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `user_tag`;
CREATE TABLE `user_tag`
(
    `id`      int NOT NULL AUTO_INCREMENT,
    `user_id` int NOT NULL,
    `tag`   varchar(255) NOT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `country`;
CREATE TABLE `country`
(
    `id`      int NOT NULL AUTO_INCREMENT,
    `name`   varchar(255) NOT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
);
