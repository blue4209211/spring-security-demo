package com.demo.controller.payload;

public class AccountDeleteResponse {
    private final long id;

    public AccountDeleteResponse(long accountId) {
        this.id = accountId;
    }

    public long getId() {
        return id;
    }
}
