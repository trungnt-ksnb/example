package com.almighty.dbc.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * @author trungnt
 *
 */
@Service
public interface UserDetailService {

	public UserDetails findUserDetailByUserName(String userName);

}
