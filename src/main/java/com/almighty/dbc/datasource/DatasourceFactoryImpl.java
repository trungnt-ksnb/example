package com.almighty.dbc.datasource;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author trungnt
 *
 */
public class DatasourceFactoryImpl {
	public DataSourceBuilder<?> dataSourceBuilder;
	public DataSource dataSource;
	public DatasourceFactoryImpl(String driverClassName, String url, String username, String password) {
		dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(driverClassName);
		dataSourceBuilder.url(url);
		dataSourceBuilder.username(username);
		dataSourceBuilder.password(password);
		createDataSource();
	}
	
	public DataSource createDataSource() {
		dataSource = dataSourceBuilder.build();
		return dataSource;
	}
	
	public JdbcTemplate createJdbcTemplate() {
		if(dataSource != null) {
			return new JdbcTemplate(dataSource);
		}
		return new JdbcTemplate(createDataSource());
	}
}
