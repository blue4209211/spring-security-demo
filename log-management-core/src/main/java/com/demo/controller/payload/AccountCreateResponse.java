package com.demo.controller.payload;

public class AccountCreateResponse {
    private final long id;
    private final String name;

    public AccountCreateResponse(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
