package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoDataApplication.class, args);
	}
	
	@Bean
	CommandLineRunner ejempos(EjemplosDatos demos) {
		return arg -> {
//			demos.consultas();
//			demos.actores();
//			demos.carga();
			demos.valida();
		};
	}


}
