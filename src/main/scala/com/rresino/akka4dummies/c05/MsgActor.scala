package com.rresino.akka4dummies.c05

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object MsgActor extends App {

  val system = ActorSystem("MsgActor")

  implicit val timeout = Timeout(1, SECONDS)
  val actor = system.actorOf(Props[MsgActor], name = "juanActor")
  val actorProxy = system.actorOf(Props(new ProxyActor(actor)), name = "proxyActor")

  println("Voy a saludar:")
  actor ! "saluda"
  println("Voy a enviar un número:")
  val future = actor ? 4

  val num = Await.result(future, timeout.duration).asInstanceOf[Int]
  println(s"La respuesta es: ${num}")

  println("Voy a saludar usando un proxy:")
  actor ! "saluda"
  println("Voy a enviar un número al proxy:")
  val futureProxy = actor ? 42

  val numProxy = Await.result(futureProxy, timeout.duration).asInstanceOf[Int]
  println(s"La respuesta es: ${numProxy}")


  println("Please press any key to exit:")
  try StdIn.readLine()
  finally system.terminate()
}

class ProxyActor(actorRef: ActorRef) extends Actor {
  override def receive: Receive = {
    case msg => {
      println("Proxy ...")
      actorRef forward msg
    }
  }
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
