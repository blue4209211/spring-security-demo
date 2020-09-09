package com.demo.config;

import com.demo.controller.AccountsPermissionsEnum;
import com.demo.controller.AdminPermissionsEnum;
import com.demo.controller.DataPermissionsEnum;
import com.demo.repository.*;
import com.demo.repository.model.*;
import com.demo.security.DefaultSystemRolesEnum;
import com.demo.security.PermissionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private AccountUserRoleRepository accountUserRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAttrRepository userAttrRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionRoleRepository permissionRoleRepository;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        createPermissionsAndDefaultRolesIfNotFound();
        //Create Super Admin Account And User
        createSuperAdminAccountIfNotFound();

        alreadySetup = true;
    }

    private final void createPermissionsAndDefaultRolesIfNotFound() {
        //create permissions
        Set<PermissionEnum> allPermissions = new HashSet<>();
        for (AdminPermissionsEnum ape : AdminPermissionsEnum.values()) {
            allPermissions.add(ape);
        }
        for (AccountsPermissionsEnum ape : AccountsPermissionsEnum.values()) {
            allPermissions.add(ape);
        }
        for (DataPermissionsEnum ape : DataPermissionsEnum.values()) {
            allPermissions.add(ape);
        }

        for (PermissionEnum pe : allPermissions) {
            if (!permissionRepository.existsByNameIgnoreCase(pe.name())) {
                permissionRepository.save(new Permission(pe.name()));
            }
        }

        //create default roles
        for (DefaultSystemRolesEnum re : DefaultSystemRolesEnum.values()) {
            if (!roleRepository.existsByNameIgnoreCase(re.name())) {
                roleRepository.save(new Role(re.name(), true));
            }
        }

        //create default roles mappings
        for (DefaultSystemRolesEnum re : DefaultSystemRolesEnum.values()) {
            if (!re.permissions().isEmpty()) {
                Optional<Role> r = roleRepository.getByNameIgnoreCase(re.name());
                for (PermissionEnum pe : re.permissions()) {
                    if (!permissionRoleRepository.existsByPermissionNameAndRoleName(pe.name(), re.name())) {
                        Optional<Permission> p = permissionRepository.getByNameIgnoreCase(pe.name());
                        permissionRoleRepository.save(new PermissionRole(p.get(), r.get()));
                    }
                }
            }
        }
    }

    private final void createSuperAdminAccountIfNotFound() {
        Optional<Account> accountOptional = accountRepository.findByNameIgnoreCase(appProperties.getAdmin().getAccountName());
        Account account = null;
        if (!accountOptional.isPresent()) {
            account = new Account(appProperties.getAdmin().getAccountName());
            account = accountRepository.save(account);
        } else {
            account = accountOptional.get();
        }

        appProperties.getAdmin().setAccountId(account.getId());

        Optional<User> userOptional = userRepository.findByUserId(appProperties.getAdmin().getUserName());
        User user = null;
        if (!userOptional.isPresent()) {
            user = new User(appProperties.getAdmin().getUserName(), passwordEncoder.encode(appProperties.getAdmin().getPassword()));
            final UserAttr userEmailAttr = new UserAttr(UserAttr.USER_EMAIL, appProperties.getAdmin().getEmail(), user);
            final UserAttr userNameAttr = new UserAttr(UserAttr.USER_NAME, appProperties.getAdmin().getName(), user);
            user.setUserAttrs(Arrays.asList(userEmailAttr,userNameAttr));
            user = userRepository.save(user);
        } else {
            user = userOptional.get();
        }

        Optional<Role> superAdmin = roleRepository.getByNameIgnoreCase(DefaultSystemRolesEnum.SUPER_ADMIN.name());
        Optional<AccountUserRole> accountUserRoleOptional = accountUserRoleRepository.findByAccountAndUserAndRole(account, user, superAdmin.get());
        AccountUserRole accountUserRole = null;
        if (!accountUserRoleOptional.isPresent()) {
            Optional<Role> role = roleRepository.getByNameIgnoreCase(DefaultSystemRolesEnum.SUPER_ADMIN.toString());
            accountUserRole = new AccountUserRole(account, user, role.get());
            accountUserRole = accountUserRoleRepository.save(accountUserRole);
        } else {
            accountUserRole = accountUserRoleOptional.get();
        }
    }

}
