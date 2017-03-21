package com.ricardohsd.webapi

import akka.actor.{ ActorSystem, PoisonPill, Props }
import akka.testkit.{ ImplicitSender, TestKit, TestProbe }
import org.scalatest.FunSuiteLike
import org.scalatest.concurrent.Eventually

class HttpServiceCrash extends TestKit(ActorSystem())
    with FunSuiteLike with ImplicitSender with Eventually {

  test("terminates Master actor when HttpService crashes") {
    val testProbe = TestProbe()
    val masterRef = system.actorOf(Props(new Master), "user-app-master")
    val httpService = system.actorSelection("/user/user-app-master/http-service")

    testProbe.watch(masterRef)

    httpService ! PoisonPill

    eventually {
      testProbe.expectTerminated(masterRef)
    }
  }
}

class UserRepositoryCrash extends TestKit(ActorSystem())
    with FunSuiteLike with ImplicitSender with Eventually {

  test("terminates Master actor when UserRepository crashes") {
    val testProbe = TestProbe()
    val masterRef = system.actorOf(Props(new Master), "user-app-master")
    val userRepository = system.actorSelection("/user/user-app-master/user-repository")

    testProbe.watch(masterRef)

    userRepository ! PoisonPill

    eventually {
      testProbe.expectTerminated(masterRef)
    }
  }
}
