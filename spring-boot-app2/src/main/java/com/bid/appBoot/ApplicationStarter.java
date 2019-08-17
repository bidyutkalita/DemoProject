package com.bid.appBoot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.*"})
@EntityScan("com.bid.*")
@EnableJpaRepositories("com.bid.*")
public class ApplicationStarter {
	
		public static void main(String[] args) {
			SpringApplication.run(ApplicationStarter.class, args);
		}
		
		

}
