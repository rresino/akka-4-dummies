# stash, become y unbecome: Jugando con el buzón

Hay ocasiones que se quiere cambiar el comportamiento a un Actor para que haga cosas diferentes cuando pase algo, esto se puede hacer con las operaciones `become` y `unbecome`.

Como el concepto es complicado vamos a poner un ejemplo practico para verlo claramente.

## Que queremos

Queremos una calculadora con dos modos disponible sumar y restar. Al cambiar de modo le podremos pasar números y los ira sumando/restando y acumulando el resultado.
Por defecto estará en modo neutro (No mode) y no hace nada con los números que le pasemos.

- No mode: No hace nada con los números.
- Sum mode: Modo suma, suma el número al acumulado y guarda el resultado.
- Minus mode: Modo resta, resta el número al acumulado y guarda el resultado.

## Creado los mensajes

Empecemos creando los posibles mensajes del actor.

- `SumSwitch` Cambiar el modo a Sum Mode.
- `MinusSwitch` Cambiar el modo a Minus Mode.
- `ResetSwitch` Resetea el modo y pasa a modo Neutro.
- `NumberMsg` Envia un nuevo número a sumar/restar a nuestra calculadora.
- `ShowMsg` Pinta por consola el valor acumulado, independientemente del modo.

```scala
case class SumSwitch()
case class MinusSwitch()
case class ResetSwitch()
case class NumberMsg(number: Int)
case class ShowMsg()
```

## Modo Neutro

Ahora nos toca crear nuestro actor, con su variable para acumular el resultado de las operaciones. Y hay manejar los mensajes que nos podrían llegar:

- `SumSwitch` Cambiar el modo a Sum Mode. Usando `become` y le enviamos el nuevo comportamiento.
- `MinusSwitch` Igual que el anterior pero con el modo Minus.
- `ResetSwitch` No hay que hacer nada, porque ya estamos en modo neutro.
- `NumberMsg` Al estar en modo neutro no haremos nada. Pintaremos un mensaje de advertencia al usuario.
- `ShowMsg` Pinta por consola el valor acumulado.

Para cambiar el comportamiento hay que utilizar el método `context.become()` que espera recibir un `behavior: Actor.Receive`, al igual que nuestro método `override def receive: Receive`.

Una vez cambiado el comportamiento nuestro actor usará este nuevo comportamiento hasta que se ejecute `context.unbecome()` que le devolverá a su comportamiento original.

```scala
class CalculatorActor extends Actor {

  import context._

  var total = 0

  override def receive: Receive = {
    case SumSwitch() => {
      println("No mode: Sum mode on")
      become(sumCal)
    }
    case MinusSwitch() => {
      println("No mode: Minus mode on")
      become(minusCal)
    }
    case ShowMsg() => println(s"No mode: Result: ${total}")
    case _ => println("No mode: Please set one mode")
  }
}
```

## Cambiando el comportamiento del actor

Ahora nos toca el método `sumCal` que define el nuevo comportamiento del actor, que es estructuralmente hablando igual a la definición del método `override def receive: Receive`.

- `SumSwitch` No hay que hacer nada ya estamos en modo Sum.
- `MinusSwitch` Llamaremos a `become` con el nuevo comportamiento.
- `ResetSwitch` Utilizamos `unbecome` para volver al comportamiento original.
- `NumberMsg` Sumamos el número indicado y guardamos el resultado.
- `ShowMsg` Pinta por consola el valor acumulado.

```scala
  def sumCal: Receive = {
    case SumSwitch() => println("Sum mode: It's now on sum mode")
    case MinusSwitch() => {
      println("Sum mode: Minus mode on")
      become(minusCal)
    }
    case ResetSwitch() => {
      println("Sum mode: Reset mode")
      unbecome()
    }
    case NumberMsg(n: Int) => total += n
    case ShowMsg() => println(s"Sum mode: Result: ${total}")
  }
```

Para el método `minusCal` haremos lo mismo pero restando los número cuando sea necesario.

Tenéis el ejemplo completo en [MailBoxExample](../../src/main/scala/com/rresino/akka4dummies/advance/c02/MailBoxExample.scala)

## Un paso más

Ahora vamos a ir un paso más alla. Si os fijais en el ejemplo anterior si intentamos enviar un número `NumberMsg(2)` estando en modo neutro este número se pierde, porque no sabemos si hay que sumarlo o restarlo. Fijate que en ejecutais el ejemplo os devolvera algo parecido a esto:

```bash
Please press any key to exit:
No mode: Result: 0
No mode: Please set one mode
No mode: Result: 0
No mode: Sum mode on
Sum mode: Result: 0
Sum mode: Result: 20
Sum mode: Minus mode on
Minus mode: Result: 15
Minus mode: Reset mode
No mode: Please set one mode
No mode: Result: 15
```

Si os fijáis en el mensaje `No mode: Please set one mode` es porque enviamos un número estando en modo neutro.

## Stash

Pero ahora os voy a presentar a `stash()` que es un comando para guardar mensajes temporalmente, para que luego se puedan procesar (o no). Con esto podemos guardar los mensajes que no se puedan procesar en modo neutro y recuperarlos con `unstashAll()` al cambiar de modo.

Primero tenemos que modificar nuestro actor para que implemente el trait `akka.actor.Stash`.

```scala
class CalculatorActor extends Actor with Stash {
    ...
}
```

Y ahora cambiaremos el comportamiento en el modo neutro para guardar y recuperar los mensajes como hemos hablado antes:

```scala
    override def receive: Receive = {
      case SumSwitch() => {
        println("No mode: Sum mode on")
        unstashAll()
        become(sumCal)
      }
      case MinusSwitch() => {
        println("No mode: Minus mode on")
        unstashAll()
        become(minusCal)
      }
      case NumberMsg(n: Int) => stash()
      case ShowMsg() => println(s"No mode: Result: ${total}")
      case _ => println("No mode: Please set one mode")
    }
```

Tenéis el ejemplo completo en [MailBoxStashExample](../../src/main/scala/com/rresino/akka4dummies/advance/c02/MailBoxStashExample.scala)

---

- Siguiente [Temporizadores](./03_time.md)
- Volver a [Dispatchers](./01_dispatchers.md)
- [Ir al Inicio](../../README.md)