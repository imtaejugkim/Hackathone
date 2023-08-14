package com.konkuk.hackerthonrelay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HackerthonrelayApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackerthonrelayApplication.class, args);
	}

}
