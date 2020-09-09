package com.demo.controller.payload;

public class AgentFileuploadResponse {
    private final String fileName;

    public AgentFileuploadResponse(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
