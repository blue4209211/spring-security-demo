package com.demo.controller.payload;

public class UserDeleteFromAccountResponse {
    private final Long accountId;
    private final Long userId;

    public UserDeleteFromAccountResponse(Long accountId, Long userId) {
        this.accountId = accountId;
        this.userId = userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getUserId() {
        return userId;
    }
}
