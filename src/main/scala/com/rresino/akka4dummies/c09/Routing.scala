package com.rresino.akka4dummies.c09

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing._

import scala.io.StdIn

object Routing extends App {


  val system = ActorSystem("LifeCycleActor")

  val father = system.actorOf(Props[FatherActor](), name = "fatherActor")

  (1 to 1000).foreach(i => father ! Msg(s"Hola este es el mensaje número $i"))


  println("Please press any key to exit:")
  try StdIn.readLine()
  finally system.terminate()
}

case class Msg(msg: String)

class FatherActor extends Actor {

//  val router = Router(BroadcastRoutingLogic(),
//  val router = Router(RandomRoutingLogic(),
//  val router = Router(RoundRobinRoutingLogic(),
  val router = Router(SmallestMailboxRoutingLogic(),
    Vector(
     ActorRefRoutee(context.actorOf(Props[SonActor](), "Juanito")),
     ActorRefRoutee(context.actorOf(Props[SonActor](), "Luisito")),
     ActorRefRoutee(context.actorOf(Props[SonActor](), "Jorgito")),
     ActorRefRoutee(context.actorOf(Props[SonActor](), "Tomasito")),
     ActorRefRoutee(context.actorOf(Props[SonActor](), "Paquito"))
    ))

  override def receive: Receive = {
    case m: Msg => router.route(m, sender())
  }

}

class SonActor extends Actor {

  var contador = 0

  override def receive: Receive = {
    case Msg(str) => {
      contador = contador + 1
      println(s"${self.path.name} ha recibido el mensaje: $str")
    }
  }

  override def postStop(): Unit =
    println(s"${self.path.name} ha recibido $contador mensajes")
}
