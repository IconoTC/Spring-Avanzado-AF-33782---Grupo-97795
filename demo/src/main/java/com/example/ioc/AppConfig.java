package com.example.ioc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.ioc.anotaciones.Twit;
import com.example.ioc.notificaciones.EMailSender;
import com.example.ioc.notificaciones.Sender;
import com.example.ioc.notificaciones.TwitterSender;
import com.example.nulabilidad.Dummy;

@Configuration
public class AppConfig {
//	@Bean
//	ConstructorConValores miClase(NotificationService notify) {
//		return new ConstructorConValores(1, "yo", notify);
//	}
	@Bean
	@Qualifier("tweet")
//	@Twit
	Sender twitea() {
		return new TwitterSender();
	}
	
	@Bean
	@Qualifier("correo")
	Sender correo() {
		return new EMailSender();
	}
	@Bean
	int version() {
		return 66;
	}
	
	@Bean
	String otroAutor(@Value("${mi.nombre:Anonimo}") String nombre) {
		return nombre;
	}
	
	@Bean
	Dummy dummy() {
		return new Dummy();
	}
}
