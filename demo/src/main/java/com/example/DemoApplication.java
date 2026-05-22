package com.example;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.aop.AuthenticationService;
import com.example.aop.DummyAsync;
import com.example.aop.introductions.Visible;
import com.example.base.DummyJSpecify;
import com.example.base.DummyRetry;
import com.example.contracts.application.services.MessagingService;
import com.example.ioc.NotificationService;
import com.example.ioc.Rango;
import com.example.ioc.anotaciones.EMail;
import com.example.ioc.contratos.ServicioCadenas;
import com.example.ioc.notificaciones.Sender;

import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;

@Log4j2
@EnableAsync
@EnableScheduling
@EnableResilientMethods
@EnableAspectJAutoProxy
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.err.println("Aplicacion arrancada...");
	}

//	@Bean
	CommandLineRunner tratamientoDeNulos(DummyJSpecify dummy) {
		return arg -> {
//			var dummy = new Dummy();
			String cad = "";
//			dummy.setCadena(cad);
			try {
//			dummy.setCadenaSegura(null);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
			log.warn("Esto es un aviso");
		};
	}

	@Bean
	CommandLineRunner resiliencia(DummyRetry dummy) {
		return arg -> {
			try {
				IO.println("------------------> reintentaConAnotacion: " + dummy.reintentaConAnotacion(3));
				IO.println("------------------> reintentaConAnotacion: " + dummy.reintentaConAnotacion(5));
			} catch (Exception e) {
				System.err.println("ERROR reintentaConAnotacion: " + e.getMessage());
			}
//			dummy.reinicia();
			try {
				IO.println("------------------> reintentaConTemplate: " + dummy.reintentaConTemplate(3));
				IO.println("------------------> reintentaConTemplate: " + dummy.reintentaConTemplate(5));
			} catch (Exception e) {
				System.err.println("ERROR reintentaConTemplate: " + e.getMessage());
			}
		};
	}

//	@Bean
	CommandLineRunner ioc(ServicioCadenas srv, NotificationService notify, AuthenticationService auth) {
		return arg -> {
//			NotificationService notify = new NotificationServiceImpl();
//			ServicioCadenas srv = new ServicioCadenasImpl(new RepositorioCadenasImpl(new ConfiguracionImpl(notify), notify), notify);
			auth.login();
			srv.add("algo");
			srv.get().forEach(notify::add);
//			IO.println("===============================>");
//			notify.getListado().forEach(IO::println);
//			notify.clear();
//			IO.println("<===============================");
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

//	@Bean
	CommandLineRunner valores(@Value("${mi.valor:Sin valor}") String cad, Rango rango) {
		return arg -> {
			System.err.println(cad);
			System.err.println(rango.toString());
		};
	}

//	@Bean
	CommandLineRunner configuracionEnXML() {
		return _ -> {
			try (var contexto = new FileSystemXmlApplicationContext("applicationContext.xml")) {
				var notify = contexto.getBean(NotificationService.class);
				System.out.println("configuracionEnXML ===================>");
				var srv = (ServicioCadenas) contexto.getBean("servicioCadenas");
				System.out.println(srv.getClass().getName());
				contexto.getBean(NotificationService.class).getListado().forEach(System.out::println);
				System.out.println("===================>");
				srv.get().forEach(notify::add);
				srv.add("Hola mundo");
				notify.add(srv.get(1));
				srv.modify("modificado");
				System.out.println("===================>");
				notify.getListado().forEach(System.out::println);
				notify.clear();
				System.out.println("<===================");
				((Sender) contexto.getBean("sender")).send("Hola mundo");
			}
		};
	}

//	@Order(1)
//	@EventListener
//	private void suscriptor(GenericoEvent event) {
//		System.err.println("Evento generico de %s: %s".formatted(event.origen(), event.carga()));
//	}
//	@Order(10)
//	@EventListener
//	private void suscriptor2(GenericoEvent event) {
//		System.err.println("Otro tratamiento de %s: %s".formatted(event.origen(), event.carga()));
//	}
//	
//	@EventListener
//	private void eventHandler(String event) {
//		System.err.println("Evento cadena: %s".formatted(event));
//	}

//	@Bean
	CommandLineRunner introduciones(ServicioCadenas srv, NotificationService notify, AuthenticationService auth) {
		return arg -> {
			IO.println(srv.getClass().getCanonicalName());
			if (srv instanceof Visible v) {
				IO.println(v.isVisible() ? "es visible" : "es invisible");
				v.mostrar();
				IO.println(v.isVisible() ? "es visible" : "es invisible");
				v.ocultar();
				IO.println(v.isVisible() ? "es visible" : "es invisible");
			} else {
				IO.println("No implementa Visible");
			}
		};
	}

	@Autowired
	NotificationService notify;

//	@Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS, initialDelay = 5)
	void programacion() {
//		System.out.println("Han pasado 5 segundos.");
		if (notify.hasMessages()) {
			IO.println("===============================>");
			notify.getListado().forEach(IO::println);
			notify.clear();
			IO.println("<===============================");
		}
	}

//	@Bean
	CommandLineRunner asincrono(DummyAsync dummy) {
		return arg -> {
			var obj = dummy; // new DummyAsync();
			System.err.println(obj.getClass().getCanonicalName());
//			obj.ejecutarAutoInvocado(1);
//			obj.ejecutarAutoInvocado(2);
			obj.ejecutarTareaSimpleAsync(1);
			obj.ejecutarTareaSimpleAsync(2);
			obj.calcularResultadoAsync(10, 20, 30, 40, 50).thenAccept(result -> notify.add(result));
			obj.calcularResultadoAsync(1, 2, 3).thenAccept(result -> notify.add(result));
			obj.calcularResultadoAsync().thenAccept(result -> notify.add(result));
			System.err.println("Termino de mandar hacer las cosas");
		};
	}
	@Autowired
	MessagingService mensajeria;

//	@Bean
	CommandLineRunner demosCorreos() {
		return _ -> {
			mensajeria.sendEmailAsync("admin@example.com", "Aplicacion Init", "La aplicacion se ha iniciado");
			mensajeria.sendWelcomeEmailAsync("pgrillo@example.com", "Pepito Grillo");
			despidete();
		};
	}

//	@PreDestroy
	void despidete() {
		var body = """
				<!DOCTYPE html>
				<html lang="es">
				<head>
				    <meta charset="UTF-8">
				    <meta name="viewport" content="width=device-width, initial-scale=1.0">
				    <title>Servicio</title>
				</head>
				<body>
				    <h1>%s</h1>
				    <p>%s</p>
				</body>
				</html>
				""".formatted("Aplicacion Close", "La aplicacion se ha cerrado");
		mensajeria.sendMimeEmail("admin@example.com", "Aplicacion Close", body, true);
	}

}
