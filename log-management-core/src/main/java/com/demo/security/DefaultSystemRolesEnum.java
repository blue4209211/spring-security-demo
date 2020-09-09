package com.demo.security;

import com.demo.controller.AccountsPermissionsEnum;
import com.demo.controller.AdminPermissionsEnum;
import com.demo.controller.DataPermissionsEnum;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum DefaultSystemRolesEnum {
    //Marker Role, anyone with this will have all the access
    SUPER_ADMIN(AdminPermissionsEnum.SUPER_ADMIN),
    DATA_ACCESS(DataPermissionsEnum.ACCOUNT_UPLOAD_FILE),
    ACCOUNT_ADMIN(AccountsPermissionsEnum.values()),
    ACCOUNT_USER(AccountsPermissionsEnum.ACCOUNT_LIST_AGENT, AccountsPermissionsEnum.ACCOUNT_LIST_USER, AccountsPermissionsEnum.ACCOUNT_LIST_EVENTS);

    private Set<PermissionEnum> permissions;

    private DefaultSystemRolesEnum(PermissionEnum... permissionEnums) {
        permissions = new HashSet();
        for (PermissionEnum pe : permissionEnums) {
            permissions.add(pe);
        }
        permissions = Collections.unmodifiableSet(permissions);
    }

    public Set<PermissionEnum> permissions() {
        return this.permissions;
    }
}
