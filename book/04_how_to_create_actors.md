# Como crear un Actor

En el capitulo anterior vimos un método básico para crear un actor, pero existen más.

## Actor con un constructor sin parámetros o por defecto

Es el uso más común de uso de los actores. Utilizaremos `ActorSystem.actorOf` y  `Props[Class]` para indicarle el actor a crear. Como anteriormente indicamos el nombre es opcional pero es muy recomendable.

```scala
val system = ActorSystem("CreateActor")

val actorDefCons = system.actorOf(Props[SimpleActor], name = "actorDefCons")

class SimpleActor extends Actor {
  override def receive: Receive = {
    case "saluda" => println("Hola a todos!!")
  }
}
```

## Actor con un constructor con parámetros

Es el método utilizado en el caso que el actor posea (o queramos usar) un constructor con parámetros. Es parecido al anterior pero sustituimos `Props[Class]` por la creación del objeto deseado.

```scala
val system = ActorSystem("CreateActor")

val actorParamCons = system.actorOf(
    Props(new ComplexActor(3)), name = "actorParamCons")

class ComplexActor(val times: Int) extends Actor {
  override def receive: Receive = {
    case "saluda" => println("Hola a todos " * times + "!!!")
  }
}
```

## Actor anidado dentro de otro

Es muy común crear actores desde otro actor, por ejemplo al recibir un mensaje o al inicializarse. Esto tendrá implicaciones a la hora de la supervisión que veremos más adelante. Para crearlo tan solo necesitaremos usar la variable `context` (heredada de la clase `Actor`) y su método `actorOf` para crear el actor.

```scala
val system = ActorSystem("CreateActor")

val actorFather = system.actorOf(
    Props[FatherActor], name = "actorFather")

class SimpleActor extends Actor {
  override def receive: Receive = {
    case "saluda" => println("Hola a todos!!")
  }
}

class FatherActor() extends Actor {

  val son = context.actorOf(Props[SimpleActor], name = "hijo")

  override def receive: Receive = {
    case "saluda" => {
      println("Hola el padre saluda!!!")
      son ! "saluda"
    }
  }
}
```

Este es el código de [ejemplo completo](../src/main/scala/com/rresino/akka4dummies/c04/CreateActor.scala).

## La importancia del nombre

Al crear un actor es de suma importancia crearlo con un nombre y path único, para que luego podamos encontrar nuestro actor fácilmente. Veremos en próximos capítulos que tan importante es el nombre como el path elegido al crearlo, pero de momento lo único que nos tiene que preocupar es que todos nuestros actores empezaran en el path `/user` más el path elegido al crearlo.

Por ejemplo el path del siguiente actor será `/user/actorFather`.

```scala
system.actorOf(
    Props[FatherActor], name = "actorFather")
```

> Cuidado con intentar crear 2 actores con el mismo path + nombre porque al intentar crear un actor con un path ya existente se provocará una excepción de tipo `akka.actor.InvalidActorNameException`.

## ¿Donde esta mi actor?

Hasta ahora hemos visto como crear actores. ¿Pero como puedo enviar un mensaje a un actor que ya exista?

### Buscado por referencia

Como hemos visto al crear un actor siempre nos devuelve la referencia a este `ActorRef` con la cual siempre podemos enviarle mensajes.

```scala
  val actorDefCons = system.actorOf(Props[SimpleActor], name = "LookupActor")
  actorDefCons ! "saluda"
```

Cuando estamos en un actor tenemos disponible la variable `context` de tipo `ActorContext` que nos permite obtener fácilmente la referencia al su actor padre o a si mismo.

```scala
class SimpleActor extends Actor {

  context.parent ! "saluda"
  context.self ! "saluda"

}
```

### Buscado un actor con una ruta absoluta

Podemos usar el método `actorSelection` para recuperar la referencia de un actor que ya exista y asi poder usarlo. Para ello debemos usar el path completo del actor para recuperarlo. Cuidado con no olvidar el `/user` para nuestros actores.

```scala
val actor1 = system.actorSelection("/user/mypath/SimpleActor")
```

### Buscado un actor con una ruta relativa

También podemos usar rutas relativas como si se tratara de un sistema de ficheros. Usando `../` para acceder al padre del actual actor.

```scala
val actor3 = context.actorSelection("../mypath/SimpleActor")
```

> Cuidado con el método `actorSelection` porque no crea el actor y en el caso que no exista el actor estaremos enviando mensajes al buzón de `dead-letters`, que es donde acaban todos los mensajes cuando el destinatario no esta disponible.

Este es el código de [ejemplo](../src/main/scala/com/rresino/akka4dummies/c04/LookUpActor.scala).

---

- Siguiente [Enviando mensajes](./05_msgs.md)
- Volver a [Mi primer Actor](./03_my_first_actor.md)
- [Ir al Inicio](../README.md) 