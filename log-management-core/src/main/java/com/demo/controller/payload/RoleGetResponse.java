package com.demo.controller.payload;

import com.demo.repository.model.AccountUserRole;

public class RoleGetResponse {
    private final Long accountId;
    private final String name;

    public RoleGetResponse(AccountUserRole ac) {
        this.name = ac.getRole().getName();
        this.accountId = ac.getAccount().getId();
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }
}
