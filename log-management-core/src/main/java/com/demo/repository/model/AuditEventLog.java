package com.demo.repository.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "audit_event_logs",
        indexes = {
                @Index(name = "accountIdIdx", columnList = "account_id"),
                @Index(name = "userIdIdx", columnList = "user_id"),
                @Index(name = "eventNameIdx", columnList = "event_name")
        }

)
public class AuditEventLog {

    @Id
    private String id;

    @Column
    private Long timestamp;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "event_name")
    private String eventName;

    @Column
    private String details;

    public AuditEventLog() {
        id = UUID.randomUUID().toString();
        timestamp = System.currentTimeMillis();
    }

    public AuditEventLog(long accountId, long userId, String eventName, String details) {
        this();
        this.accountId = accountId;
        this.userId = userId;
        this.eventName = eventName;
        this.details = details;
    }

    public AuditEventLog(long userId, String eventName) {
        this();
        this.accountId = -1L;
        this.userId = userId;
        this.eventName = eventName;
        this.details = null;
    }

    public AuditEventLog(long userId, String eventName,String details) {
        this();
        this.accountId = -1L;
        this.userId = userId;
        this.eventName = eventName;
        this.details = details;
    }


    public AuditEventLog(String eventName, String details) {
        this();
        this.accountId = -1L;
        this.userId = -1L;
        this.eventName = eventName;
        this.details = details;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
