package com.demo.repository;

import com.demo.repository.model.Permission;
import com.demo.repository.model.PermissionRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRoleRepository extends JpaRepository<PermissionRole, Long> {
    boolean existsByPermissionNameAndRoleName(String permissionName, String roleName);
}
