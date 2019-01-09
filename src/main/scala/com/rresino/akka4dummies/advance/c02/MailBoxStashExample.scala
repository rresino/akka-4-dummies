package com.rresino.akka4dummies.advance.c02

import akka.actor.{Actor, ActorSystem, Props, Stash}

import scala.io.StdIn

object MailBoxStashExample extends App {

  val system = ActorSystem("MailBoxStashExample")

  val actor = system.actorOf(Props[CalculatorActor],"MailBox")

  actor ! ShowMsg()
  actor ! NumberMsg(1)
  actor ! ShowMsg()
  actor ! SumSwitch()
  actor ! ShowMsg()
  actor ! NumberMsg(20)
  actor ! ShowMsg()
  actor ! MinusSwitch()
  actor ! NumberMsg(5)
  actor ! ShowMsg()
  actor ! ResetSwitch()
  actor ! NumberMsg(5)
  actor ! ShowMsg()

  println("Please press any key to exit:")
  try StdIn.readLine()
  finally system.terminate()

  case class SumSwitch()
  case class MinusSwitch()
  case class ResetSwitch()
  case class NumberMsg(number: Int)
  case class ShowMsg()

  class CalculatorActor extends Actor with Stash {

    import context._

    var total = 0

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

    def minusCal: Receive = {
      case MinusSwitch() => println("Minus mode: It's now on minus mode")
      case SumSwitch() => {
        println("Minus mode: Sum mode on")
        become(sumCal)
      }
      case ResetSwitch() => {
        println("Minus mode: Reset mode")
        unbecome()
      }
      case NumberMsg(n: Int) => total -= n
      case ShowMsg() => println(s"Minus mode: Result: ${total}")
    }
  }
}


