package com.matheusslr.springsecurity.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Entity
@Table(name = "tb_anime")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Anime extends RepresentationModel<Anime> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "producer_id")
    private Producer producer;

}
