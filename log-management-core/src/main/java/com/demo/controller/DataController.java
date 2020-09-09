package com.demo.controller;

import com.demo.controller.payload.AgentFileuploadResponse;
import com.demo.repository.model.Account;
import com.demo.security.CurrentUser;
import com.demo.security.IUserPrincipal;
import com.demo.service.FileStorageService;
import com.demo.util.AuthorizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/data")
@Transactional
public class DataController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private AuthorizationUtils authorizationUtils;

    @PostMapping(value = "/{accountId}/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AgentFileuploadResponse uploadFile(@PathVariable("accountId") Long accountId, @CurrentUser IUserPrincipal userPrincipal,
                                              @RequestParam("file") MultipartFile file) throws Exception {
        Account account = authorizationUtils.loggedInAgentAuthorizedToUploadFile(accountId, userPrincipal);
        String fileName = fileStorageService.storeFile(account, userPrincipal, file);
        return new AgentFileuploadResponse(fileName);
    }

}
