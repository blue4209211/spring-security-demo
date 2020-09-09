package com.demo.service;

import com.demo.repository.model.Account;
import com.demo.security.IUserPrincipal;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(Account account, IUserPrincipal userPrincipal, MultipartFile file) throws Exception;
}
