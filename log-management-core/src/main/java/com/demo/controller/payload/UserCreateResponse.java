package com.demo.controller.payload;

public class UserCreateResponse {
    private final long id;

    public UserCreateResponse(Long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
