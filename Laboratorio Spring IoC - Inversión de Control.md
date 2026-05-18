# Laboratorio Spring IoC: Inversión de Control

## Objetivo del laboratorio

Aprender cómo funciona Spring IoC y sus principales características dentro de un proyecto Spring Boot:

- Inyección de dependencias (DI)
- Tipos de inyección: por constructor, setter y campo
- Alcances de beans (Scopes)
- Ciclo de vida (@PostConstruct, @PreDestroy)
- Uso de @Qualifier, @Primary, @Lazy, y @Profile
- Configuración por Java (@Configuration, @Bean)
- Reemplazo de configuración XML por anotaciones

### Requisitos previos

- Java 17+
- Maven o Gradle
- IDE (IntelliJ, Eclipse, VS Code con soporte para Spring Boot)

## Paso 1. Crear el Proyecto Spring Boot

### Usando Spring Initializr

1. Abre [start.spring.io](https://start.spring.io/).
2. Configura:
    - Project: Maven Project
    - Language: Java
    - Spring Boot: 4.x.x o superior
    - Group: `com.example`
    - Artifact: `spring-ioc-lab`
    - Name: `spring-ioc-lab`
    - Description: `Laboratorio Spring IoC: Inversión de Control`
    - Package name: `com.example`
3. Añade las dependencias:
    - Spring Boot DevTools (spring-boot-devtools)
4. Descarga, descomprime e importa el proyecto.

### Crear paquetes

- com.example.config
- com.example.model
- com.example.service

### Usar YAML en las propiedades

1. Sobre `src/main/resources/application.properties`, clic con botón derecho, opción **Spring**, clic en **Convert .properties to .yaml**. 
2. Borra el anterior `src/main/resources/application.properties`

### Estructura base

    spring-ioc-lab/
    ├─ src/main/java/com/example/
    │   ├─ SpringIocLabApplication.java
    │   ├─ config/
    │   ├─ model/
    │   └─ service/
    └─ src/main/resources/
        └─ application.yml

## Paso 2: Clase principal

`SpringIocLabApplication.java`

```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication  // incluye @Configuration, @EnableAutoConfiguration, @ComponentScan
public class SpringIocLabApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(SpringIocLabApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Laboratorio Spring IoC: Inversión de Control ---");

    }
}
```

## Paso 3: Crear un Bean simple

`model/Saludo.java`

```java
package com.example.model;

import org.springframework.stereotype.Component;

@Component
public class Saludo {
    public String obtenerMensaje() {
        return "Hola desde Spring IoC con Spring Boot!";
    }
}
```

## Paso 4: Inyección de dependencias (DI)

### Inyección por constructor

`service/PersonaService.java`

```java
package com.example.service;

import com.example.model.Saludo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonaService {

    private final Saludo saludo;

    // Inyección por constructor (recomendada)
    @Autowired
    public PersonaService(Saludo saludo) {
        this.saludo = saludo;
    }

    public void decirHola() {
        System.out.println(saludo.obtenerMensaje());
    }
}
```

### Inyección por setter

`service/SetterService.java`

```java
package com.example.service;

import com.example.model.Saludo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetterService {

    private Saludo saludo;

    @Autowired
    public void setSaludo(Saludo saludo) {
        this.saludo = saludo;
    }

    public void saludar() {
        System.out.println("SetterService - saludo: " + saludo.obtenerMensaje());
    }
}
```

### Inyección por campo (atributo)

`service/FieldInjectionService.java`

```java
package com.example.service;

import com.example.model.Saludo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FieldInjectionService {

    @Autowired
    private Saludo saludo;

    public void saludar() {
        System.out.println("FieldInjectionService - saludo: " + saludo.obtenerMensaje());
    }
}
```

## Paso 5: Usar los bean en el arranque

En la clase principal:

`SpringIocLabApplication.java`

```java
package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.service.FieldInjectionService;
import com.example.service.PersonaService;
import com.example.service.SetterService;

@SpringBootApplication
public class SpringIocLabApplication implements CommandLineRunner {

    private final PersonaService personaService;
    private final SetterService setterService;
    private final FieldInjectionService fieldService;

    public SpringIocLabApplication(PersonaService personaService,
                                   SetterService setterService,
                                   FieldInjectionService fieldService) {
        this.personaService = personaService;
        this.setterService = setterService;
        this.fieldService = fieldService;
    }

    @Override
    public void run(String... args) {
        System.out.println("--- Laboratorio Spring IoC: Inversión de Control ---");

        System.out.println("\n1) Inyección por constructor (PersonaService):");
        personaService.decirHola();

        System.out.println("\n2) Inyección por setter (SetterService):");
        setterService.saludar();

        System.out.println("\n3) Inyección por campo (FieldInjectionService):");
        fieldService.saludar();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringIocLabApplication.class, args);
    }
}
```

El resultado al ejecutar es:

    --- Laboratorio Spring IoC: Inversión de Control ---

    1) Inyección por constructor (PersonaService):
    Hola desde Spring IoC con Spring Boot!

    2) Inyección por setter (SetterService):
    SetterService - saludo: Hola desde Spring IoC con Spring Boot!

    3) Inyección por campo (FieldInjectionService):
    FieldInjectionService - saludo: Hola desde Spring IoC con Spring Boot!

## Paso 6: Configuración Java con @Configuration y @Bean

`model/Saludo.java`

```java
package com.example.model;

import org.springframework.stereotype.Component;

@Component
public class Saludo {
    private String message;
    
    public Saludo(String saludoMessage) {
        this.message = saludoMessage;
        System.out.println("Nuevo bean Saludo creado, soy " + this);
    }
    
    public String obtenerMensaje() {
        return message;
    }
}
```

`config/AppConfig.java`

```java
package com.example.config;

import com.example.model.Saludo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    String saludoMessage(@Value("${saludo.message:Hola desde Spring IoC con Spring Boot!}") String message) {
        return message;
    }
    
    @Bean
    Saludo saludoPersonalizado() {
        return new Saludo("Hola desde un Bean configurado manualmente!");
    }
}
```

Para usar este Bean, se puede inyectar por nombre:

`service/PersonaService.java`

```java
package com.example.service;

import com.example.model.Saludo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonaService {

    private final Saludo saludo;

    // Inyección por constructor (recomendada)
    @Autowired
    public PersonaService(Saludo saludoPersonalizado) {
        this.saludo = saludoPersonalizado;
    }

    public void decirHola() {
        System.out.println(saludo.obtenerMensaje());
    }
}
```

El resultado al ejecutar es:

    --- Laboratorio Spring IoC: Inversión de Control ---

    1) Inyección por constructor (PersonaService):
    Hola desde un Bean configurado manualmente!

    2) Inyección por setter (SetterService):
    SetterService - saludo: Hola desde Spring IoC con Spring Boot!

    3) Inyección por campo (FieldInjectionService):
    FieldInjectionService - saludo: Hola desde Spring IoC con Spring Boot!

## Paso 7: Ciclo de vida del beans

`model/Saludo.java`

```java
package com.example.model;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class Saludo {
    private String message;
    
    public Saludo(String saludoMessage) {
        this.message = saludoMessage;
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
```

El resultado al ejecutar es:

    Inicializando Saludo com.example.model.Saludo@2fefeed3...
    :
    FieldInjectionService - saludo: ¡Bean Saludo completamente operativo!
    :
    Destruyendo Saludo com.example.model.Saludo@2fefeed3...

## Paso 8: @Scope y @Lazy

### Sin estado

Por defecto, los beans son *singleton*. Si añadimos el ámbito prototype y modificamos el constructor:

`model/Saludo.java`

```java
import org.springframework.context.annotation.Scope;

@Component
@Scope("prototype")
public class Saludo {
    private String message;
    
    public Saludo(String saludoMessage) {
        this.message = saludoMessage;
        System.out.println("Nuevo bean Saludo creado, soy " + this);
    }
    
```

El resultado al ejecutar es:

    :
    Nuevo bean Saludo creado, soy com.example.model.Saludo@2fefeed3
    Inicializando Saludo com.example.model.Saludo@2fefeed3...
    Nuevo bean Saludo creado, soy com.example.model.Saludo@5ad91fce
    Inicializando Saludo com.example.model.Saludo@5ad91fce...
    :

El valor que muestra después de la @ representa a la referencia (hashcode), su valor concreto no es lo importante (lo puede cambiar el Garbage Collector), que coincidan o no si es relevante, dado que valores distintos representan instancias distintas. Cada vez que lo inyectes, Spring creará una nueva instancia.

### Con estado

Los beans *singleton* comparten su estado mientras que los *prototype* no.

`model/ContadorBean.java`

```java
package com.example.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
@Scope("prototype")
public class ContadorBean {

    private static int instances = 0;
    private final int id;
    private int counter = 0;

    public ContadorBean() {
        id = ++instances;
    }

    @PostConstruct
    public void init() {
        System.out.println("ContadorBean init, id=" + id);
    }

    public int getNext() { 
        return ++counter; 
    }
    
    @Override
    public String toString() {
        return "ContadorBean#" + id + " counter: " + counter;
    }
}
```

En la clase principal:

`SpringIocLabApplication.java`

```java
package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.model.ContadorBean;
import com.example.service.FieldInjectionService;
import com.example.service.PersonaService;
import com.example.service.SetterService;

@SpringBootApplication
public class SpringIocLabApplication implements CommandLineRunner {

    private final PersonaService personaService;
    private final SetterService setterService;
    private final FieldInjectionService fieldService;
    private final ApplicationContext ctx;

    public SpringIocLabApplication(PersonaService personaService,
                                   SetterService setterService,
                                   FieldInjectionService fieldService,
                                   ApplicationContext ctx) {
        this.personaService = personaService;
        this.setterService = setterService;
        this.fieldService = fieldService;
        this.ctx = ctx;
    }

    @Override
    public void run(String... args) {
        System.out.println("--- Laboratorio Spring IoC: Inversión de Control ---");

        System.out.println("\n1) Inyección por constructor (PersonaService):");
        personaService.decirHola();

        System.out.println("\n2) Inyección por setter (SetterService):");
        setterService.saludar();

        System.out.println("\n3) Inyección por campo (FieldInjectionService):");
        fieldService.saludar();
        
        var c1 = ctx.getBean(ContadorBean.class);
        var c2 = ctx.getBean(ContadorBean.class);
              System.out.println("\n4) Bean con scope %s:".formatted(c1 == c2 ? "singleton" : "prototype"));
        System.out.println("c1 = %d < %s".formatted(c1.getNext(), c1));
        System.out.println("c2 = %d < %s".formatted(c2.getNext(), c2));
        System.out.println("c1 = %d < %s".formatted(c1.getNext(), c1));
        System.out.println("c2 = %d < %s".formatted(c2.getNext(), c2));
        System.out.println("c1 = %d < %s".formatted(c1.getNext(), c1));
   }

    public static void main(String[] args) {
        SpringApplication.run(SpringIocLabApplication.class, args);
    }
}
```

El resultado al ejecutar es:

    :
    4) Bean con scope prototype:
    c1 = 1 < ContadorBean#1 counter: 1
    c2 = 1 < ContadorBean#2 counter: 1
    c1 = 2 < ContadorBean#1 counter: 2
    c2 = 2 < ContadorBean#2 counter: 2
    c1 = 3 < ContadorBean#1 counter: 3
    :
Y si comentamos el scope (por defecto es *singleton*):

`model/ContadorBean.java`

```java
@Component
//@Scope("prototype")
public class ContadorBean {
```

El resultado al ejecutar es:

    :
    4) Bean con scope singleton:
    c1 = 1 < ContadorBean#1 counter: 1
    c2 = 2 < ContadorBean#1 counter: 2
    c1 = 3 < ContadorBean#1 counter: 3
    c2 = 4 < ContadorBean#1 counter: 4
    c1 = 5 < ContadorBean#1 counter: 5
    :

### @Lazy

Con @Lazy, el bean *singleton* se crea solo cuando se inyecta la primera vez:

`model/SaludoLento.java`

```java
package com.example.model;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component("saludoLento")
@Lazy
public class SaludoLento {
    private final long init;
    public SaludoLento() {
        init = System.nanoTime();
        System.out.println("SaludoLento constructor called");
    }

    @PostConstruct
    public void init() throws InterruptedException {
        System.out.println("SaludoLento @PostConstruct - inicializando");
        Thread.sleep(5000);
        System.out.println("SaludoLento @PostConstruct - inicializado");
        System.out.println("SaludoLento ha tardado %f ms en construirse.".formatted((System.nanoTime() - init)/1_000_000.0));
        System.out.println("Hoooooooooolllllaaaaaa!");
    }
}
```

En la clase principal:

`SpringIocLabApplication.java`

```java
    @Override
    public void run(String... args) {
        System.out.println("--- Laboratorio Spring IoC: Inversión de Control ---");
        // ...

        System.out.println("\n5) @Lazy Bean (no inicializado hasta uso):");
        System.out.println("Pidiendo lazyBean...");
        Object lazy = ctx.getBean("saludoLento");
        System.out.println("lazyBean obtenido: " + lazy.getClass().getSimpleName());
   }
```

El resultado al ejecutar es:

    :
    5) Bean @Lazy demo (no inicializado hasta uso):
    Pidiendo lazyBean...
    SaludoLento constructor called
    SaludoLento @PostConstruct - inicializando
    😴
    SaludoLento @PostConstruct - inicializado
    SaludoLento ha tardado 5000,714600 ms en construirse.
    Hoooooooooolllllaaaaaa!
    lazyBean obtenido: SaludoLento
    :

## Paso 9: @Primary, @Qualifier y @Profile

### @Primary

Varias implementaciones del mismo tipo:

`model/Saludar.java`

```java
package com.example.model;

public interface Saludar {
    String obtenerMensaje();
}
```

`model/SaludoFormal.java`

```java
package com.example.model;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component("formalSaludo")
class SaludoFormal implements Saludar {
    public String obtenerMensaje() { return "Saludos, estimado usuario."; }
}
```

`model/SaludoInformal.java`

```java
package com.example.model;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

//@Primary
@Component("informalSaludo")
class SaludoInformal implements Saludar {
    public String obtenerMensaje() { return "¡Hola, usuario!"; }
}
```

En el servicio:

`service/PersonaService.java`

```java
package com.example.service;

import org.springframework.stereotype.Service;

import com.example.model.Saludar;

@Service
public class PersonaService {

    private final Saludar saludo;

    public PersonaService(Saludar saludo) {
        this.saludo = saludo;
    }

    public void decirHola() {
        System.out.println(saludo.obtenerMensaje());
    }
}
```

En la clase principal:

`SpringIocLabApplication.java`

```java
    @Override
    public void run(String... args) {
        System.out.println("--- Laboratorio Spring IoC: Inversión de Control ---");
        // ...

        System.out.println("\n6) Uso de @Primary y @Qualifier:");
        personaService.decirHola();
   }
```

El resultado al ejecutar es:

    :
    6) Uso de @Primary y @Qualifier:
    Saludos, estimado usuario.
    :

Si cambiamos el primario:

`model/SaludoFormal.java`

```java
//@Primary
@Component("formalSaludo")
class SaludoFormal implements Saludar {
```

`model/SaludoInformal.java`

```java
@Primary
@Component("informalSaludo")
class SaludoInformal implements Saludar {
```

El resultado al ejecutar es:

    :
    6) Uso de @Primary y @Qualifier:
    ¡Hola, usuario!
    :

### @Qualifier

Para usar el Bean cualificado:

`config/AppConfig.java`

```java
package com.example.config;

import com.example.model.Saludar;
import com.example.model.Saludo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    String saludoMessage(@Value("${saludo.message:Hola desde Spring IoC con Spring Boot!}") String message) {
        return message;
    }
    
    @Bean
    Saludo saludoPersonalizado() {
        return new Saludo("Hola desde un Bean configurado manualmente!");
    }

    @Bean
    @Qualifier("saludoGenerico")
    Saludar saludoGenerico() {
        return new Saludar() {
            @Override
            public String obtenerMensaje() {
                return "Hola mundo!";
            }
        };
    }
}
```

`service/PersonaService.java`

```java
package com.example.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.model.Saludar;

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
```

En la clase principal:

`SpringIocLabApplication.java`

```java
    @Override
    public void run(String... args) {
        System.out.println("--- Laboratorio Spring IoC: Inversión de Control ---");
        // ...

        System.out.println("\n6) Uso de @Primary y @Qualifier:");
        personaService.decirHola();
        personaService.mostrarMensajeCalificado();
   }
```

Si SaludoFormal es el @Primary, el resultado al ejecutar es:

    :
    6) Uso de @Primary y @Qualifier:
    Saludos, estimado usuario.
    Hola mundo!
    :

Si SaludoInformal es el @Primary, el resultado al ejecutar es:

    :
    6) Uso de @Primary y @Qualifier:
    ¡Hola, usuario!
    Hola mundo!
    :

### @Profile

`model/SaludoDev.java`

```java
@Component("profileSaludo")
@Profile("dev")
public class SaludoDev implements Saludar {
    public String obtenerMensaje() { return "Hola desarrollador!"; }
}
```

`model/SaludoProd.java`

```java
@Component("profileSaludo")
@Profile("prod")
public class SaludoProd implements Saludar {
    public String obtenerMensaje() { return "Bienvenido al sistema en producción."; }
}
```

En la clase principal:

`SpringIocLabApplication.java`

```java
    @Override
    public void run(String... args) {
        System.out.println("--- Laboratorio Spring IoC: Inversión de Control ---");
        // ...

        System.out.println("\n7) Beans por perfil activo (consulta):");
        String[] profiles = ctx.getEnvironment().getActiveProfiles();
        System.out.println("Active profiles: " + java.util.Arrays.toString(profiles));
        if (ctx.containsBean("profileSaludo")) {
            var bean = (Saludar) ctx.getBean("profileSaludo");
            System.out.println("Bean 'profileSaludo' presente: " + bean.getClass().getSimpleName());
            System.out.println(bean.obtenerMensaje());
        } else {
            System.out.println("Bean 'profileSaludo' no definido para el perfil activo.");
        }
        // Cargar perfil 'prod'
        var prod = new AnnotationConfigApplicationContext();
        prod.getEnvironment().setActiveProfiles("prod");
        prod.scan("com.example.model");
        prod.refresh();
        var bean = (Saludar) prod.getBean("profileSaludo");
        System.out.println("Bean 'profileSaludo' cargado: " + bean.getClass().getSimpleName());
        System.out.println(bean.obtenerMensaje());
   }
```

El resultado al ejecutar es:

    :
    7) Beans por perfil activo (consulta):
    Active profiles: []
    Bean 'profileSaludo' no definido para el perfil activo.
    Bean 'profileSaludo' cargado: SaludoProd
    Bienvenido al sistema (perfil prod)
    :

Activar perfil `dev` en application.yml

`src/main/resources/application.yml`

```yml
spring:
  application:
    name: spring-ioc-lab
  profiles:
    active: dev
```

El resultado al ejecutar es:

    :
    7) Beans por perfil activo (consulta):
    Active profiles: [dev]
    Bean 'profileSaludo' presente: SaludoDev
    Hola desarrollador (perfil dev)
    Bean 'profileSaludo' cargado: SaludoProd
    Bienvenido al sistema (perfil prod)
    :

Activar perfil `prod` en application.yml

`src/main/resources/application.yml`

```yml
spring:
  application:
    name: spring-ioc-lab
  profiles:
    active: pred
```

El resultado al ejecutar es:

    :
    7) Beans por perfil activo (consulta):
    Active profiles: [prod]
    Bean 'profileSaludo' presente: SaludoProd
    Bienvenido al sistema (perfil prod)
    Bean 'profileSaludo' cargado: SaludoProd
    Bienvenido al sistema (perfil prod)
    :

## Estructura final

    spring-ioc-lab/
    ├─ pom.xml
    ├─ src/main/java/com/example/
    │   ├─ SpringIocLabApplication.java        ← Clase principal
    │   ├─ config/AppConfig.java               ← Beans configurados manualmente
    │   ├─ model/
    │   │   ├─ ContadorBean.java               ← Bean @Scope("prototype")
    │   │   ├─ Saludo.java                     ← Bean simple
    │   │   ├─ Saludar.java                    ← Interfaz
    │   │   ├─ SaludoLento.java                ← Lazy Bean
    │   │   ├─ SaludoFormal.java               ← Bean formal
    │   │   ├─ SaludoInformal.java             ← Bean informal (primary)
    │   │   ├─ SaludoDev.java                  ← Bean con @Profile("dev")
    │   │   └─ SaludoProd.java                 ← Bean con @Profile("prod")
    │   └─ service/PersonaService.java         ← Servicio que usa DI
    └─ src/main/resources/application.yml      ← Configuración de perfil activo

## Resumen de conceptos

| Concepto | Implementación | Ejemplo |
| --- | --- | --- |
| Bean | @Component, @Bean | Saludo |
| DI | @Autowired, constructor | PersonaService |
| Scopes | @Scope("prototype") | Saludo, ContadorBean |
| Lazy loading | @Lazy | SaludoLento |
| Ciclo de vida | @PostConstruct, @PreDestroy | Saludo |
| Calificadores | @Qualifier, @Primary | SaludoFormal vs SaludoInformal |
| Perfiles | @Profile("dev") | SaludoDev, SaludoProd |

---
© JMA 2024
