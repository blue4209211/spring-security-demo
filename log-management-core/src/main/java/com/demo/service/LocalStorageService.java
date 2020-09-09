package com.demo.service;

import com.demo.config.AppProperties;
import com.demo.repository.AuditEventLogRepository;
import com.demo.repository.model.Account;
import com.demo.repository.model.AuditEventLog;
import com.demo.security.IUserPrincipal;
import com.demo.security.UserPrincipal;
import com.demo.util.AuthorizationUtils;
import com.demo.util.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Transactional
public class LocalStorageService implements FileStorageService {
    private final String fileStorageLocation;

    @Autowired
    private AuthorizationUtils authorizationUtils;

    @Autowired
    private AuditEventLogRepository auditEventLogRepository;

    @Autowired
    public LocalStorageService(AppProperties appProperties) {
        this.fileStorageLocation = appProperties.getUploadDir();
    }

    public String storeFile(Account account, IUserPrincipal userPrincipal, MultipartFile file)
            throws Exception {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new Exception(
                        "Sorry! Filename contains invalid path sequence "
                                + fileName);
            }

            // Copy file to the target location (Replacing existing file with
            // the same name)
            Path storageLocation = Paths
                    .get(this.fileStorageLocation + File.separator
                            + account.getId())
                    .normalize().toAbsolutePath();
            Files.createDirectories(storageLocation);

            Path targetLocation = storageLocation.resolve(userPrincipal.getUsername() + "_" + System.currentTimeMillis() + "_" + fileName);
            Files.copy(file.getInputStream(), targetLocation,
                    StandardCopyOption.REPLACE_EXISTING);
            Map<String, String> data = new HashMap<>();
            data.put("source", fileName);
            data.put("destination", targetLocation.getFileName().toString());
            data.put("accessToken", userPrincipal.getUsername());
            auditEventLogRepository.save(new AuditEventLog(account.getId(), userPrincipal.getId(), "TOKEN_FILE_UPLOAD", SerializationUtils.toJSONString(data)));
            return fileName;
        } catch (IOException ex) {
            throw new Exception(
                    "Could not store file " + fileName + ". Please try again!",
                    ex);
        }
    }

    public Resource loadFileAsResource(Account account, UserPrincipal userPrincipal,
                                       String fileName) throws Exception {
        try {
            // Copy file to the target location (Replacing existing file with
            // the same name)
            Path storageLocation = Paths
                    .get(this.fileStorageLocation + File.separator
                            + account.getId())
                    .normalize().toAbsolutePath();
            Path filePath = storageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new Exception("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new Exception("File not found " + fileName, ex);
        }
    }
}

