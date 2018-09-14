package com.rresino.akka4dummies.c05

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object MsgActor extends App {

  val system = ActorSystem("MsgActor")

  implicit val timeout = Timeout(1 seconds)
  val actor = system.actorOf(Props[MsgActor], name = "juanActor")

  println("Voy a saludar:")
  actor ! "saluda"
  println("Voy a enviar un número:")
  val future = actor ? 4

  val num = Await.result(future, 1 seconds).asInstanceOf[Int]
  println(s"La respuesta es: ${num}")

  println("Please press any key to exit:")
  try StdIn.readLine()
  finally system.terminate()
}

class MsgActor extends Actor {

  override def receive: Receive = {
    case name: String => println(s"Hola ${name} !!!")
    case num: Int => {
      println(s"Has enviado el número $num")
      sender ! (num * 2)
    }
  }

}
