package com.matheusslr.springsecurity.repository;

import com.matheusslr.springsecurity.domain.Anime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimeRepository extends JpaRepository<Anime, Long> {
}
