package com.MiTi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MiTiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiTiApplication.class, args);
	}

}
