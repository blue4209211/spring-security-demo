package com.demo.controller.payload;

import com.demo.exception.BadRequestException;
import com.demo.repository.model.*;
import com.demo.security.AuthProviderEnum;
import com.demo.security.ModelStatusEnum;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAccountOnlyDescribeResponse {
    private final Long id;
    private final String userId;
    private final String name;
    private final Map<String, Object> attrs = new HashMap<>();
    private final Timestamp createdOn;
    private final AuthProviderEnum authProvider;
    private final ModelStatusEnum status;
    private final Set<AccountGetResponse> accounts;
    private final Set<RoleGetResponse> roles;

    public UserAccountOnlyDescribeResponse(User user, Account account) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.createdOn = user.getCreatedOn();
        this.authProvider = user.getProvider();
        this.status = user.getStatus();

        accounts = user.getRoles()
                .stream()
                .filter(aur -> aur.getAccount().getId().equals(account.getId()))
                .map(ac -> new AccountGetResponse(ac.getAccount()))
                .collect(Collectors.toSet());

        if (accounts.isEmpty()) {
            throw new BadRequestException("User Doesnt have access to account");
        }

        roles = user.getRoles()
                .stream()
                .filter(aur -> aur.getAccount().getId().equals(account.getId()))
                .map(ac -> new RoleGetResponse(ac))
                .collect(Collectors.toSet());

        String userName = null;
        for (UserAttr userAttr : user.getUserAttrs()) {
            if (userAttr.getName().equalsIgnoreCase(UserAttr.USER_NAME)) {
                userName = userAttr.getValue();
            }
            attrs.put(userAttr.getName(), userAttr.getValue());
        }

        this.name = userName;
    }

    public Set<AccountGetResponse> getAccounts() {
        return accounts;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public AuthProviderEnum getAuthProvider() {
        return authProvider;
    }

    public ModelStatusEnum getStatus() {
        return status;
    }

    public Set<RoleGetResponse> getRoles() {
        return roles;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }
}
