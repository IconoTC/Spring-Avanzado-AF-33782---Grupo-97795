# Laboratorio Spring AOP: Comprobación estricta de nulos

## Objetivo del laboratorio

Crear un aspecto con Spring  AOP que lance excepciones cuando:

- un método reciba un argumento con valor null (IllegalArgumentException)
- un método devuelva null (NoSuchElementException).

Este laboratorio te guiará paso a paso para crear, integrar y probar el aspecto `StrictNullChecksAspect` en una aplicación Spring Boot. Aprenderás a usar AOP (Programación Orientada a Aspectos) para validar argumentos y retornos nulos en los métodos de tu aplicación.

## Ejecutar las pruebas

    mvn clean test
