# FSM o actores Finite State Machine

## Que son

Son máquinas de estado en las que el actor guarda un estado que se puede ir modificando mediante acciones que se le envían.
Os voy a enseñar como funcionan a través de un ejemplo que será un stack al que lo puedes añadir, borrar elementos o vaciar del todo.

## Definición de posibles estados

Lo primero que hay que definir es que estados posibles puede tener nuestro actor. En nuestro ejemplo serán 3, `EmptyStack` stack vacío, `Stack` normal o `FullStack` lleno.

```scala
sealed trait StackState
case object EmptyStack extends StackState
case object Stack extends StackState
case object FullStack extends StackState
```

## Definición de posibles acciones sobre el actor

El segundo paso es que acciones pueden realizarse al actor, es decir que mensajes pueden llegarle. Ya veremos que estos mensajes o acciones pueden o no modificar el estado interno del actor.
En nuestro ejemplo tenemos 3 acciones disponibles vaciar completamente el stack, añadir 1 elemento o borrar el primer elemento del stack.

```scala
sealed trait StackAction
case object CleanStack extends StackAction
case object AddElement extends StackAction
case object DeleteElement extends StackAction
```

## Inicializando el actor

El siguiente paso es crear nuestro actor que heredará de `FSM[S,D]`, siendo S el tipo de estados disponibles del actor y D el dato interno del actor.
Tambien tenemos que establecer el estado y los datos iniciales del actor usando el metodo `startWith(S,D)`.

Una vez definido esto hay que añadir la llamada a `initialize()` para que inicialice nuestro actor. Es importante que antes se halla llamado al método `startWith(S,D)` para indicar cual va a ser los valores iniciales.

```scala
class LittleStackActor extends FSM[StackState, collection.mutable.MutableList[Int]] {

  startWith(EmptyStack, new collection.mutable.MutableList[Int]())

  initialize()
}
```

## Acciones

Ahora viene lo más complejo que es indicar que queremos que hacer en función del estado del actor y de la acción. Esto se realiza usando el método `when(S)` y defines con `case` los que hacer para cada acción, no es imprescindible cubrir todas las acciones posibles.

En este ejemplo indicamos que si es actor está en el estado `Stack` y recibe la acción `AddElement` llamará al método `addElementToStack`. Para la acción `DeleteElement` llamará al método `deleteElementToStack`. Y por último para la acción `CleanStack` llamará al método `cleanStack`.

```scala
  when(Stack) {
    case Event(AddElement, stack) => addElementToStack(stack)
    case Event(DeleteElement, stack) => deleteElementToStack(stack)
    case Event(CleanStack, stack) => cleanStack(stack)
  }
```

Es importante que para todo las acciones indique si el actor cambiar de estado indicado cual con `goto(S)` o si se mantiene en el estado actual usando `stay()`. En nuestro ejemplo para evitar duplicar código nos hemos llevado la lógica a unos métodos. También es necesario indicar el dato interno del actor usando `using(D)`

```scala
  def cleanStack(stack: myStack): FSM.State[StackState, myStack] = {
    stack.clear()
    goto(EmptyStack) using(stack)
  }

  def addElementToStack(stack: myStack): FSM.State[StackState, myStack] = {
    stack += Random.nextInt(10)
    if (stack.length >= MAX_ELEMENTS) {
      goto(FullStack)
    } else {
      goto(Stack)
    } using(stack)
  }

  def deleteElementToStack(stack: myStack): FSM.State[StackState, myStack] = {
    if (stack.length <= 1) {
      goto(EmptyStack)
    } else {
      stay()
    } using(stack.tail)
  }
```

## Transiciones

En nuestro actor también podemos manejar la transiciones entre estados, pero recordar que si se utiliza el método `stay()` no se produce ninguna transición.
En el ejemplo hacemos un uso muy básico ya que solo lo utilizamos para logar la transición indicado el estado previo y posterior del actor.

```scala
  onTransition {
    case b -> a => println(s"Transición de ${b} a ${a}")
  }
```

## Unhandled event

Como bien indique arriba no es obligatorio manejar todas las combinaciones de estado - acción, pero en el caso que no se haga y se produzca el sistema no pintará una traza de advertencia:

```bash
[LifeCycleActor-akka.actor.default-dispatcher-3] [akka://LifeCycleActor/user/myLittleStack] unhandled event DeleteElement in state EmptyStack
```

Tanto si quiere eliminar esta advertencia como si quieres manejar estos eventos, puedes utilizar el método `whenUnhandled` y decidir que hacer. En nuestro ejemplo tan solo logamos el evento y dejamos el estado tal cual esta.

```scala
  whenUnhandled {
    case Event(e, stack) => {
      println(s"Unhandled event $e for stack $stack")
      stay()
    }
  }
```

Puedes ver el ejemplo completo en: [FSMExample](../src/main/scala/com/rresino/akka4dummies/c10/FSMExample.scala).

---

- Siguiente [????](./03_???.md)
- Volver a [Como crear un Actor](./04_how_to_create_actors.md)
- [Ir al Inicio](../README.md) 