package com.demo.controller.payload;

import javax.validation.constraints.NotBlank;

public class TokenCreateRequest {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
