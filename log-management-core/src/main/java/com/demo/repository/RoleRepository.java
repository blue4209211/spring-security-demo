package com.demo.repository;

import com.demo.repository.model.Permission;
import com.demo.repository.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> getByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
