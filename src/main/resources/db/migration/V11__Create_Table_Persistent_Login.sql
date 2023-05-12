CREATE TABLE IF NOT EXISTS `tb_remember_me_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `token_value` varchar(255) NOT NULL,
  `create_at` DATE NOT NULL,
  `expires_in` DATE NOT NULL,
  PRIMARY KEY (`id`)
);