package com.demo.controller.payload;

import com.demo.security.DefaultSystemRolesEnum;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserAccountRegisterRequest {
    @NotBlank
    @Email
    private String userId;
    @NotNull
    private DefaultSystemRolesEnum role;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public DefaultSystemRolesEnum getRole() {
        return role;
    }

    public void setRole(DefaultSystemRolesEnum role) {
        this.role = role;
    }
}
