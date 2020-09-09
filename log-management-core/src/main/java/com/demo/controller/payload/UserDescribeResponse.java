package com.demo.controller.payload;

import com.demo.security.AuthProviderEnum;
import com.demo.security.ModelStatusEnum;
import com.demo.repository.model.User;
import com.demo.repository.model.UserAttr;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDescribeResponse {
    private final Long id;
    private final String userId;
    private final Timestamp createdOn;
    private final AuthProviderEnum authProvider;
    private final ModelStatusEnum status;
    private final Set<AccountGetResponse> accounts;
    private final Set<RoleGetResponse> roles;
    private final Map<String, Object> attrs = new HashMap<>();
    private final String name;

    public UserDescribeResponse(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.createdOn = user.getCreatedOn();
        this.authProvider = user.getProvider();
        this.status = user.getStatus();

        accounts = user.getRoles()
                .stream()
                .map(ac -> new AccountGetResponse(ac.getAccount()))
                .collect(Collectors.toSet());

        roles = user.getRoles()
                .stream()
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

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public String getName() {
        return name;
    }
}
