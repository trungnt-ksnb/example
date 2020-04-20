package com.almighty.dbc.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class SourceDAO {
	private Log LOGGER = LogFactory.getLog(SourceDAO.class);
	@Autowired
	@Qualifier("jdbcTemplateSource")
	private JdbcTemplate jdbcTemplate;

	@Transactional(readOnly = true)
	public Object findUser() {
		String sql = "select * from user_";
		LOGGER.info("SourceDAO:findUser " + sql);
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		if (rows != null) {
			rows.forEach((row) -> {
				row.forEach((k, v) -> System.out.println("col : " + k + " value : " + v));
			});
		}
		return rows;
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> execute(String sql) {
		LOGGER.info("SourceDAO:execute " + sql);
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

		return rows;
	}

	public long getTotal(String sql) {
		LOGGER.info("SourceDAO:getTotal " + sql);
		/*List<Object> counters = jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper<Object>(Object.class));
		if (counters != null && !counters.isEmpty()) {
			return (Long)counters.get(0);
		}*/

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

		if (rows != null && !rows.isEmpty()) {
			return (Long) rows.get(0).get("total");
		}

		return -1;
	}

}
