package com.ricardohsd.webapi

import akka.actor._
import akka.pattern._
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout

import scala.concurrent.ExecutionContext

object HttpService {
  final val Name = "http-service"

  def props(address: String, port: Int, internalTimeout: Timeout, userRepository: ActorRef): Props = {
    Props(new HttpService(address, port, internalTimeout, userRepository))
  }

  private def route(httpService: ActorRef, address: String, port: Int, internalTimeout: Timeout,
                    userRepository: ActorRef, system: ActorSystem)(implicit ec: ExecutionContext, mat: Materializer) = {
    import akka.http.scaladsl.server.Directives._
    import io.circe.generic.auto._

    new UserService(userRepository, internalTimeout).route
  }
}

class HttpService(address: String, port: Int, internalTimeout: Timeout, userRepository: ActorRef)
  extends Actor with ActorLogging {
  import HttpService._
  import context.dispatcher

  private implicit val mat = ActorMaterializer()

  Http(context.system)
    .bindAndHandle(route(self, address, port, internalTimeout, userRepository, context.system), address, port)
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
