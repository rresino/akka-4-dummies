# Como crear un Actor

En el capitulo anterior vimos un método básico para crear un actor, pero existen más.

- **Actor con un constructor sin parámetros o por defecto:** Es el uso más común de uso de los actores. Se crea utiliza `ActorSystem.actorOf` y usamos `Props[Class]` para indicarle el actor a crear. Como anteriormente indicamos el nombre es opcional pero es muy recomendable.

```scala
val system = ActorSystem("CreateActor")

val actorDefCons = system.actorOf(Props[SimpleActor], name = "actorDefCons")

class SimpleActor extends Actor {
  override def receive: Receive = {
    case "saluda" => println("Hola a todos!!")
  }
}
```

- **Actor con un constructor con parámetros:** Es el método utilizado en el caso que el actor a crear tenga (o queramos usar) un constructor con párametros. Es parecido al anterior pero sustituimos `Props[Class]` por la creación del objeto deseado.

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

- **Actor anidado dentro de otro:** Es muy común crear actores desde otro, esto tendrá implicaciones a la hora de la supervisión que veremos más adelante. Para crearlo tan solo necesitaremos usar la variable `context` y su método `actorOf` para crear el actor.

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

Este es el código de [ejemplo completo](..\src\main\scala\com\rresino\akka4dummies\c04\CreateActor.scala).

--- 

- Siguiente [????](./03_???.md)
- Volver a [Mi primer Actor](./03_my_first_actor.md)
- [Ir al Inicio](../README.md) 