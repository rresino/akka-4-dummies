package com.rresino.akka4dummies.c06

import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
import akka.actor._
import akka.pattern._
import akka.util.Timeout
import com.rresino.akka4dummies.c06.SupervisionActor.{Create, Msg}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Failure, Success}

object SupervisionActor extends App {

  val system = ActorSystem("LifeCycleActor")

  implicit val timeout = Timeout(1, SECONDS)

  val father = system.actorOf(Props[SupervisionFatherActor], name = "lifeFatherActor")

  case class Msg(action: String)
  case class Create(name: String)

  val actor = for {
    _  <- (father ? Create("hijo1")).mapTo[ActorRef]
    _  <- (father ? Create("hijo2")).mapTo[ActorRef]
    f3 <- (father ? Create("hijo3")).mapTo[ActorRef]
  } yield (f3)

  actor.onComplete {
    case Success(a) => {
      a ! Msg("hello")
      a ! Msg("falla")
      a ! Msg("hello")
      a ! Msg("falla grave")
    }
    case Failure(ex) => println("Something was wrong: " + ex.getMessage)
  }

  println("Please press any key to exit:")
  try StdIn.readLine()
  finally system.terminate()
}

class SupervisionFatherActor extends Actor {

  override def receive: Receive = {
    case Create(name) => {
      val son = context.actorOf(Props[SupervisionSonActor], name)
      sender() ! son
    }
    case _ => println("Sigo vivo")
  }

  override def supervisorStrategy: SupervisorStrategy =
    AllForOneStrategy(maxNrOfRetries = 10,
      withinTimeRange = FiniteDuration(10, SECONDS)) {

      case _: ArithmeticException => {
        println("Restaring ...")
        Restart
      }
      case _: UnsupportedOperationException => {
        println("Stopping ...")
        Stop
      }
      case _ => {
        println("Resume ...")
        Resume
      }
    }

  override def unhandled(message: Any): Unit = println("unhandled(message)")
}

class SupervisionSonActor extends Actor {

  override def preStart(): Unit = {
    println("preStart()")
    super.preStart()
  }

  override def postStop(): Unit = {
    println("postStop()")
    super.postStop()
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("preRestart(reason, message)")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    println("postRestart(reason)")
    super.postRestart(reason)
  }

  override def receive: Receive = {
    case Msg("falla") => {
      throw new ArithmeticException("Fallo leve")
    }
    case Msg("falla grave") => {
      throw new UnsupportedOperationException("Fallo grave!!")
    }
    case Msg("hello") => println("Sigo vivo")
  }
}
