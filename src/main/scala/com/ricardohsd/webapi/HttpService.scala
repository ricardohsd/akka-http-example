package com.ricardohsd.webapi

import akka.actor._
import akka.http.scaladsl.Http
import akka.pattern._
import akka.stream.ActorMaterializer
import akka.util.Timeout

class HttpService(address: String, port: Int, internalTimeout: Timeout, userRepository: ActorRef)
    extends Actor with ActorLogging {
  import context.dispatcher

  private implicit val mat = ActorMaterializer()

  val userService = new UserServiceRoute(userRepository, internalTimeout)

  Http(context.system)
    .bindAndHandle(userService.route, address, port)
    .pipeTo(self)

  override def receive = binding

  private def binding: Receive = {
    case serverBinding @ Http.ServerBinding(address) =>
      log.info(s"Listening on ${address}")

    case Status.Failure(cause) =>
      log.error(cause, s"Can' bind to ${address}:${port}")
      context.stop(self)
  }
}
