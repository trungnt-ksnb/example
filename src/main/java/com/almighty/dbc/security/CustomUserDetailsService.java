package com.almighty.dbc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almighty.dbc.model.User;
import com.almighty.dbc.service.impl.UserDetailServiceImpl;
import com.almighty.dbc.service.impl.UserServiceImpl;

/**
 * Created by rajeevkumarsingh on 02/08/17.
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	UserDetailServiceImpl userDetailServiceImpl;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		return userDetailServiceImpl.findUserDetailByUserName(username);
	}

	@Transactional
	public UserDetails loadUserById(Long userId) {
		User user = userServiceImpl.findByPrimaryKey(userId);

		return UserPrincipal.create(user);
	}
}