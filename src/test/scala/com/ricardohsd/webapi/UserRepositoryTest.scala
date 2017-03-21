package com.ricardohsd.webapi

import akka.actor.{ ActorSystem, Props }
import akka.testkit.{ ImplicitSender, TestKit }
import org.scalatest.concurrent.Eventually
import org.scalatest.{ BeforeAndAfterAll, FunSuiteLike }

class UserRepositoryTest extends TestKit(ActorSystem("testSystem"))
    with FunSuiteLike with ImplicitSender with Eventually with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  test("add nonexistent user to the repo") {
    val userRepository = system.actorOf(Props(classOf[UserRepository]))

    userRepository ! UserRepository.AddUser("john")

    eventually {
      expectMsg(UserRepository.UserAdded(UserRepository.User("john")))
    }
  }

  test("add existent user to the repo") {
    val userRepository = system.actorOf(Props(classOf[UserRepository]))

    userRepository ! UserRepository.AddUser("john")
    userRepository ! UserRepository.AddUser("john")

    eventually {
      expectMsg(UserRepository.UserExists("john"))
    }
  }

  test("get users") {
    val userRepository = system.actorOf(Props(classOf[UserRepository]))

    userRepository ! UserRepository.AddUser("john")
    userRepository ! UserRepository.AddUser("mary")
    userRepository ! UserRepository.GetUsers

    eventually {
      expectMsg(Set(UserRepository.User("john"), UserRepository.User("mary")))
    }
  }
}
