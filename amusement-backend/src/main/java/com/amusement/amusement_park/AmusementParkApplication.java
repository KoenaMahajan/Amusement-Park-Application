package com.amusement.amusement_park;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AmusementParkApplication {
	public static void main(String[] args) {
		SpringApplication.run(AmusementParkApplication.class, args);
	}

}
