package com.demo.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        })
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @NotNull(message = "name cannot be null")
    private String name;

    @Column
    private boolean systemRole = false;

    @Column
    private String description;

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "role",
            fetch = FetchType.LAZY
    )
    private Set<PermissionRole> permissionRoles;

    public Role() {

    }

    public Role(String name, boolean isSystem) {
        this.name = name;
        this.systemRole = isSystem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<PermissionRole> getPermissionRoles() {
        return permissionRoles;
    }

    public void setPermissionRoles(Set<PermissionRole> permissionRoles) {
        this.permissionRoles = permissionRoles;
    }

    public boolean isSystemRole() {
        return systemRole;
    }

    public void setSystemRole(boolean defaultRole) {
        this.systemRole = defaultRole;
    }
}
