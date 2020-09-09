package com.demo.controller;

import com.demo.security.PermissionEnum;

public enum AccountsPermissionsEnum implements PermissionEnum {
    ACCOUNT_LIST_EVENTS,
    ACCOUNT_DESCRIBE_EVENTS,

    ACCOUNT_LIST_USER,
    ACCOUNT_DESCRIBE_USER,
    ACCOUNT_ADD_USER,
    ACCOUNT_REMOVE_USER,

    ACCOUNT_LIST_TOKEN,
    ACCOUNT_DESCRIBE_AGENT,
    ACCOUNT_ADD_TOKEN,
    ACCOUNT_REMOVE_TOKEN,
}
