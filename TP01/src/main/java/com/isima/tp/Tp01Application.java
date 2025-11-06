package com.isima.tp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Tp01Application {

	public static void main(String[] args) {
		SpringApplication.run(Tp01Application.class, args);
	}

}
