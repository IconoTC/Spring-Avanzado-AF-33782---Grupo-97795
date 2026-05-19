package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy // Habilitar anotaciones AspectJ
@SpringBootApplication
public class SpringAopLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAopLabApplication.class, args);
	}

}
