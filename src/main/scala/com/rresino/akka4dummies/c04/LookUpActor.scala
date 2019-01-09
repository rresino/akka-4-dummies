package com.rresino.akka4dummies.c04

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.io.StdIn

object LookUpActor extends App {

  val system = ActorSystem("LookupActor")
  implicit val timeout = Timeout(1, SECONDS)

  val actorDefCons = system.actorOf(Props[MyPathActor], name = "mypath")

  actorDefCons ! "saluda1"

  val actor1 = system.actorSelection("/user/mypath/SimpleActor")
  val actor2 = system.actorSelection("/user/mypath/fake")

  actor1 ! "saluda1"
  actor2 ! "saluda2"

  println("Please press any key to exit:")
  try StdIn.readLine()
  finally system.terminate()
}

class MyPathActor extends Actor {

  val son = context.actorOf(Props[MyPathSonActor], "SimpleActor")
  val actor3 = context.actorSelection("../mypath/SimpleActor")
  actor3 ! "saluda3"

  override def receive: Receive = {
    case "saluda1" => println("Hola 1!!")
    case "saluda2" => println("Hola 2!!")
    case "saluda3" => println("Hola 3!!")
  }

}

class MyPathSonActor extends Actor {

  override def receive: Receive = {
    case "saluda1" => println("Hola a todos 1!!")
    case "saluda2" => println("Hola a todos 2!!")
    case "saluda3" => println("Hola a todos 3!!")
  }
}
