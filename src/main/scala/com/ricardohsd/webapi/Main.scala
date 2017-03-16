package com.ricardohsd.webapi

import akka.actor._

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  implicit val system = ActorSystem("myuser")

  system.actorOf(Props(new Master), "user-app-master")

  sys.addShutdownHook({
    Await.ready(system.terminate(), 20 seconds)
  })
}