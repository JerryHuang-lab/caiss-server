package com.mojito.caiss.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"com.mojito.caiss","com.mojito.server"})
public class CaissServerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CaissServerApplication.class, args);
	}


	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(CaissServerApplication.class);
	}

}
