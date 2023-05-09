package com.matheusslr.springsecurity.dto;

import lombok.Data;

@Data
public class AnimeDTO {
    private Long id;
    private String name;
    private String description;
    private Long id_producer;
}
