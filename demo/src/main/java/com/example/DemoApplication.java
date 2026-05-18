package com.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.ioc.NotificationService;
import com.example.ioc.NotificationServiceImpl;
import com.example.ioc.Rango;
import com.example.ioc.anotaciones.EMail;
import com.example.ioc.contratos.ServicioCadenas;
import com.example.ioc.implementaciones.ConfiguracionImpl;
import com.example.ioc.implementaciones.RepositorioCadenasImpl;
import com.example.ioc.implementaciones.ServicioCadenasImpl;
import com.example.ioc.notificaciones.Sender;
import com.example.nulabilidad.Dummy;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@Log4j2
public class DemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.err.println("Aplicacion arrancada...");
	}

	//@Bean
	CommandLineRunner tratamientoDeNulos() {
		return arg -> {
			var dummy = new Dummy();
			String cad = "";
			dummy.setCadena(cad);
			log.warn("Esto es un aviso");
		};
	}
	
	@Bean
	CommandLineRunner ioc(ServicioCadenas srv, NotificationService notify) {
		return arg -> {
//			NotificationService notify = new NotificationServiceImpl();
//			ServicioCadenas srv = new ServicioCadenasImpl(new RepositorioCadenasImpl(new ConfiguracionImpl(notify), notify), notify);
			srv.add("algo");
			srv.get().forEach(notify::add);
			IO.println("===============================>");
			notify.getListado().forEach(IO::println);
			notify.clear();
			IO.println("<===============================");
			IO.println(srv.getClass().getCanonicalName());
		};
	}
//	@Bean
	CommandLineRunner porNombre(Sender twitterSender, Sender email, @EMail Sender sender) {
		return arg -> {
			twitterSender.send("mando un twitt");
			email.send("mando un correo");
			sender.send("mando un noseque");
		};
	}
	
//	@Bean
	CommandLineRunner porDondeSea(List<Sender> lista) {
		return arg -> {
			lista.forEach(item -> item.send("mensaje"));
		};
	}

	@Bean
	CommandLineRunner valores(@Value("${mi.valor:Sin valor}") String cad, Rango rango) {
		return arg -> {
			System.err.println(cad);
			System.err.println(rango.toString());
		};
	}
}
