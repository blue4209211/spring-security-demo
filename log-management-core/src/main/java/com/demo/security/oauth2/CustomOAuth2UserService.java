package com.demo.security.oauth2;

import com.demo.exception.OAuth2AuthenticationProcessingException;
import com.demo.repository.*;
import com.demo.repository.model.*;
import com.demo.security.AuthProviderEnum;
import com.demo.security.ModelStatusEnum;
import com.demo.security.UserPrincipal;
import com.demo.security.oauth2.user.OAuth2UserInfo;
import com.demo.security.oauth2.user.OAuth2UserInfoFactory;
import com.demo.util.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountUserRoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserAttrRepository userAttrRepository;

    @Autowired
    private AuditEventLogRepository auditEventLogRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByUserId(oAuth2UserInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            LOGGER.info("user is already present and registered, returning same");
            user = userOptional.get();
            if (!user.getProvider().equals(AuthProviderEnum.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            LOGGER.info("user is not present creating new user");
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        LOGGER.info("returning new user principal after OAuth login");
        try {
            return UserPrincipal.create(user, oAuth2User.getAttributes());
        } catch (Exception e) {
            LOGGER.error("unable to create user principal", e);
            throw e;
        }

    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User(oAuth2UserInfo.getEmail());
        user.setProvider(AuthProviderEnum.google);
        user.setProviderId(oAuth2UserInfo.getId());

        final List<UserAttr> userAttrs = new ArrayList<>();
        userAttrs.add(new UserAttr(UserAttr.USER_EMAIL, oAuth2UserInfo.getEmail(), user));
        userAttrs.add(new UserAttr(UserAttr.USER_NAME, oAuth2UserInfo.getName(), user));
        userAttrs.add(new UserAttr(UserAttr.USER_PROFILE_URL, oAuth2UserInfo.getImageUrl(), user));
        user.setUserAttrs(userAttrs);
        user = userRepository.save(user);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("id", user.getId());
        data.put("source", "oauth2");
        try {
            auditEventLogRepository.save(new AuditEventLog("AUTH_USER_CREATE", SerializationUtils.toJSONString(data)));
        } catch (Exception e) {
            LOGGER.error("unable to store audit events", e);
        }
        LOGGER.info("created new user using Oauth attributes");
        return user;
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        final List<UserAttr> userAttrs = new ArrayList<>();
        List<UserAttr> attrs = userAttrRepository.findByNameIgnoreCase_AndUserId(UserAttr.USER_NAME, existingUser.getId());
        if (attrs.isEmpty()) {
            UserAttr ua = new UserAttr(UserAttr.USER_NAME, oAuth2UserInfo.getName(), existingUser);
            userAttrRepository.save(ua);
        } else {
            UserAttr ua = attrs.get(0);
            ua.setValue(oAuth2UserInfo.getName());
            userAttrRepository.save(ua);
        }

        attrs = userAttrRepository.findByNameIgnoreCase_AndUserId(UserAttr.USER_PROFILE_URL, existingUser.getId());
        if (attrs.isEmpty()) {
            UserAttr ua = new UserAttr(UserAttr.USER_PROFILE_URL, oAuth2UserInfo.getImageUrl(), existingUser);
            userAttrRepository.save(ua);
        } else {
            UserAttr ua = attrs.get(0);
            ua.setValue(oAuth2UserInfo.getImageUrl());
            userAttrRepository.save(ua);
        }
        existingUser.setStatus(ModelStatusEnum.ACTIVE);
        existingUser.setProvider(AuthProviderEnum.google);
        existingUser = userRepository.save(existingUser);

        return existingUser;
    }

    private String getDomain(String email) {
        String temp = email.substring(email.indexOf("@") + 1);
        return temp.substring(0, temp.indexOf(".")); // e.g. google
    }
}
