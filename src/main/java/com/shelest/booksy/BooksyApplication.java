package com.shelest.booksy;

import com.shelest.booksy.service.config.LoyaltyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(value = LoyaltyConfig.class)
public class BooksyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BooksyApplication.class, args);
	}

}
