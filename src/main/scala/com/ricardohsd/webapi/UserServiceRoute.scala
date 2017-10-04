package com.ricardohsd.webapi

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.ExecutionContext

class UserServiceRoute(userRepository: ActorRef, internalTimeout: Timeout)(implicit executionContext: ExecutionContext)
    extends Directives {
  import de.heikoseeberger.akkahttpcirce.CirceSupport._
  import io.circe.generic.auto._

  implicit val timeout = internalTimeout

  val route = pathPrefix("users") { usersGetAll ~ userPost }

  def usersGetAll = get {
    complete {
      (userRepository ? UserRepository.GetUsers).mapTo[Set[UserRepository.User]]
    }
  }

  def userPost = post {
    entity(as[UserRepository.User]) { user =>
      onSuccess(userRepository ? UserRepository.AddUser(user.name)) {
        case UserRepository.UserAdded(_) => complete(StatusCodes.OK)
        case UserRepository.UserExists(_) => complete(StatusCodes.Conflict, "User exists.")
      }
    }
  }
}
