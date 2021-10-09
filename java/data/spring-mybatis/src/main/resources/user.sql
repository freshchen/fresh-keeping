DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`       int NOT NULL,
    `username` varchar(255) DEFAULT NULL,
    `email`    varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `user_tag`;
CREATE TABLE `user_tag`
(
    `id`      int NOT NULL,
    `user_id` int,
    `tag`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
);
