package com.demo.controller;

import com.demo.config.AppProperties;
import com.demo.controller.payload.AccountCreateRequest;
import com.demo.controller.payload.AccountCreateResponse;
import com.demo.controller.payload.AccountDeleteResponse;
import com.demo.exception.BadRequestException;
import com.demo.exception.ResourceNotFoundException;
import com.demo.repository.*;
import com.demo.repository.model.*;
import com.demo.security.AuthProviderEnum;
import com.demo.security.CurrentUser;
import com.demo.security.IUserPrincipal;
import com.demo.security.DefaultSystemRolesEnum;
import com.demo.util.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@Transactional
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountUserRoleRepository accountUserRoleRepository;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private AuditEventLogRepository auditEventLogRepository;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/accounts")
    public List<Account> listAccounts(@CurrentUser IUserPrincipal userPrincipal) {
        return accountRepository.findAll();
    }

    @PostMapping("/accounts")
    public AccountCreateResponse createAccounts(@CurrentUser IUserPrincipal userPrincipal, @Valid @RequestBody AccountCreateRequest accountCreateRequest) {
        if (accountRepository.existsByNameIgnoreCase(accountCreateRequest.getName())) {
            throw new BadRequestException("Account Already Exists");
        }
        Account account = new Account(accountCreateRequest.getName());
        accountRepository.save(account);

        //Save Account Admin
        Optional<User> userOptional = userRepository.findByUserId(accountCreateRequest.getAccountAdmin());
        User user = null;
        if (!userOptional.isPresent()) {
            user = new User(accountCreateRequest.getAccountAdmin());
            user.setProviderId(AuthProviderEnum.google.toString());
            user = userRepository.save(user);
        } else {
            user = userOptional.get();
        }
        Optional<Role> role = roleRepository.getByNameIgnoreCase(DefaultSystemRolesEnum.ACCOUNT_ADMIN.name());
        accountUserRoleRepository.save(new AccountUserRole(account, user, role.get()));

        Map<String, Object> data = new HashMap<>();
        data.put("name", accountCreateRequest.getName());
        data.put("id", account.getId());
        try {
            auditEventLogRepository.save(new AuditEventLog(userPrincipal.getId(), "ADMIN_ACCOUNT_CREATE", SerializationUtils.toJSONString(data)));
        } catch (Exception e) {
            LOGGER.error("unable to store audit events", e);
        }


        return new AccountCreateResponse(account.getId(), account.getName());
    }

    @DeleteMapping("/accounts/{accountId}")
    public AccountDeleteResponse deleteAccounts(@CurrentUser IUserPrincipal userPrincipal, @PathVariable("accountId") long accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            throw new ResourceNotFoundException("account", "id", accountId);
        }
        if (accountId == appProperties.getAdmin().getAccountId()) {
            throw new BadRequestException("Account cannot be deleted");
        }

        accountUserRoleRepository.deleteByAccount(accountOptional.get());
        accountRepository.delete(accountOptional.get());

        Map<String, Object> data = new HashMap<>();
        data.put("id", accountId);
        try {
            auditEventLogRepository.save(new AuditEventLog(userPrincipal.getId(), "ADMIN_ACCOUNT_DELETE", SerializationUtils.toJSONString(data)));
        } catch (Exception e) {
            LOGGER.error("unable to store audit events", e);
        }

        return new AccountDeleteResponse(accountId);
    }

}
