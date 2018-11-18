# Enviando mensajes

Ahora vamos a repasar como enviar mensajes a los actores. Existen 2 tipos:

- `tell`
- `ask`
- `forward`

## `tell` o mensaje asíncrono

Envía un mensaje asíncrono (fire & forget) al actor indicado, sin esperar a que este contesté o complete la acción. Al enviar un mensaje usando `tell` el hilo de ejecución no se paraliza y continua en la siguiente instrucción.

Para enviar un mensaje se puede hacer de 3 formas diferentes:

- `actor ! "mensaje"`
- `actor.!("mensaje")`
- `actor tell "mensaje"`
- `actor.tell("mensaje")`

## `ask` o mensaje síncrono

Envía un mensaje que nos devuelve un mensaje con un futuro. Este futuro es la 'promesa' de la respuesta del actor. Podemos hacer que el programa espere la respuesta del actor (pausando la ejecución del hilo) hasta que este responda o se produzca un timeout en la espera. 

- `actor ? "mensaje"`
- `actor.?("mensaje")`
- `actor ask "mensaje"`
- `actor.ask("mensaje")`

Para enviar una respuesta a un mensaje hay que utilizar el objeto `sender` para referirse al actor que ha enviado el mensaje y usar un `tell` o un `ask` para enviar la respuesta.

```scala
class MsgActor extends Actor {

  override def receive: Receive = {
    case name: String => println(s"Hola ${name} !!!")
    case num: Int => {
      println(s"Has enviado el número $num")
      sender ! (num * 2)
    }
  }
}
```

## `forward` Reenvío de mensajes

Puede darse la situación que quieras reenviar un mensaje a otro actor, para esto puedes usar el metodo `forward` para reenviar un mensaje. Permite el reenvío de mensajes tanto `ask` como `tell` porque envía también las referencias del `sender` original.

- `actorRef forward "mensaje"`
- `actorRef.forward("mensaje")`

## Mailbox

Todos los actores tienen un buzón de mensajes o mailbox en el que van almacenándose los mensajes recibidos para mientras se van procesando en el orden de llegada (FIFO). 

## Deadletter mailbox

Pero en el caso que un actor no este disponible o no pueda procesar un mensaje este se almacenará en un buzón especial llamado `deadletter mailbox`.

Este es el código de [ejemplo completo](../src/main/scala/com/rresino/akka4dummies/c05/MsgActor.scala).

--- 

- Siguiente [Supervisión](./06_supervision.md)
- Volver a [Como crear un Actor](./04_how_to_create_actors.md)
- [Ir al Inicio](../README.md) 