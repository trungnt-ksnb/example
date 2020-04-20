package com.almighty.dbc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.almighty.dbc.model.User;

/**
 * @author trungnt
 *
 */
@Service
public interface UserService {

	public boolean addUser(User user);

	public boolean deleteUser(long userId);

	public User findByPrimaryKey(long userId);

	public User findByUserName(String userName);

	public List<User> getUsers(int start, int end);

	public boolean updateUser(User user);

}
