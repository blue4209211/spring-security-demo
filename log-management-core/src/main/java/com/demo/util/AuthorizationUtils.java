package com.demo.util;

import com.demo.controller.AdminPermissionsEnum;
import com.demo.exception.ResourceNotFoundException;
import com.demo.repository.AccountRepository;
import com.demo.repository.AccountUserRoleRepository;
import com.demo.repository.model.Account;
import com.demo.repository.model.AccountUserRole;
import com.demo.security.CurrentUser;
import com.demo.security.DefaultSystemRolesEnum;
import com.demo.security.IUserPrincipal;
import com.demo.security.PermissionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorizationUtils {

    private final AccountRepository accountRepository;

    private final AccountUserRoleRepository accountUserRoleRepository;

    @Autowired
    public AuthorizationUtils(AccountRepository accountRepository, AccountUserRoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.accountUserRoleRepository = roleRepository;
    }


    public Account getAccountOrThrowError(long accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            throw new ResourceNotFoundException("account", "id", accountId);
        }

        return accountOptional.get();
    }

    public Account loggedInUserAuthorizedToCreateEntity(long accountId, @CurrentUser IUserPrincipal userPrincipal) {
        Account account = getAccountOrThrowError(accountId);

        Set<AccountUserRole> userAccountRoles = accountUserRoleRepository.findByAccountIdAndUserId(accountId, userPrincipal.getId());
        Set<Long> userAccountIds = userAccountRoles.stream().map(aur -> aur.getAccount().getId()).collect(Collectors.toSet());
        Set<String> userRoles = userAccountRoles.stream()
                .flatMap(aur -> aur.getRole()
                        .getPermissionRoles()
                        .stream()
                        .map(pr -> pr.getPermission().getName()))
                .collect(Collectors.toSet());

        Set<PermissionEnum> permissionToCheck = null;
        if (!userAccountIds.contains(accountId)) {
            permissionToCheck = DefaultSystemRolesEnum.SUPER_ADMIN.permissions();
        } else {
            permissionToCheck = DefaultSystemRolesEnum.ACCOUNT_ADMIN.permissions();
        }

        permissionsExistsElseThrowError(userRoles, permissionToCheck);

        return account;
    }

    public Account loggedInUserAuthorizedToListEntity(long accountId, IUserPrincipal userPrincipal) {
        Account account = getAccountOrThrowError(accountId);
        Set<AccountUserRole> userAccountRoles = accountUserRoleRepository.findByAccountIdAndUserId(accountId, userPrincipal.getId());
        Set<Long> userAccountIds = userAccountRoles.stream().map(aur -> aur.getAccount().getId()).collect(Collectors.toSet());
        Set<String> userRoles = userAccountRoles.stream()
                .flatMap(aur -> aur.getRole()
                        .getPermissionRoles()
                        .stream()
                        .map(pr -> pr.getPermission().getName()))
                .collect(Collectors.toSet());

        if (!userAccountIds.contains(accountId)) {
            permissionsExistsElseThrowError(userRoles, DefaultSystemRolesEnum.SUPER_ADMIN.permissions());
        } else {
            Set<PermissionEnum> permissions = new HashSet<>();
            permissions.addAll(DefaultSystemRolesEnum.ACCOUNT_ADMIN.permissions());
            permissions.addAll(DefaultSystemRolesEnum.ACCOUNT_USER.permissions());
            permissionsExistsElseThrowError(userRoles, permissions);
        }

        return account;
    }

    public Account loggedInAgentAuthorizedToUploadFile(long accountId, IUserPrincipal userPrincipal) {
        Account account = getAccountOrThrowError(accountId);

        Set<AccountUserRole> userAccountRoles = accountUserRoleRepository.findByAccountIdAndUserId(accountId, userPrincipal.getId());
        Set<Long> userAccountIds = userAccountRoles.stream().map(aur -> aur.getAccount().getId()).collect(Collectors.toSet());
        Set<String> userRoles = userAccountRoles.stream()
                .flatMap(aur -> aur.getRole()
                        .getPermissionRoles()
                        .stream()
                        .map(pr -> pr.getPermission().getName()))
                .collect(Collectors.toSet());

        if (!userAccountIds.contains(accountId)) {
            permissionsExistsElseThrowError(userRoles, DefaultSystemRolesEnum.SUPER_ADMIN.permissions());
        } else {
            permissionsExistsElseThrowError(userRoles, DefaultSystemRolesEnum.DATA_ACCESS.permissions());
        }

        return account;

    }

    private static void permissionsExistsElseThrowError(Set<String> userAccountPermissions, Set<PermissionEnum> permissionsToCheck) {
        if (userAccountPermissions.contains(AdminPermissionsEnum.SUPER_ADMIN.name())) {
            return;
        }

        boolean accessAllowed = false;
        for (String re : userAccountPermissions) {
            for (PermissionEnum roleToCheck : permissionsToCheck) {
                if (roleToCheck.toString().equalsIgnoreCase(re)) {
                    accessAllowed = true;
                    break;
                }
            }
        }

        if (!accessAllowed) {
            throw new AuthorizationServiceException("User is not authorized");
        }
    }

}
