package com.demo.repository;

import com.demo.repository.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> getByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
