package com.almighty.dbc.dao;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.almighty.dbc.model.User;

/**
 * @author trungnt
 *
 */
@Transactional
@Repository
public class UserDAO {
	@Autowired
	@Qualifier("jdbcTemplatePrimary")
	private JdbcTemplate jdbcTemplate;
	private Log LOGGER = LogFactory.getLog(UserDAO.class);

	public boolean execute(String[] sqls) {
		LOGGER.info("UserDAO:exsecute " + Arrays.toString(sqls));
		
		int[] results = jdbcTemplate.batchUpdate(sqls);
		
		// List<Integer> integers =
		// Arrays.stream(results).boxed().collect(Collectors.toList());
		if (results == null || results.length == 0) {
			return false;
		}

		if(results[0] == 1) {
			return true;
		}

		return false;
	}

	public boolean execute(String sql) {
		LOGGER.info("UserDAO:addUser " + sql);
		int result = jdbcTemplate.update(sql);
		return result == 1 ? true : false;
	}

	public User findByPrimaryKey(String sql, long userId) {
		LOGGER.info("UserDAO findByPrimaryKey " + sql + "|" + userId);
		// return jdbcTemplate.queryForObject(sql, new Object[] { userId }, User.class);

		List<User> users = jdbcTemplate.query(sql, new Object[] { userId },
				new BeanPropertyRowMapper<User>(User.class));
		if (users != null && !users.isEmpty()) {
			return users.get(0);
		}

		return null;
	}

	/**
	 * @param sql
	 * @param userName
	 * @return
	 */
	public User findByUserName(String sql, String userName) {
		LOGGER.info("UserDAO findByUserName " + sql + "|" + userName);
		// return jdbcTemplate.queryForObject(sql, new Object[] { userName },
		// User.class);
		List<User> users = jdbcTemplate.query(sql, new Object[] { userName },
				new BeanPropertyRowMapper<User>(User.class));
		if (users != null && !users.isEmpty()) {
			return users.get(0);
		}

		return null;
	}

	public List<User> getUsers(String sql) {
		List<User> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(User.class));
		return users;
	}

	public boolean updateUser(String sql) {
		return false;
	}
}
