package com.example.model;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
@Scope("prototype")
public class Saludo {
	private String message;
	
    public Saludo(String saludoMessage) {
		this.message = saludoMessage;
        System.out.println("Nuevo bean Saludo creado, soy " + this);
	}
    
    @PostConstruct
    public void init() {
        System.err.println("Inicializando Saludo %s...".formatted(this));
    }

    @PreDestroy
    public void destroy() {
        System.err.println("\nDestruyendo Saludo %s...".formatted(this));
    }

    public String obtenerMensaje() {
        return message;
    }
}

// version: Paso 7
//
//@Component
//public class Saludo {
//    @PostConstruct
//    public void init() {
//        System.out.println("Inicializando Saludo...");
//    }
//
//    @PreDestroy
//    public void destroy() {
//        System.out.println("Destruyendo Saludo...");
//    }
//
//    public String obtenerMensaje() {
//        return "¡Bean Saludo completamente operativo!";
//    }
//}

// version: Paso 3
//
//public class Saludo {
//    public String obtenerMensaje() {
//        return "Hola desde Spring IoC con Spring Boot!";
//    }
//}