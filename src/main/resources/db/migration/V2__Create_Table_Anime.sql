CREATE TABLE `tb_anime` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `producer_id` BIGINT,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_producer_id` FOREIGN KEY (`producer_id`) REFERENCES `tb_producer` (`id`)
);