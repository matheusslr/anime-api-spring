package com.matheusslr.springsecurity.repository;

import com.matheusslr.springsecurity.domain.Producer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProducerRepository extends JpaRepository<Producer, Long> {
}
