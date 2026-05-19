# Laboratorio Spring AOP: Comprobación estricta de nulos

## Objetivo del laboratorio

Crear un aspecto con Spring  AOP que lance excepciones cuando:

- un método reciba un argumento con valor null (IllegalArgumentException)
- un método devuelva null (NoSuchElementException).

Este laboratorio te guiará paso a paso para crear, integrar y probar el aspecto `StrictNullChecksAspect` en una aplicación Spring Boot. Aprenderás a usar AOP (Programación Orientada a Aspectos) para validar argumentos y retornos nulos en los métodos de tu aplicación.

### Requisitos previos

- Java 17+ (o compatible con tu versión de Spring)
- Maven o Gradle
- IDE (Eclipse, IntelliJ, VS Code)

## Paso 1. Crear el Proyecto Spring Boot

### Usando Spring Initializr

1. Abre [start.spring.io](https://start.spring.io/).
2. Configura:
   - Project: Maven Project
   - Language: Java
   - Spring Boot: 3.3.x o superior
   - Group: `com.example`
   - Artifact: `spring-aop-lab`
   - Name: `spring-aop-lab`
   - Package name: `com.example`
3. Añade las dependencias:
   - Spring Boot DevTools (spring-boot-devtools)
4. Descarga, descomprime e importa el proyecto.

### Editar el fichero pom.xml

Agregar la dependencia (en Spring Boot 3.x):

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>

Agregar la dependencia (en Spring Boot 4.x)::

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-aspectj</artifactId>
    </dependency>

### Crear paquetes

- com.example.aop (main)
- com.example.aop (test)

## Paso 2: Clase principal

`src/main/java/com/example/SpringIocLabApplication.java`

```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy // Habilitar anotaciones AspectJ
@SpringBootApplication
public class SpringAopLabApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringIocLabApplication.class, args);
    }
}
```

## Paso 3. Crear una clase de servicio para probar el aspecto

`src/test/java/com/example/aop/DummyService.java`

```java
package com.example.aop;

import java.util.Optional;

import org.jspecify.annotations.NonNull;

public class DummyService {
    private String value = null;

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

    public void setValue(@NonNull String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("No acepto argumentos nulos");
        }
        this.value = value;
    }
    
    public void clearValue() {
        value = alwaysNull(); // auto referenciado
    }

    @NonNull 
    public String echo(String input) {
        return input;
    }
    
    public String alwaysNull() {
        return null;
    }

}
```

## Paso 4. Crear las Pruebas Automatizadas

Crea un test de integración en `src/test/java/com/example/aop/StrictNullChecksAspectTest.java`:

```java
package com.example.aop;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(DummyService.class)
class StrictNullChecksAspectTest {
    @Autowired
    DummyService service;
 
    @Test
    @DisplayName("Valores validos para la propiedad Value")
    void testValueOK() {
        assertDoesNotThrow(() -> service.setValue("valor"));
        assertTrue(service.getValue().isPresent());
        assertEquals("valor", service.getValue().get());
    }
   
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Valores invalidos para la propiedad Value")
    void testValueKO(String caso) {
        var ex = assertThrows(IllegalArgumentException.class, () -> service.setValue(caso));
        assertTrue(service.getValue().isEmpty());
    }

    @Test
    @DisplayName("Borrar (poner a null) la propiedad Value")
    void testClearValue() {
        assertDoesNotThrow(() -> service.setValue("not null"));
        assertTrue(service.getValue().isPresent());
        assertDoesNotThrow(() -> service.clearValue());
        assertNotNull(service.getValue());
        assertTrue(service.getValue().isEmpty());
    }


    @ParameterizedTest
    @ValueSource(strings = {"not null"})
    @EmptySource
    @DisplayName("Valores validos para los argumentos de los métodos")
    void testValidArgumentAndReturn(String caso) {
        assertEquals("not null", service.echo("not null"));
    }

    @Test
    @DisplayName("El aspecto lanza IllegalArgumentException con argumentos nulos")
    void testNullArgumentThrows() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.echo(null));
        assertTrue(ex.getMessage().contains("Illegal argument"));
    }

    @Test
    @DisplayName("El aspecto lanza NoSuchElementException con retornos nulos")
    void testNullReturnThrows() {
        Exception ex = assertThrows(java.util.NoSuchElementException.class, () -> service.alwaysNull());
        assertTrue(ex.getMessage().contains("Returns null"));
    }

}
```

> [!NOTE]
> Son pruebas de integración con @SpringBootTest para que SampleService sea inyectado y se verifique que se lanzan las excepciones esperadas.

> [!IMPORTANT]
> Las pruebas llaman al bean inyectado (pasa por el proxy AOP y activa el aspecto).

> [!TIP]
> Las pruebas se pueden ejecutar con `mvn test` o con el propio IDE.

## Paso 5. Ejecuta las pruebas

| Resultados (6/8)|
| --- |
| *Valores validos para los argumentos de los métodos* |
|   ✅ [1] caso='not null' |
|   ✅ [2] caso=''  |
| ❌ El aspecto lanza IllegalArgumentException con argumentos nulos |
| ❌ El aspecto lanza NoSuchElementException con retornos nulos |
| ✅ Valores validos para la propiedad Value |
| ✅ Borrar (poner a null) la propiedad Value |
| *Valores invalidos para la propiedad Value* |
|   ✅ [1] caso=null |
|   ✅ [2] caso='' |

> [!NOTE]
> El orden puede cambiar pseudo aleatoriamente.

## Paso 6. Crear el aspecto StrictNullChecks

`src/main/java/com/example/aop/StrictNullChecksAspect.java` 

```java
package com.example.aop;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class StrictNullChecksAspect {

}
```

## Paso 7. Añadir el consejo (advice) que compruebe los argumentos nulos

`src/main/java/com/example/aop/StrictNullChecksAspect.java` 

```java
package com.example.aop;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class StrictNullChecksAspect {
    @Before("execution(public * com.example..*.*(*,..))")
    public void nullArgument(JoinPoint jp) {
        for(var i = 0; i < jp.getArgs().length; i++) {
            if(Objects.isNull(jp.getArgs()[i])) 
                throw new IllegalArgumentException(String.format("Illegal argument %d in method '%s'", i + 1, jp.getSignature()));
        }
    }
}
```

## Paso 8. Ejecuta las pruebas

| Resultados (7/8)|
| --- |
| *Valores validos para los argumentos de los métodos* |
|   ✅ [1] caso='not null' |
|   ✅ [2] caso=''  |
| ✅ El aspecto lanza IllegalArgumentException con argumentos nulos |
| ❌ El aspecto lanza NoSuchElementException con retornos nulos |
| ✅ Valores validos para la propiedad Value |
| ✅ Borrar (poner a null) la propiedad Value |
| *Valores invalidos para la propiedad Value* |
|   ✅ [1] caso=null |
|   ✅ [2] caso='' |

> [!NOTE]
> El orden puede cambiar pseudo aleatoriamente.

## Paso 9. Añadir el consejo (advice) que compruebe valores nulos de retorno

`src/main/java/com/example/aop/StrictNullChecksAspect.java` 

```java title="app.js"
package com.example.aop;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class StrictNullChecksAspect {
    @Before("execution(public * com.example..*.*(*,..))")
    public void nullArgument(JoinPoint jp) {
        for(var i = 0; i < jp.getArgs().length; i++) {
            if(Objects.isNull(jp.getArgs()[i])) 
                throw new IllegalArgumentException(String.format("Illegal argument %d in method '%s'", i + 1, jp.getSignature()));
        }
    }
    @AfterReturning(pointcut="execution(* com.example..*.*(..)) && !execution(void *(..))", returning="retVal")
    public void nullReturn(JoinPoint jp, Object retVal) {
        if(Objects.isNull(retVal))
            throw new NoSuchElementException(String.format("Returns null in method '%s'", jp.getSignature()));
    }
}
```

## Paso 10. Ejecuta las pruebas

| Resultados (8/8)|
| --- |
| *Valores validos para los argumentos de los métodos* |
|   ✅ [1] caso='not null' |
|   ✅ [2] caso=''  |
| ✅ El aspecto lanza IllegalArgumentException con argumentos nulos |
| ✅ El aspecto lanza NoSuchElementException con retornos nulos |
| ✅ Valores validos para la propiedad Value |
| ✅ Borrar (poner a null) la propiedad Value |
| *Valores invalidos para la propiedad Value* |
|   ✅ [1] caso=null |
|   ✅ [2] caso='' |

> [!NOTE]
> El orden puede cambiar pseudo aleatoriamente.

> [!CAUTION]
> El auto refenciado no genero excepción. Para forzar a que lo valide, hay que sustituir la referencia propia (this) por la del proxy (target):
> ```java
>    public void clearValue() {
>        // value = alwaysNull(); // auto referenciado
>        value = ((DummyService) AopContext.currentProxy()).alwaysNull();
>    }
>```

| Resultados (5/8)|
| --- |
| *Valores validos para los argumentos de los métodos* |
|   ✅ [1] caso='not null' |
|   ✅ [2] caso=''  |
| ✅ El aspecto lanza IllegalArgumentException con argumentos nulos |
| ✅ El aspecto lanza NoSuchElementException con retornos nulos |
| ✅ Valores validos para la propiedad Value |
| ❌ Borrar (poner a null) la propiedad Value |
| *Valores invalidos para la propiedad Value* |
|   ❌ [1] caso=null |
|   ❌ [2] caso='' |

## Pontos clave del Aspecto

- **@Component:** Marca la clase como componente para que el scan la detecte.
- **@Aspect:** Marca la clase como aspecto de AOP.
- **@Before:** Intercepta antes de ejecutar métodos públicos en `com.example..*` con al menos un parámetro y lanza una `IllegalArgumentException` si algún argumento es nulo.
- **@AfterReturning:** Intercepta después de métodos con retorno (no `void`) y lanza una `NoSuchElementException` si el retorno es nulo.

## Estructura base

    spring-ioc-lab/
    ├─ src/main/java/com/example/
    │   ├─ SpringAopLabApplication.java
    │   └─ aop/StrictNullChecksAspect.java
    ├─ src/main/resources/
    │   └─ application.properties
    └─ src/test/java/com/example/
    │   ├─ aop/DummyService.java
        └─ aop/StrictNullChecksAspectTest.java

## Buenas Prácticas y Extensiones

- Puedes ajustar los pointcuts para afinar los métodos mas problemáticos.
- Considera usar anotaciones personalizadas para marcar métodos a validar en lugar de interceptar todo el paquete
- Agrega logging para una mejor trazabilidad.

## Recursos Adicionales

- [Spring AOP Reference](https://docs.spring.io/spring-framework/reference/core/aop.html)
- [AspectJ Pointcut Expressions](https://www.eclipse.org/aspectj/doc/next/progguide/semantics-pointcuts.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

---
© JMA 2024
