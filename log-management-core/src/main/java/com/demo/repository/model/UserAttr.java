package com.demo.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "user_attr")
public class UserAttr {
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_EMAIL_VARIFIED = "USER_EMAIL_VARIFIED";
    public static final String USER_PROFILE_URL = "USER_PROFILE_URL";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @NotNull(message = "user cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull(message = "name cannot be null")
    @Column(nullable = false)
    private String name;

    @Column
    private String value;

    @NotNull(message = "createdOn cannot be null")
    @Column(nullable = false)
    private Timestamp createdOn;

    public UserAttr() {
        createdOn = Timestamp.from(Instant.now());
    }

    public UserAttr(String name, String value, User user) {
        this();
        this.name = name;
        this.value = value;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }
}
