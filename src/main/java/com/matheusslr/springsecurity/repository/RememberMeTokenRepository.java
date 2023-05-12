package com.matheusslr.springsecurity.repository;

import com.matheusslr.springsecurity.domain.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, Long> {
    RememberMeToken findByUsername(String username);
}
