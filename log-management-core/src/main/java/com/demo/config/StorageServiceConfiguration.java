package com.demo.config;

import com.demo.service.FileStorageService;
import com.demo.service.LocalStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageServiceConfiguration {

    @Autowired
    private AppProperties appProperties;

    //Create service based on condition like S3/local etc
    @Bean
    public FileStorageService getFileStorage() {
        return new LocalStorageService(appProperties);
    }
}
