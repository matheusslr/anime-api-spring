CREATE TABLE IF NOT EXISTS `tb_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `is_account_non_expired` BOOLEAN NOT NULL,
  `is_account_non_locked` BOOLEAN NOT NULL,
  `is_credentials_non_expired` BOOLEAN NOT NULL,
  `is_enabled` BOOLEAN NOT NULL,
  PRIMARY KEY (`id`)
);