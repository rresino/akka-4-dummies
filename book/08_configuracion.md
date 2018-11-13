# Configuración

Aunque Akka puede funcionar sin tener que crear ningún archivo de configuración, muchas veces es mejor configurar Akka mediante un fichero de configuración, que puede variar en función del entorno sin tener que generar diferentes compilados.
Akka utiliza la librería [Typesafe Config Library](https://github.com/lightbend/config) para gestiona la configuración que se ve en otros capítulos.

## Carga de configuración

Por defecto al iniciar el contexto de actores usando `val system = ActorSystem("miApp")` internamente utiliza la carga por defecto `ConfigFactory.load()`. Pero siempre se puede cargar la configuración manualmente y pasarlo al contexto en la creación de este.

```scala
val config = ConfigFactory.load("conf_actores")
val system = ActorSystem("miApp",config)
```

Por defecto la librería busca los siguiente archivos para cargar la configuración:

1. Propiedades o variables del sistema.
2. `application.conf`: Fichero de tipo HOCON, basado en el formato json.*
3. `application.json`: Fichero de configuración de tipo json. *
4. `application.properties`: Típico mensaje de propiedades de Java. *
5. `reference.conf`: Fichero de tipo HOCON, basado en el formato json.

*Nota:* * busca todos los archivos en el classpath con este nombre.

## Logging

Akka por defecto viene con un sistema de logging que utiliza internamente y puedes usar añadiendo a tu clase el trait `akka.actor.ActorLogging`. Pero se puede configurar el nivel y el tipo de implementación para el log.

```scala
akka {
    loggers = ["akka.event.Logging$DefaultLogger"]
    loglevel = "DEBUG"
}
```

- *akka.loggers:* Define la implementación a usar. Por ejemplo `akka.event.slf4j.Slf4jLogger` o `akka.event.Logging$DefaultLogger`.
- *loglevel:* Nivel por defecto para el log (ERROR, WARNING, INFO, DEBUG).

### Configuraciones avanzadas

- *akka.stdout-loglevel:* (OFF, ERROR, WARNING, INFO, DEBUG) Configura el log para el momento de arraque de la aplicación que envia al `system.out`.
- *akka.log-config-on-start:* (on, off) Inicializa la configuración a INFO cuando el sistema de actores está arrancando y todavía no se sabe que configuración esta usando.
- *akka.debug.receive:* (on, off) Loga todos los mensajes del usuario mediante la clase `akka.event.LoggingReceive` con el nivel DEBUG.
- *akka.debug.autoreceive:* (on, off) Loga todos los mensajes de tipo AutoReceiveMessages, como el Posion Pill o Kill.
- *akka.debug.lifecycle:* (on, off) Loga todos los cambios de estado del ciclo de vida (restart, stop...).
- *akka.debug.fsm:* (on, off) Loga los eventos del FSM como transiciones o temporizadores.
- *akka.debug.event-stream:* (on, off) Loga los eventos de cambio de suscripción del event-stream.
- *akka.remote.log-received-messages:* (on, off) Loga los eventos de entrada de actores remotos.

```scala
akka {
    loglevel = INFO
    stdout-loglevel = INFO
    log-config-on-start = on
    debug {
        receive = on
        autoreceive = on
        lifecycle = on
        fsm = on
        event-stream = on
    }
    remote {
        log-sent-messages = on
        log-received-messages = on
    }
}
```
