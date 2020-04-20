package com.almighty.dbc.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.almighty.dbc.dao.UserDAO;
import com.almighty.dbc.model.Role;
import com.almighty.dbc.model.User;
import com.almighty.dbc.service.UserService;

/**
 * @author trungnt
 *
 */
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDAO userDAO;

	@Autowired
	private RoleServiceImpl roleServiceImpl;

	@Override
	public boolean addUser(User user) {

		List<String> sqls = new ArrayList<>();

		String sql = "INSERT INTO dbc_user(" + "user_name," + "full_name," + "password," + "passwordencrypted,"
				+ "email," + "address," + "telno," + "imageurl," + "status)" + "VALUES(";
		sql += "'" + user.getUserName() + "',";
		sql += "'" + user.getFullName() + "',";
		sql += "'" + user.getPassword() + "',";
		sql += "'" + user.getPasswordEncrypted() + "',";
		sql += "'" + user.getEmail() + "',";
		sql += "'" + user.getAddress() + "',";
		sql += "'" + user.getTelNo() + "',";
		sql += "'" + user.getImageURL() + "',";
		sql += "'" + user.getStatus() + "');\n";
		sqls.add(sql);
		// boolean result = userDAO.exscute(sql);
		// user = findByUserName(user.getUserName());
		if (user.getRoles() != null) {

			for (Role role : user.getRoles()) {

				sql = "INSERT INTO dbc_user_role(user_id, role_id) VALUES("
						+ "(SELECT user_id FROM dbc_user WHERE user_name = " + "'" + user.getUserName() + "'" + "),"
						+ "'" + role.getRoleId() + "');\n";
				sqls.add(sql);
			}

		}

		return userDAO.execute(sqls.stream().toArray(String[]::new));
	}

	@Override
	public User findByUserName(String userName) {
		String sql = "SELECT * FROM dbc_user WHERE user_name = ?";
		User user = userDAO.findByUserName(sql, userName);
		if (user != null) {
			List<Role> roles = roleServiceImpl.findRolesByUserId(user.getUserId());
			if (roles != null) {
				user.setRoles(new HashSet<>(roles));
			}
		}

		return user;
	}

	@Override
	public List<User> getUsers(int start, int end) {
		String sql = "SELECT * FROM dbc_user";
		if (start >= 0 && end > start) {
			sql += " LIMIT " + start + "," + end;
		}

		List<User> users = userDAO.getUsers(sql);

		if (users != null) {
			for (User user : users) {
				List<Role> roles = roleServiceImpl.findRolesByUserId(user.getUserId());
				if (roles != null) {
					user.setRoles(new HashSet<>(roles));
				}

			}
		}

		return users;
	}

	@Override
	public boolean deleteUser(long userId) {
		String sql1 = "DELETE FROM dbc_user WHERE user_id = " + userId + ";";
		String sql2 = "DELETE FROM dbc_user_role WHERE user_id = " + userId + ";";
		String[] sqls = new String[] { sql1, sql2 };
		return userDAO.execute(sqls);
	}

	@Override
	public boolean updateUser(User user) {
		String sql = "UPDATE dbc_user SET " + "user_name = '" + user.getUserName() + "'," + "full_name = '"
				+ user.getFullName() + "'," + "password = '" + user.getPassword() + "'," + "passwordencrypted = '"
				+ user.getPasswordEncrypted() + "'," + "email = '" + user.getEmail() + "'," + "address = '"
				+ user.getAddress() + "'," + "telno '" + user.getTelNo() + "'," + "imageurl '" + user.getImageURL()
				+ "'," + "status = " + user.getStatus() + "WHERE user_id = " + user.getUserId() + ";";

		return userDAO.execute(sql);
	}

	@Override
	public User findByPrimaryKey(long userId) {
		String sql = "SELECT * FROM dbc_user WHERE user_id = ?;";

		User user = userDAO.findByPrimaryKey(sql, userId);
		if (user != null) {
			List<Role> roles = roleServiceImpl.findRolesByUserId(user.getUserId());
			if (roles != null) {
				user.setRoles(new HashSet<>(roles));
			}
		}

		return user;
	}

}
