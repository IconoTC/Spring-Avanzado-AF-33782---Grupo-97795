package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.model.Saludar;
import com.example.model.Saludo;

@Service
public class PersonaService {

    private final Saludar saludoDefault;
    private final Saludar saludoGenerico;

    public PersonaService(Saludar saludoDefault,  @Qualifier("saludoGenerico") Saludar otroSaludo) {
        this.saludoDefault = saludoDefault;
		this.saludoGenerico = otroSaludo;
    }

    public void decirHola() {
        System.out.println(saludoDefault.obtenerMensaje());
    }

    public void mostrarMensajeCalificado() {
        System.out.println(saludoGenerico.obtenerMensaje());
    }
}

//Version: Paso 9
//
//@Service
//public class PersonaService {
//
//    private final Saludar saludo;
//
//    public PersonaService(Saludar saludo) {
//        this.saludo = saludo;
//    }
//
//    public void decirHola() {
//        System.out.println(saludo.obtenerMensaje());
//    }
//}

//Version: Paso 6
//
//@Service
//public class PersonaService {
//
//    private final Saludo saludo;
//
//    // Inyección por constructor (recomendada)
//    @Autowired
//    public PersonaService(Saludo saludoPersonalizado) {
//        this.saludo = saludoPersonalizado;
//    }
//
//    public void decirHola() {
//        System.out.println(saludo.obtenerMensaje());
//    }
//}

// Version: Paso 4
//
//@Service
//public class PersonaService {
//
//    private final Saludo saludo;
//
//    // Inyección por constructor (recomendada)
//    @Autowired
//    public PersonaService(Saludo saludo) {
//        this.saludo = saludo;
//    }
//
//    public void decirHola() {
//        System.out.println(saludo.obtenerMensaje());
//    }
//}