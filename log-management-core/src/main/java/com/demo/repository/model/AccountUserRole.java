package com.demo.repository.model;

import com.demo.security.DefaultSystemRolesEnum;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * The persistent class for the role database table.
 */
@Entity
@Table(name = "account_user_role",
        indexes = {
                @Index(name = "accountIdIdx", columnList = "account_id"),
                @Index(name = "userIdIdx", columnList = "user_id"),
                @Index(name = "roleIdx", columnList = "role_id")
        }
)
public class AccountUserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "role cannot be null")
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    private Role role;

    @NotNull(message = "account cannot be null")
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    private Account account;

    @NotNull(message = "user cannot be null")
    @NotNull
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    private User user;

    @NotNull(message = "timestamp cannot be null")
    @NotNull
    private Timestamp createdOn;

    public AccountUserRole() {
        this.createdOn = Timestamp.from(Instant.now());
    }

    public AccountUserRole(Account account, User user, Role role) {
        this();
        this.account = account;
        this.user = user;
        this.role = role;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountUserRole that = (AccountUserRole) o;
        return Objects.equals(id, that.id) &&
                role == that.role &&
                Objects.equals(account, that.account) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, account, user);
    }
}
