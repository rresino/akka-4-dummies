package com.rresino.akka4dummies.advance.c01

import akka.actor.{Actor, ActorSystem, Props}

import scala.io.StdIn

object DispatchersExample extends App {

  val system = ActorSystem("DispatchersExample")

  //implicit val executionContext = system.dispatchers.lookup("disp-forkito")
  implicit val executionContext = system.dispatchers.lookup("disp-pinedito")

//  println("Dispatcher: disp-forkito")
//  (0 until 100).foreach(id =>
//    system.actorOf(Props[TesterActor].withDispatcher("disp-forkito"), "tester-" + id) ! Start())
  println("Dispatcher: disp-pinedito")
  (0 until 100).foreach(id =>
    system.actorOf(Props[TesterActor]().withDispatcher("disp-pinedito"),
      "tester-p-" + id) ! Start())

  println("Please press any key to exit:")
  try StdIn.readLine()
  finally system.terminate()
}

case class Start()
case class Message(id:Int, str: String)
case class MessageAck(id:Int)

class TesterActor extends Actor {

  val MSG2SEND = 1000
  var startTime = 0L
  var acks = 0

  override def receive: Receive = {
    case Start() => {
      startTime = System.currentTimeMillis
      println("Start Test")
      (0 until MSG2SEND).foreach(i =>
        context.actorOf(Props[PrintActor]()) ! Message(i, "Hola hola"))
    }

    case MessageAck(_) => {
      acks += 1
      if (acks >= MSG2SEND) {
        val time = System.currentTimeMillis - startTime
        println(s"Test end: ${time /1000} (${time})")
      }
    }
  }
}

class PrintActor extends Actor {

  override def receive: Receive = {
    case Message(id, str) => {
      sender() ! MessageAck(id)
    }
  }
}
