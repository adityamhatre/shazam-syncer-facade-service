package com.adityamhatre.shazamfacade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.adityamhatre"})
public class ShazamfacadeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShazamfacadeApplication.class, args);
	}

}
