package com.example.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
@ComponentScan(basePackages = {"com.example.accounting"})
public class AccountingApplication {
	public static void main(String[] args) {
		final SpringApplication springApplication = new SpringApplication(AccountingApplication.class);
		springApplication.run(args);
	}
}
