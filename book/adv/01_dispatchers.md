# Dispatchers

Un dispatcher de akka es el encargado de enviar todos los mensajes a sus destinatarios (actores).
En el símil de los buzones sería el cartero. Recoge todos los mensajes de todos los senders/remitentes y los deposita en el buzón de cada actor.

## Tipos

Hay que tener cuidado con la configuración del/los dispatchers porque puede pasar de ejecución optima a un desastre completo.

- **`Dispatcher`:** o dispatcher por defecto. Todos los actores comparten un pool de threads/hilos. Manejado por un `java.util.ExecutorService`, el cual puede ser un "fork-join-executor" o "thread-pool-executor".
- **`PinnedDispatcher`:** Cada actor tendrá su propio hilo que no se comparte con el resto de actores. Este caso es útil cuando un actor es susceptible de bloquear la ejecución y no quieres afectar al resto de actores.
- **`CallingThreadDispatcher`:** Ejecuta todo sobre el hilo actual, sin crear hilos adicionales. Solo se debería de ejecutar con fines de test o depuración de funcionamiento.

## Configuración de los ejecutores

En función del tipo puede elegir que executor quieres usar:

- **`ForkJoinPool`:** Opción por defecto y suele ser la optima para la mayoría de los casos. Una implementación basada en un grupo de procesos con colas sin bloqueos y con robo de tareas. Todos los hilos intentan encontrar y ejecutar tareas enviadas y/o creadas por otras tareas activas. Esta configuración permite un funcionamiento adecuado cuando las tareas generan otras sub-tareas y para tareas pequeñas generadas por clientes externos.

- **`ThreadPoolExecutor`:** Es una implementación basada en un `LinkedBlockingQuenue` para guardar las tareas, y tiene un número configurado de hilos para ir ejecutándolas. Funciona como el típico patrón consumidor productor.  

## Configuración

Para crear una nueva configuración hay que añadirla al fichero de configuración. En nuestro ejemplo lo añadimos a nuestro [application.conf](../../src/main/resources/application.conf).

```scala
disp-forkito {
  # Dispatcher is the name of the event-based dispatcher
  type = Dispatcher
  # What kind of ExecutionService to use
  executor = "fork-join-executor"
  # Configuration for the fork join pool
  fork-join-executor {
    # Min number of threads to cap factor-based parallelism number to
    parallelism-min = 2
    # Parallelism (threads) ... ceil(available processors * factor)
    parallelism-factor = 2.0
    # Max number of threads to cap factor-based parallelism number to
    parallelism-max = 10
  }
  # Throughput defines the maximum number of messages to be
  # processed per actor before the thread jumps to the next actor.
  # Set to 1 for as fair as possible.
  throughput = 10
}
```

1. Primero elegiremos un nombre `disp-forkito`.
2. Luego seleccionamos que tipo vamos a utilizar `Dispatcher`.
3. Luego el executor/ejecutor `fork-join-executor`.
4. En función del executor elegido hay que configurarlo adecuadamente indicado:
    - El número mínimo de threads/hilos `parallelism-min = 2`
    - El factor de número de hilos a usar (operaciones en paralelo), este factor utilizara el calculo número de ceil(procesadores * factor) = Threads. `parallelism-factor = 2.0`.
    - El número máximo de hilos. `parallelism-max = 10`.
- Y por ultimo el `throughput` que es el número máximo de mensajes a enviar antes de cambiar al siguiente actor. Cuidado porque un número bajo indicara una repartición más justa pero habrá un cambio de actores más constante y por tanto bajaran el rendimiento de envío de mensajes.

## Configurar mis actores con un dispatcher

Se puede hacer de 3 formas:

- En la creación de un actor: `system.actorOf(Props[TesterActor].withDispatcher("disp-pinedito"), "tester")`
- Establecer el parametro como implicito y por tanto todos los actores que se creen a continuación lo utilizaran implicitamente `implicit val executionContext = system.dispatchers.lookup("disp-forkito")`
- O por configuración. Como ya hemos visto en otros artículos los actores se pueden configurar usando el/los archivo/s de configuración y en este caso también se puede indicar el dispatcher.

```scala
  akka.actor.deployment {
    /tester {
        dispatcher = disp-pinedito
    }
  }
```

Os dejo un [ejemplo](../../src/main/scala/com/rresino/akka4dummies/advance/c01/DispatchersExample.scala) para que podáis probar las diferentes configuraciones de los dispatchers.

---

- Siguiente [stash, become y unbecome: Jugando con el buzón](./02_mailbox.md)
- Volver a [Akka Avanzado](./README.md)
- [Ir al Inicio](../../README.md)