package com.laundry.LaundryManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LaundaryManagementApplication {

	public static void main(String[] args) {

		SpringApplication.run(LaundaryManagementApplication.class, args);
	}

}
