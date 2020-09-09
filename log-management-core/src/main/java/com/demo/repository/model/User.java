package com.demo.repository.model;

import com.demo.security.AuthProviderEnum;
import com.demo.security.ModelStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "userId")
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull(message = "userId cannot be null")
    private String userId;

    @JsonIgnore
    private String password;

    @NotNull(message = "provider cannot be null")
    @Enumerated(EnumType.STRING)
    private AuthProviderEnum provider;

    private String providerId;

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private Set<AccountUserRole> roles = new HashSet<>();

    @Column(nullable = false)
    private Timestamp createdOn;

    @NotNull(message = "status cannot be null")
    @Enumerated(EnumType.STRING)
    private ModelStatusEnum status;

    @NotNull(message = "userType cannot be null")
    @Enumerated(EnumType.STRING)
    private UserTypeEnum userType = UserTypeEnum.USER;

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<UserAttr> userAttrs = new ArrayList<>();

    public User() {
        this.createdOn = Timestamp.from(Instant.now());
        this.userType = UserTypeEnum.USER;
        this.status = ModelStatusEnum.ACTIVE;
    }

    public User(String userId) {
        this();
        this.userId = userId;
    }

    public User(String userId, String password) {
        this(userId);
        this.password = password;
        this.provider = AuthProviderEnum.local;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public AuthProviderEnum getProvider() {
        return provider;
    }

    public void setProvider(AuthProviderEnum provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Set<AccountUserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<AccountUserRole> roles) {
        this.roles = roles;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public ModelStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ModelStatusEnum status) {
        this.status = status;
    }

    public UserTypeEnum getUserType() {
        return userType;
    }

    public void setUserType(UserTypeEnum userType) {
        this.userType = userType;
    }

    public List<UserAttr> getUserAttrs() {
        return userAttrs;
    }

    public void setUserAttrs(List<UserAttr> userAttrs) {
        this.userAttrs = userAttrs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
