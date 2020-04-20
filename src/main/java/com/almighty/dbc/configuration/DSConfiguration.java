package com.almighty.dbc.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author trungnt
 *
 */
@Configuration
public class DSConfiguration {
	
	@Bean(name = "primary")
	@ConfigurationProperties(prefix = "spring.datasource")
	@Primary
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name = "source")
	@ConfigurationProperties(prefix = "spring.source.datasource")
	public DataSource dataSource1() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "target")
	@ConfigurationProperties(prefix = "spring.target.datasource")
	public DataSource dataSource2() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name = "jdbcTemplatePrimary")
	@Primary
	public JdbcTemplate jdbcTemplate(@Qualifier("primary") DataSource ds) {
		return new JdbcTemplate(ds);
	}
	
	@Bean(name = "jdbcTemplateSource")
	public JdbcTemplate jdbcTemplate1(@Qualifier("source") DataSource ds) {
		return new JdbcTemplate(ds);
	}

	@Bean(name = "jdbcTemplateTarget")
	public JdbcTemplate jdbcTemplate2(@Qualifier("target") DataSource ds) {
		return new JdbcTemplate(ds);
	}
}
