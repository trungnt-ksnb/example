package com.almighty.dbc.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class UserRoleDAO {
	@Autowired
	@Qualifier("jdbcTemplatePrimary")
	private JdbcTemplate jdbcTemplate;
	// private Log LOGGER = LogFactory.getLog(UserRoleDAO.class);

	public List<Long> findLstRoleIdByUserId(String sql, long userId) {
		List<Long> roleIds = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Long>(long.class));
		return roleIds;
	}

	public boolean execute(String sql) {
		return jdbcTemplate.update(sql) == 1 ? true : false;
	}

}
