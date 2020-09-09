package com.demo.controller;

import com.demo.controller.payload.*;
import com.demo.exception.BadRequestException;
import com.demo.repository.AccountUserRoleRepository;
import com.demo.repository.AuditEventLogRepository;
import com.demo.repository.UserAttrRepository;
import com.demo.repository.UserRepository;
import com.demo.repository.model.*;
import com.demo.security.AuthProviderEnum;
import com.demo.security.IUserPrincipal;
import com.demo.security.ModelStatusEnum;
import com.demo.security.TokenProvider;
import com.demo.util.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/auth")
@Transactional
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountUserRoleRepository roleRepository;

    @Autowired
    private UserAttrRepository userAttrRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private AuditEventLogRepository auditEventLogRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication);
        IUserPrincipal userPrincipal = (IUserPrincipal) authentication.getPrincipal();
        auditEventLogRepository.save(new AuditEventLog(userPrincipal.getId(), "AUTH_USER"));

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AgentLoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getClientId(), loginRequest.getSecret()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication);

        IUserPrincipal userPrincipal = (IUserPrincipal) authentication.getPrincipal();
        auditEventLogRepository.save(new AuditEventLog(userPrincipal.getId(), "AUTH_TOKEN"));

        return ResponseEntity.ok(new AuthResponse(token));
    }

    //TODO Email Verification
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateRequest createUserRequest) {
        if (userRepository.existsByUserId(createUserRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        Optional<User> userOptional = userRepository.findByUserId(createUserRequest.getEmail());
        User user = null;
        if (userOptional.isPresent()) {
            if (userOptional.get().getStatus() == ModelStatusEnum.ACTIVE) {
                throw new BadRequestException("user already exists");
            }
            user = userOptional.get();
            user.setStatus(ModelStatusEnum.ACTIVE);
            user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
            user.setProvider(AuthProviderEnum.local);
            user.getUserAttrs().add((new UserAttr(UserAttr.USER_NAME, createUserRequest.getName(), user)));
        } else {
            // Creating user's account
            user = new User(createUserRequest.getEmail(), passwordEncoder.encode(createUserRequest.getPassword()));
            final List<UserAttr> userAttrs = new ArrayList<>();
            userAttrs.add(new UserAttr(UserAttr.USER_EMAIL, createUserRequest.getEmail(), user));
            userAttrs.add(new UserAttr(UserAttr.USER_NAME, createUserRequest.getName(), user));
            user.setUserAttrs(userAttrs);
        }
        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/me")
                .buildAndExpand(result.getId()).toUri();

        Map<String, String> data = new HashMap<>();
        data.put("userId", createUserRequest.getEmail());
        try {
            auditEventLogRepository.save(new AuditEventLog("AUTH_USER_CREATE", SerializationUtils.toJSONString(data)));
        } catch (Exception e) {
            LOGGER.error("unable to store audit events", e);
        }

        return ResponseEntity.created(location).body(new UserCreateResponse(result.getId()));
    }

}
