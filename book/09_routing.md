# Enrutado de mensajes

Akka permite configurar como dirigir un mensaje a los múltiples actores o pool de actores, para optimizar el comportamiento deseado.

> Al decir enrutado me refiero [El enrutamiento o ruteo es la función de buscar un camino entre todos los posibles en una red de paquetes](https://es.wikipedia.org/wiki/Encaminamiento), en este caso mensajes.

## Actores de 'enrutado'

El 'enrutado' se puede hacer desde un actor con hijos o simplemente por configuración:

- **Pool:** Un actor 'padre' con actores hijos que serán los destinatarios de los mensajes son creados por el padre y lo elimina al terminar.
- **Group:** Los actores destino son creados externamente, el actor de enrutado envía los mensajes usando el path del actor destino, pero no vigila si algún hijo ha terminado o parado.

## Tipos de enrutamiento:

- `akka.routing.RoundRobinRoutingLogic`: Utiliza la técnica de distribución de carga [round robin](https://en.wikipedia.org/wiki/Round-robin_DNS), con el cual se obtiene una distribución casi igualitaria.
- `akka.routing.RandomRoutingLogic`: Distribución aleatoria de carga.
- `akka.routing.SmallestMailboxRoutingLogic`: Envía el mensaje al actor/es con el buzón menos saturado. Esta estrategia es muy util cuando el procesado de los mensajes puede variar mucho y una asignación igualitaria puede producir saturación y lentitud si coinciden mensajes 'pesados' en un mismo actos, mientras actores con mensajes más 'ligeros' están ociosos.
- `akka.routing.BroadcastRoutingLogic`: Envía el mismo mensaje a todos los actores, ideal para informar a todos los actores del algo. Por ejemplo un mensaje de parada de sistema.
- `akka.routing.ScatterGatherFirstCompletedRoutingLogic`: Envía un mensaje a todos los actores pero solo utiliza la primera respuesta que reciba.
- `akka.routing.TailChoppingRoutingLogic`: Los actores destinos se ordenan aleatoriamente y envia el mensaje al primer actor sino response en el tiempo indicado (`interval`) se lo envía al siguiente actor hasta que algún actor responda o el pool de actores se termine. En el caso que nadie responda al sender se le responde con un `akka.actor.Status.Failure` que encapsula a un `akka.pattern.AskTimeoutException`.
- `akka.routing.ConsistentHashingRoutingLogic`: Utiliza un hash consistente para saber a que actor enviar el mensaje. Para definir un hash se puede utilizar 3 metodos (o varios). En el caso de existir varios se resolverán en order de prioridad:
  1. Definir `hashMapping` / `withHashMapper`  del router para calcular para cada mensajes su hash, haciendo la decisión transparente al remitente.  
  2. El mensaje puede implementar `akka.routing.ConsistentHashingRouter.ConsistentHashable`. Y con esto se calculara el hash. Delegas la responsabilidad del calculo del hash al mensaje.
  3. Encapsular el mensaje en `akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope`. Esto delega la responsabilidad al remitente para que asigne el hash a utilizar.
- Personalizado: Hay que extender la clase `RoutingLogic` e implementar el método `select` que se encarga de decidir a donde enviar el mensaje. 

## Como configurar el enrutado

- **Por configuración:** Se define uno o varios enrutados en el archivo de configuración y defines una ruta + nombre (`/parent/miPoolRouter`) que posteriormente indicas en el actor.

    Ejemplo: Crea un router del tipo pool y `round robin` con 10 instancias.

```
akka.actor.deployment {
  /parent/miPoolRouter {
    router = round-robin-pool
    nr-of-instances = 10
  }
}
```

```scala
val router: ActorRef =
  context.actorOf(FromConfig.props(Props[SonActor]), "miPoolRouter")
```

- **Por código:** Se define en la clase deseada el enrutado y creas el pool de actores.

```scala
  val router = Router(RoundRobinRoutingLogic(),
    Vector(
     ActorRefRoutee(context.actorOf(Props[SonActor], "Juanito")),
     ActorRefRoutee(context.actorOf(Props[SonActor], "Luisito")),
     ActorRefRoutee(context.actorOf(Props[SonActor], "Jorgito")),
     ActorRefRoutee(context.actorOf(Props[SonActor], "Tomasito")),
     ActorRefRoutee(context.actorOf(Props[SonActor], "Paquito"))
    ))
```

Hay un código de ejemplo en el que se puede ver la diferencias de usar un tipo de enrutado frente a otro [ejemplo](../src/main/scala/com/rresino/akka4dummies/c09/Routing.scala).

---

- Volver a [Configuración](./08_configuracion.md)
- [Ir al Inicio](../README.md)