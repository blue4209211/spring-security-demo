package com.demo.repository.model;

import com.demo.security.ModelStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        })
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Timestamp createdOn;

    @JsonIgnore
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "account",
            fetch = FetchType.LAZY
    )
    private Set<AccountUserRole> accountUserRoles = new HashSet<>();


    @NotNull
    @Enumerated(EnumType.STRING)
    private ModelStatusEnum status = ModelStatusEnum.ACTIVE;

    public Account() {
        this.createdOn = Timestamp.from(Instant.now());
    }

    public Account(String name) {
        this();
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Set<AccountUserRole> getAccountUserRoles() {
        return accountUserRoles;
    }

    public void setAccountUserRoles(Set<AccountUserRole> accountUserRoles) {
        this.accountUserRoles = accountUserRoles;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Account{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", createdOn=").append(createdOn);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
