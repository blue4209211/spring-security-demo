package com.demo.controller.payload;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class AccountCreateRequest {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String accountAdmin;

    public String getName() {
        return name;
    }

    public String getAccountAdmin() {
        return accountAdmin;
    }
}
