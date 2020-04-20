package com.almighty.dbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EntityScan(basePackages = "com.almighty.dbc.model")
public class DbconvertApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbconvertApplication.class, args);
	}

}
