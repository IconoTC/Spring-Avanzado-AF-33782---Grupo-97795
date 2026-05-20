package com.example;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.simple.JdbcClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SpringBootApplication
public class DemoJdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoJdbcApplication.class, args);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class Actor {
		private int actorId;
		private String firstName;
		private String lastName;
		private LocalDateTime lastUpdate;
	}

	@Bean
	CommandLineRunner consultas(JdbcClient jdbcClient) {
		return arg -> {
			IO.println(jdbcClient.sql("select count(1) from actor").query(Integer.class).single());

			var actor = jdbcClient.sql("select * from actor where actor_id = ?").param(1).query(Actor.class)
					.optional();
			if (actor.isPresent())
				IO.println(actor.get());
			else {
				System.err.println("No encontrado");
			}
		};
	}

}
