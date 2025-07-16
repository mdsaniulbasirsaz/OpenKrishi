package com.openkrishi.OpenKrishi;

import com.openkrishi.OpenKrishi.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpenKrishiApplication {

	public static void main(String[] args) {
		EnvConfig.loadEnv();
		SpringApplication.run(OpenKrishiApplication.class, args);
	}

}
