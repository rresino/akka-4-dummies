package com.rresino.akka4dummies.c04

import akka.actor.{Actor, ActorSystem, Props}
import scala.io.StdIn

object CreateActor extends App {

  val system = ActorSystem("CreateActor")

  val actorDefCons = system.actorOf(Props[SimpleActor], name = "actorDefCons")
  actorDefCons ! "saluda"

  val actorParamCons = system.actorOf(
    Props(new ComplexActor(3)), name = "actorParamCons")
  actorParamCons ! "saluda"

  val actorFather = system.actorOf(
    Props[FatherActor], name = "actorFather")
  actorFather ! "saluda"

  println("Please press any key to exit:")
  try StdIn.readLine()
  finally system.terminate()
}

class SimpleActor extends Actor {
  override def receive: Receive = {
    case "saluda" => println("Hola a todos!!")
  }
}

class ComplexActor(val times: Int) extends Actor {
  override def receive: Receive = {
    case "saluda" => println("Hola a todos " * times + "!!!")
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
