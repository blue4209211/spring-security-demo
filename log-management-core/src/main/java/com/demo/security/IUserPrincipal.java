package com.demo.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface IUserPrincipal extends OAuth2User, UserDetails{
	 public Long getId() ;
}
