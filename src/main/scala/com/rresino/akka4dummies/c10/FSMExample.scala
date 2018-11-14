package com.rresino.akka4dummies.c10

import akka.actor.{ActorSystem, FSM, Props}
import com.rresino.akka4dummies.c10.FSMExample.myStack

import scala.io.StdIn
import scala.util.{Random, Try}

object FSMExample extends App {

  type myStack =  collection.mutable.MutableList[Int]

  val system = ActorSystem("LifeCycleActor")
  val stackActor = system.actorOf(Props[LittleStackActor], name = "myLittleStack")

  def showOptions(): Unit= {
    println
    println("Hola bienvenido a Stack FSM:")
    println("- Pulse 1 para añadir un elemento aleatorio.")
    println("- Pulse 2 para borrar el primer elemento.")
    println("- Pulse 3 para vaciar el stack.")
    println("- Pulse Q para salir.")
    println
  }

  def readOption(): Try[Char] = Try {
    StdIn.readChar().toLower
  }

  def doAction(opc: Char): Unit = {
    opc match {
      case '1' => stackActor ! AddElement
      case '2' => stackActor ! DeleteElement
      case '3' => stackActor ! CleanStack
      case 'q' => println("exit")
      case _ => println("No te entiendo")
    }
  }

  var opc = ' '
  while (opc != 'q') {
    showOptions()
    opc = readOption().getOrElse(' ')
    doAction(opc)
  }

  println("Bye bye")
  system.terminate()
}

sealed trait StackState
case object EmptyStack extends StackState
case object Stack extends StackState
case object FullStack extends StackState

sealed trait StackAction
case object CleanStack extends StackAction
case object AddElement extends StackAction
case object DeleteElement extends StackAction

class LittleStackActor extends FSM[StackState, myStack] {

  val MAX_ELEMENTS = 5
  startWith(EmptyStack, new myStack())

  when(EmptyStack) {
    case Event(AddElement, stack) => addElementToStack(stack)
  }

  when(Stack) {
    case Event(AddElement, stack) => addElementToStack(stack)
    case Event(DeleteElement, stack) => deleteElementToStack(stack)
    case Event(CleanStack, stack) => cleanStack(stack)
  }

  when(FullStack) {
    case Event(DeleteElement, stack) => deleteElementToStack(stack)
    case Event(CleanStack, stack) => cleanStack(stack)
  }

  whenUnhandled {
    case Event(e, stack) => {
      println(s"Unhandled event $e for stack $stack")
      stay()
    }
  }

  onTransition {
    case b -> a => println(s"Transición de ${b} a ${a}")
  }

  def cleanStack(stack: myStack): FSM.State[StackState, myStack] = {
    stack.clear()
    println("stack: " + stack)
    goto(EmptyStack) using(stack)
  }

  def addElementToStack(stack: myStack): FSM.State[StackState, myStack] = {

    stack += Random.nextInt(10)
    println("stack: " + stack)
    if (stack.length >= MAX_ELEMENTS) {
      goto(FullStack)
    } else {
      goto(Stack)
    } using(stack)
  }

  def deleteElementToStack(stack: myStack): FSM.State[StackState, myStack] = {

    println("stack: " + stack.tail)
    if (stack.length <= 1) {
      goto(EmptyStack)
    } else {
      stay()
    } using(stack.tail)
  }

  initialize()
}
