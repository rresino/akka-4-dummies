# Crear un proyecto desde 0

Como crear un proyecto para practicar con Akka.

## Paso 1: Elige tu sabor Java o Scala

Akka da soporte a ambos pero en esta guía vamos a mostrar los ejemplos con sintaxis Scala.

## Paso 2: Crear un proyecto

> Aunque pienses que solo con leerlo o seguir el libro es suficiente, te recomiendo encarecidamente que según vayamos avanzando pruebes en tu máquina todos los ejemplos. Ya que el hecho de escribir los ejemplos y ver que sucede al ejecutarlos sirve para afianzar lo conocimientos. Te lo digo por experiencia propia :wink: .
  
Vamos a crear un proyecto para ir practicando todo lo que vamos viendo. 
- Crear un proyecto usando [IntelliJ IDEA](https://www.jetbrains.com/idea/) o [Eclipse](https://www.eclipse.org/) o [VS Code](https://code.visualstudio.com/) .
- O usar mi generador de proyectos Scala: [https://github.com/rresino/template-scala​](https://github.com/rresino/template-scala​) .

### Requisitos:
1. Java 8 o superior
2. Scala 2.12 (solo si vas a optar por usar Scala)
3. Gestor de librerías (opcional pero recomendable). Véase Maven, Gradle, o SBT.

## Paso 3: Añadir las librerías necesarias
Os pongo el código necesario para agregar las librerías mínimas para empezar a usar Akka. Poco a poco iremos añadiendo más.

Esta librería es la base mínima para crear y usar los actores de Akka.

- Gradle

```groovy
dependencies {
  compile group: 'com.typesafe.akka', name: 'akka-actor_2.12', version: '2.5.16'
}
```

- SBT

```
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.16"
```

- Maven

```xml
<dependency>
  <groupId>com.typesafe.akka</groupId>
  <artifactId>akka-actor_2.12</artifactId>
  <version>2.5.16</version>
</dependency>
```

> Añado la versión 2.5.16 porque es la última publicada en el momento de escritura del manual.

---

- Siguiente [Mi primer Actor](./03_my_first_actor.md)
- Volver a [Como empezar a usar Akka](./book/01_how_to_begin.md)
- [Ir al Inicio](../README.md) 