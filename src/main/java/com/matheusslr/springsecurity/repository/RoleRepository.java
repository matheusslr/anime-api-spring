package com.matheusslr.springsecurity.repository;

import com.matheusslr.springsecurity.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByDescription(String description);
}
