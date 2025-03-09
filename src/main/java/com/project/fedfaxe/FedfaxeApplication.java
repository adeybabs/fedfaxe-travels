package com.project.fedfaxe;

import com.project.fedfaxe.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FedfaxeApplication {

	public static void main(String[] args) {
		System.setProperty("MONGO_URI", EnvConfig.get("MONGO_URI"));
		System.setProperty("JWT_SECRET", EnvConfig.get("JWT_SECRET"));
		System.setProperty("GOOGLE_CLIENT_ID", EnvConfig.get("GOOGLE_CLIENT_ID"));
		System.setProperty("GOOGLE_CLIENT_SECRET", EnvConfig.get("GOOGLE_CLIENT_SECRET"));
		System.setProperty("AMADEUS_API_KEY", EnvConfig.get("AMADEUS_API_KEY"));
		System.setProperty("AMADEUS_API_SECRET", EnvConfig.get("AMADEUS_API_SECRET"));

		SpringApplication.run(FedfaxeApplication.class, args);
	}

}
