package com.almighty.dbc.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almighty.dbc.model.Role;
import com.almighty.dbc.model.User;
import com.almighty.dbc.service.UserDetailService;

/**
 * @author trungnt
 *
 */
@Service
public class UserDetailServiceImpl implements UserDetailService {

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Override
	@Transactional(readOnly = true)
	public UserDetails findUserDetailByUserName(String userName) {

		User user = userServiceImpl.findByUserName(userName);

		if (user == null) {
			throw new UsernameNotFoundException(userName);
		}

		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

		if (user.getRoles() != null) {
			for (Role role : user.getRoles()) {

				grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
			}
		}

		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPasswordEncrypted(),
				grantedAuthorities);
	}

}
