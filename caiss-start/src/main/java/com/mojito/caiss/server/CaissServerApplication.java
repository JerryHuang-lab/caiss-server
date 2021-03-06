package com.mojito.caiss.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mojito.caiss","com.mojito.server"})
public class CaissServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaissServerApplication.class, args);
	}

}
