package com.demo.repository;

import com.demo.repository.model.Account;
import com.demo.repository.model.AuditEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditEventLogRepository extends JpaRepository<AuditEventLog, String> {
    List<AuditEventLog> findByAccountIdOrderByTimestampDesc(long accountId);

    List<AuditEventLog> findByUserIdOrderByTimestampDesc(long userId);

    List<AuditEventLog> findByAccountIdAndUserIdOrderByTimestampDesc(long accountId, long userId);

    List<AuditEventLog> findByAccountIdAndUserIdAndEventNameIgnoreCaseOrderByTimestampDesc(long accountId, long userId, String eventName);
}
