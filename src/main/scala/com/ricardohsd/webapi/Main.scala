package com.ricardohsd.webapi

import akka.actor._

import scala.concurrent.Await
import scala.concurrent.duration._

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("myuser")

    system.actorOf(Props(new Master), "user-app-master")

    Await.ready(system.whenTerminated, Duration.Inf)
  }
}