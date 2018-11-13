# Como Parar/Matar un actor

Hay varias maneras de para un actor:

- **Parada del sistema:** Al recibir una parada del sistema todos los actores reciben un mensaje de parada.

```scala
system.shutdown()
```

- **`ActorRefFactory.stop`:** Se puede invocar al método stop del contexto o del actorSystem y la instancia del actor a parar o a si mismo usando `self`. Esto provoca que el actor se procese el mensaje de parada y envíe el resto de mensajes del mailbox  del actor al deadletter mailbox. Al pararse ejecuta el método `postStop` y enviar automáticamente señales de parada a todos sus hijos.

```scala
val son = context.actorOf(Props[LifeCycleSonActor], name)
context.stop(son)
```

- **`PoisonPill` o pastilla venenosa:** Envía un mensaje de parada que se encola en el mailbox como un mensaje más, y al procesarse para el actor. Es una forma de parada controlada de un actor, dejándole que termine de procesar los mensajes que tiene.

```scala
val son = context.actorOf(Props[LifeCycleSonActor], name)
son ! PoisonPill
```

- **`Kill`:** Enviar un mensaje de 'matar' a un actor, lo que provoca que el actor lanzé una excepción `ActorKilledException` que será gestionado por el supervisor.

```scala
val son = context.actorOf(Props[LifeCycleSonActor], name)
son ! Kill
```

- **`gracefulStop`:** Para controladamente un actor. Permite evitar esperar a la finalización del actor o anidar paradas de actores. Crea un futuro con el timeout indicado y por ultimo la forma de parada. En si no es una forma de parada sino un utilidad de parada de actores (mensaje).

> En general no se recomienda parar actores usando `PoisonPill` o `Kill` y usar métodos más controlados como `stop`.

--- 

- Siguiente [????](./03_???.md)
- Volver a [Como crear un Actor](./04_how_to_create_actors.md)
- [Ir al Inicio](../README.md) 