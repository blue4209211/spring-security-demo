package com.demo.controller.payload;

import com.demo.repository.model.Account;

public class AccountGetResponse {
    private final long id;
    private final String name;

    public AccountGetResponse(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public AccountGetResponse(Account account) {
        this(account.getId(), account.getName());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
