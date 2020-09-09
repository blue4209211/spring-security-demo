package com.demo.controller;

import com.demo.controller.payload.UserDescribeResponse;
import com.demo.repository.AuditEventLogRepository;
import com.demo.repository.UserRepository;
import com.demo.repository.model.AuditEventLog;
import com.demo.security.CurrentUser;
import com.demo.security.IUserPrincipal;
import com.demo.util.AuthorizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@Transactional
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorizationUtils authorizationUtils;

    @Autowired
    private AuditEventLogRepository auditEventLogRepository;

    @GetMapping("/me")
    public UserDescribeResponse getCurrentUser(@CurrentUser IUserPrincipal userPrincipal) {
        return new UserDescribeResponse(userRepository.getOne(userPrincipal.getId()));
    }

    @GetMapping("/me/events")
    public List<AuditEventLog> getCurrentUserEvents(@CurrentUser IUserPrincipal userPrincipal) {
        return auditEventLogRepository.findByUserIdOrderByTimestampDesc(userPrincipal.getId());
    }

}
