# Mi primer Actor

Vamos a crear nuestro primer actor poco a poco.

> Explicación rápida: Un actor es una entidad que recibe mensaje y los ejecuta
> en el orden de llegada. A mí me gusta usar el símil del buzón de correos.

1. Primero crearemos nuestra clase MyFirstActor (pon el nombre que quieras).

2. Hacemos que herede de la clase `akka.actor.Actor`. Lo que nos obliga a 
   implementar el método `receive`.

  ```scala
  class MyFirstActor extends Actor {
    override def receive: Receive = ???
  }
  ```

3. El método `receive` es la parte más importante del actor. Se encarga de 
   realizar acciones en función del tipo de mensaje recibido. En nuestro 
   ejemplo en el caso de recibir de `"saluda"` (cadena de texto) ejecutará un 
   `println("Hola a todos!!")`.

  ```scala
  class MyFirstActor extends Actor {
    override def receive: Receive = {
      case "saluda" => println("Hola a todos!!")
    }
  }
  ```

4. Ya tenemos nuestro actor básico, el cual pinta un mensaje cada vez que 
   recibe un mensaje de tipo "saluda" en su buzón. Ahora hay que crear una 
   clase que cree una instancia del actor y lo utilice. Vamos a crear 
   un `object MyFirstActor` que extiende de `App`.

```scala
  object MyFirstActor extends App {
      
  }
```

5. En nuestro nuevo `object` vamos a inicializar el contexto de actores, 
   requisito imprescindible para crear actores. Al crear el contexto se le 
   puede pasar un nombre por si ha futuro quieres identificar el contexto o 
   tener varios. Sino se indica ninguno utiliza `"default"` como nombre.
   
  ```scala
  object MyFirstActor extends App {
    val system = ActorSystem("MyFirstActor")
  }
  ```

6. Ahora crearemos nuestro actor utilizando `system.actorOf` indicadole el tipo 
   de actor (clase) y el nombre que queremos darle. El nombre es opcional pero 
   muy recomendable para luego poder identificarle fácilmente. 

  ```scala
  object MyFirstActor extends App {
    val system = ActorSystem("MyFirstActor")
    val pepe = system.actorOf(Props[MyFirstActor](), "pepe_el_actor")
  }
  ```

7. Ahora tenemos que llamar a nuestro actor usando el metodo `tell` o `!` y el 
   mensaje a enviarle.

  ```scala
  pepe ! "saluda"
  ```
  
  ```scala
  pepe.tell("saluda")
  ```

8. Por último hay que añadir una parada del hilo principal debido a que la 
   llamada al actor usando `tell` es asíncrona y puede que termine el hilo 
   principal antes que el actor salude. Se puede hacer de muchas formas pero 
   mi propuesta es está, parar la ejecución del hilo solicitando al usuario 
   que pulse enter.   

  ```scala
  println("Please press any key to exit:")
  try StdIn.readLine()
  finally system.terminate()
  ```

9. Es importante al final ejecutar `system.terminate()` para finalizar el 
   contexto de los actores, sino se quedará activo esperando a la alguien lo 
   finalice.  

Este es el código de [ejemplo completo](../src/main/scala/com/rresino/akka4dummies/c03/MyFirstActor.scala):

```scala
import akka.actor.{Actor, ActorSystem, Props}

import scala.io.StdIn

object MyFirstActor extends App {

  val system = ActorSystem("MyFirstActor")

  val pepe = system.actorOf(Props[MyFirstActor](), "pepe_el_actor")

  pepe ! "saluda"
  // pepe.tell("saluda")

  println("Please press any key to exit:")
  try StdIn.readLine()
  finally system.terminate()
}

class MyFirstActor extends Actor {
  override def receive: Receive = {
    case "saluda" => println("Hola a todos!!")
  }
}
```
---

> Para empezar, solo hablaremos de actores no tipados. Pero en otros capítulos 
> hablaremos de los actores de los nuevos actores tipados.

---

- Siguiente [Como crear un Actor](./04_how_to_create_actors.md)
- Volver a [Crear un proyecto desde 0](./02_init_project.md)
- [Ir al Inicio](../README.md) 
