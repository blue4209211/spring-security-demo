package com.demo.controller.payload;

public class FileuploadResponse {
    private final String fileName;

    public FileuploadResponse(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
