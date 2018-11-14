package com.rresino.akka4dummies.c03

import akka.actor.{Actor, ActorSystem, Props}

import scala.io.StdIn

object MyFirstActor extends App {

  val system = ActorSystem("MyFirstActor")

  val pepe = system.actorOf(Props[MyFirstActor], "pepe_el_actor")

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
