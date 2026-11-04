package com.soskate.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class ApiApplication {

	static {
		// Lessons happen in Paris: "today" and stored times must not depend on the server zone
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
