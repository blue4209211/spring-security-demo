package com.demo.controller;

import com.demo.config.AppProperties;
import com.demo.controller.payload.*;
import com.demo.exception.BadRequestException;
import com.demo.exception.ResourceNotFoundException;
import com.demo.repository.*;
import com.demo.repository.model.*;
import com.demo.security.*;
import com.demo.util.AuthorizationUtils;
import com.demo.util.PasswordUtils;
import com.demo.util.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
@Transactional
public class AccountsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAttrRepository userAttrRepository;

    @Autowired
    private AccountUserRoleRepository accountUserRoleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorizationUtils authorizationUtils;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private AuditEventLogRepository auditEventLogRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/{accountId}/events")
    public List<AuditEventLog> getAccountEvents(@PathVariable("accountId") Long accountId, @CurrentUser IUserPrincipal userPrincipal) {
        Account account = authorizationUtils.isloggedInUserAuthorized(accountId, userPrincipal, AccountsPermissionsEnum.ACCOUNT_LIST_EVENTS);
        return auditEventLogRepository.findByAccountIdOrderByTimestampDesc(account.getId());
    }


    @GetMapping("/{accountId}/users")
    public List<UserAccountOnlyDescribeResponse> listUsers(@PathVariable("accountId") Long accountId, @CurrentUser IUserPrincipal userPrincipal) {
        Account account = authorizationUtils.isloggedInUserAuthorized(accountId, userPrincipal, AccountsPermissionsEnum.ACCOUNT_LIST_USER);
        return userRepository.findByUserTypeAndRolesAccount(UserTypeEnum.USER, account)
                .stream()
                .map(u -> new UserAccountOnlyDescribeResponse(u, account))
                .collect(Collectors.toList());
    }

    @PostMapping("/{accountId}/users")
    public UserAccountRegisterResponse registerUserToAccount(@PathVariable("accountId") Long accountId, @CurrentUser IUserPrincipal userPrincipal, @Valid @RequestBody UserAccountRegisterRequest userRegisterRequest) {
        Account account = authorizationUtils.isloggedInUserAuthorized(accountId, userPrincipal, AccountsPermissionsEnum.ACCOUNT_ADD_USER);
        if (appProperties.getAdmin().getAccountId() == accountId && userRegisterRequest.getRole() == DefaultSystemRolesEnum.SUPER_ADMIN) {
            throw new BadRequestException("SUPER_ADMIN Role is not allowed");
        }

        Optional<User> userOptional = userRepository.findByUserId(userRegisterRequest.getUserId());
        if (!userOptional.isPresent()) {
            User user = new User(userRegisterRequest.getUserId());
            user.setProvider(AuthProviderEnum.google);
            user.setStatus(ModelStatusEnum.DISABLED);
            final UserAttr emailAttr = new UserAttr(UserAttr.USER_EMAIL, userRegisterRequest.getUserId(), user);
            user.setUserAttrs(Arrays.asList(emailAttr));
            user = userRepository.save(user);
            userOptional = Optional.of(user);
        }
        Optional<Role> role = roleRepository.getByNameIgnoreCase(userRegisterRequest.getRole().name());
        //remove existing roles of USER and Then add new Role,
        //Its possible to have more than One role, though this is for simplicity
        accountUserRoleRepository.deleteByAccountAndUser(account,userOptional.get());
        accountUserRoleRepository.save(new AccountUserRole(account, userOptional.get(), role.get()));

        Map<String, Object> data = new HashMap<>();
        data.put("role", userRegisterRequest.getRole().toString());
        data.put("user", userOptional.get().getId());
        try {
            auditEventLogRepository.save(new AuditEventLog(accountId, userPrincipal.getId(), "ACCOUNT_USER_CREATE", SerializationUtils.toJSONString(data)));
        } catch (Exception e) {
            LOGGER.error("unable to store audit events", e);
        }

        return new UserAccountRegisterResponse(userOptional.get(), account);
    }


    @GetMapping("/{accountId}/users/me")
    public UserAccountOnlyDescribeResponse getAccounttUser(@PathVariable("accountId") Long accountId, @CurrentUser IUserPrincipal userPrincipal) {
        Account account = authorizationUtils.getAccountOrThrowError(accountId);
        return new UserAccountOnlyDescribeResponse(userRepository.getOne(userPrincipal.getId()), account);
    }

    @GetMapping("/{accountId}/users/{userId}")
    public UserAccountOnlyDescribeResponse getUser(@PathVariable("accountId") Long accountId, @PathVariable("userId") Long userId, @CurrentUser IUserPrincipal userPrincipal) {
        Account account = authorizationUtils.isloggedInUserAuthorized(accountId, userPrincipal, AccountsPermissionsEnum.ACCOUNT_DESCRIBE_USER);

        Optional<User> u = userRepository.findById(userPrincipal.getId());
        if (u.isPresent()) {
            return new UserAccountOnlyDescribeResponse(u.get(), account);
        }
        throw new ResourceNotFoundException("user", "id", userId);
    }

    @DeleteMapping("/{accountId}/users/{userId}")
    public UserDeleteFromAccountResponse deleteUser(@CurrentUser IUserPrincipal userPrincipal, @PathVariable("accountId") long accountId, @PathVariable("userId") long userId) {
        Account account = authorizationUtils.isloggedInUserAuthorized(accountId, userPrincipal, AccountsPermissionsEnum.ACCOUNT_REMOVE_USER);
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("user", "id", userId);
        }
        if (accountId == appProperties.getAdmin().getAccountId() && userOptional.get().getUserId().equalsIgnoreCase(appProperties.getAdmin().getUserName())) {
            throw new BadRequestException("User Account cannot be deleted");
        }

        accountUserRoleRepository.deleteByAccountAndUser(account, userOptional.get());

        Map<String, Object> data = new HashMap<>();
        data.put("user", userOptional.get().getId());
        try {
            auditEventLogRepository.save(new AuditEventLog(accountId, userPrincipal.getId(), "ACCOUNT_USER_REMOVE", SerializationUtils.toJSONString(data)));
        } catch (Exception e) {
            LOGGER.error("unable to store audit events", e);
        }

        return new UserDeleteFromAccountResponse(account.getId(), userOptional.get().getId());
    }


    @GetMapping("/{accountId}/tokens")
    public List<UserAccountOnlyDescribeResponse> listTokens(@PathVariable("accountId") Long accountId, @CurrentUser IUserPrincipal userPrincipal) {
        Account account = authorizationUtils.isloggedInUserAuthorized(accountId, userPrincipal, AccountsPermissionsEnum.ACCOUNT_LIST_TOKEN);
        return userRepository.findByUserTypeAndRolesAccount(UserTypeEnum.TOKEN, account)
                .stream()
                .map(u -> new UserAccountOnlyDescribeResponse(u, account))
                .collect(Collectors.toList());
    }

    @PostMapping("/{accountId}/tokens")
    public ResponseEntity<?> addToken(@PathVariable("accountId") long accountId, @CurrentUser IUserPrincipal userPrincipal, @Valid @RequestBody TokenCreateRequest signUpRequest) {
        Account account = authorizationUtils.isloggedInUserAuthorized(accountId, userPrincipal, AccountsPermissionsEnum.ACCOUNT_ADD_TOKEN);

        String clientSecret = PasswordUtils.generateSecureRandomPassword();
        User token = new User(String.format("%s-%s-%s", accountId, System.currentTimeMillis(), "client-id"), passwordEncoder.encode(clientSecret));

        final HashSet<AccountUserRole> roles = new HashSet<AccountUserRole>();
        Optional<Role> role = roleRepository.getByNameIgnoreCase(DefaultSystemRolesEnum.DATA_ACCESS.name());
        roles.add(new AccountUserRole(account, token, role.get()));
        token.setRoles(roles);
        token.setUserType(UserTypeEnum.TOKEN);

        final List<UserAttr> userAttrs = new ArrayList<>();
        userAttrs.add(new UserAttr(UserAttr.USER_NAME, signUpRequest.getName(), token));
        token.setUserAttrs(userAttrs);

        User result = userRepository.save(token);

        Map<String, Object> data = new HashMap<>();
        data.put("id", result.getId());
        data.put("name", signUpRequest.getName());
        try {
            auditEventLogRepository.save(new AuditEventLog(accountId, userPrincipal.getId(), "ACCOUNT_TOKEN_CREATE", SerializationUtils.toJSONString(data)));
        } catch (Exception e) {
            LOGGER.error("unable to store audit events", e);
        }


        TokenCreateResponse res = new TokenCreateResponse();
        res.setClientId(token.getUserId());
        res.setClientSecret(clientSecret);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{accountId}/tokens/{tokenId}")
    public UserDeleteFromAccountResponse deleteToken(@CurrentUser IUserPrincipal userPrincipal, @PathVariable("accountId") Long accountId, @PathVariable("tokenId") Long tokenId) {
        Account account = authorizationUtils.isloggedInUserAuthorized(accountId, userPrincipal, AccountsPermissionsEnum.ACCOUNT_REMOVE_TOKEN);

        if (!userRepository.existsById(tokenId)) {
            throw new ResourceNotFoundException("token", "id", tokenId);
        }
        userRepository.deleteById(tokenId);

        Map<String, Object> data = new HashMap<>();
        data.put("id", tokenId);
        try {
            auditEventLogRepository.save(new AuditEventLog(accountId, userPrincipal.getId(), "ACCOUNT_TOKEN_DELETE", SerializationUtils.toJSONString(data)));
        } catch (Exception e) {
            LOGGER.error("unable to store audit events", e);
        }


        return new UserDeleteFromAccountResponse(accountId, tokenId);
    }

}
