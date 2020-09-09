package com.demo.controller.payload;

import com.demo.repository.model.Account;
import com.demo.repository.model.User;

public class UserAccountRegisterResponse {

    private final long userId;
    private final long accountId;

    public UserAccountRegisterResponse(User user, Account account) {
        this.userId = user.getId();
        this.accountId = account.getId();
    }

    public long getUserId() {
        return userId;
    }

    public long getAccountId() {
        return accountId;
    }
}
