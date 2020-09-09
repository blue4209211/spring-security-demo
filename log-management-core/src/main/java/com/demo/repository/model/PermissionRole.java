package com.demo.repository.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "permission_role")
public class PermissionRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @ManyToOne
    private Permission permission;

    @NotNull
    @ManyToOne
    private Role role;

    public PermissionRole() {
    }

    public PermissionRole(Permission permission, Role role) {
        this.permission = permission;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
