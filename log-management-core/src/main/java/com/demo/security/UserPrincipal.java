package com.demo.security;

import com.demo.repository.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

public class UserPrincipal implements IUserPrincipal {
    private Long id;
    private String email;
    private String password;
    private Collection<AccountRoleGrantedAuthority> authorities;
    private Map<String, Object> attributes = new HashMap<>();

    public UserPrincipal(Long id, String email, String password,
                         Collection<AccountRoleGrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<AccountRoleGrantedAuthority> authorities = user.getRoles().stream()
                .flatMap(a -> a.getRole().getPermissionRoles()
                        .stream()
                        .map((pr) -> new AccountRoleGrantedAuthority(a.getAccount().getId(), pr.getPermission().getName())))
                .collect(Collectors.toList());
        return new UserPrincipal(user.getId(), user.getUserId(),
                user.getPassword(), authorities);
    }

    public static UserPrincipal create(User user,
                                       Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

    public static class AccountRoleGrantedAuthority implements GrantedAuthority {

        private final String role;
        private final long accountId;

        public AccountRoleGrantedAuthority(long accountId, String role) {
            this.accountId = accountId;
            this.role = role;
        }

        @Override
        public String getAuthority() {
            return role;
        }

        public Long getAccountId() {
            return this.accountId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AccountRoleGrantedAuthority that = (AccountRoleGrantedAuthority) o;
            return accountId == that.accountId &&
                    role == that.role;
        }

        @Override
        public int hashCode() {
            return Objects.hash(role, accountId);
        }
    }

}
