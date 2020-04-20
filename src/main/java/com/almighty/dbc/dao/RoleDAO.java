package com.almighty.dbc.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.almighty.dbc.model.Role;

@Transactional
@Repository
public class RoleDAO {
	@Autowired
	@Qualifier("jdbcTemplatePrimary")
	private JdbcTemplate jdbcTemplate;

	private Log LOGGER = LogFactory.getLog(RoleDAO.class);

	public Role findByPrimaryKey(String sql, long roleId) {
		LOGGER.info("RoleDAO findByPrimaryKey " + sql + "|" + roleId);
		// return jdbcTemplate.queryForObject(sql, new Object[] { roleId }, Role.class);
		List<Role> roles = jdbcTemplate.query(sql, new Object[] { roleId },
				new BeanPropertyRowMapper<Role>(Role.class));

		if (roles != null && !roles.isEmpty()) {
			return roles.get(0);
		}

		return null;
	}

	public Role findByUserName(String sql, String userName) {
		LOGGER.info("RoleDAO findByUserName " + sql + "|" + userName);
		// return jdbcTemplate.queryForObject(sql, new Object[] {userName}, Role.class);
		List<Role> roles = jdbcTemplate.query(sql, new Object[] { userName },
				new BeanPropertyRowMapper<Role>(Role.class));

		if (roles != null && !roles.isEmpty()) {
			return roles.get(0);
		}

		return null;
	}

	public List<Role> findAll() {
		return null;
	}

	public List<Role> findRolesByIds(String sql, String roleIds) {
		LOGGER.info("RoleDAO findRolesByIds " + sql + "|" + roleIds);
		return jdbcTemplate.query(sql, new Object[] { roleIds }, new BeanPropertyRowMapper<Role>(Role.class));
	}

	public List<Role> findRolesByUserId(String sql, long userId) {
		LOGGER.info("RoleDAO findRolesByUserId " + sql + "|" + userId);
		return jdbcTemplate.query(sql, new Object[] { userId }, new BeanPropertyRowMapper<Role>(Role.class));
	}
}
