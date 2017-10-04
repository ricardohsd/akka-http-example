package com.ricardohsd.webapi

import akka.actor.Props
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import org.scalatest.{ Matchers, WordSpec }

import scala.concurrent.duration._

class UserServiceRouteTest extends WordSpec with Matchers with ScalatestRouteTest {
  val internalTimeout = 3 seconds

  "Users API" should {
    "Get users from /users" in {
      val userRepository = system.actorOf(Props(classOf[UserRepository]))
      val route = new UserServiceRoute(userRepository, internalTimeout).route

      userRepository ! UserRepository.AddUser("john")

      Get("/users") ~> route ~> check {
        responseEntity shouldEqual HttpEntity(ContentTypes.`application/json`, "[{\"name\":\"john\"}]")
        status shouldEqual StatusCodes.OK
      }
    }

    "Posting to /users should add the user" in {
      val userRepository = system.actorOf(Props(classOf[UserRepository]))
      val route = new UserServiceRoute(userRepository, internalTimeout).route

      val jsonRequest = ByteString(
        s"""
           |{
           |    "name":"Peter"
           |}
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/users",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> route ~> check {
        responseEntity shouldEqual HttpEntity(ContentTypes.`text/plain(UTF-8)`, "OK")
        status shouldEqual StatusCodes.OK
      }
    }

    "Posting an existent user to /users should return a conflict" in {
      val userRepository = system.actorOf(Props(classOf[UserRepository]))
      val route = new UserServiceRoute(userRepository, internalTimeout).route

      userRepository ! UserRepository.AddUser("Anderson")

      val jsonRequest = ByteString(
        s"""
           |{
           |    "name":"Anderson"
           |}
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/users",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> route ~> check {
        responseEntity shouldEqual HttpEntity(ContentTypes.`text/plain(UTF-8)`, "User exists.")
        status shouldBe StatusCodes.Conflict
      }
    }
  }
}
