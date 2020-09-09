package com.demo.controller;

import com.demo.security.PermissionEnum;

public enum AdminPermissionsEnum implements PermissionEnum {
    SUPER_ADMIN,
    ADMIN_LIST_ACCOUNTS,
    ADMIN_REMOVE_ACCOUNTS,
    ADMIN_DESCRIBE_ACCOUNTS,
    ADMIN_ADD_ACCOUNTS,
    ADMIN_REMOVE_USERS,
}
