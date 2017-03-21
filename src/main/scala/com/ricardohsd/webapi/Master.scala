package com.ricardohsd.webapi

import akka.actor.{ Actor, ActorLogging, ActorRef, SupervisorStrategy, Terminated }

import scala.concurrent.duration._

class Master extends Actor with ActorLogging {
  override val supervisorStrategy = SupervisorStrategy.stoppingStrategy

  private val userRepository = context.watch(createUserRepository())

  context.watch(createHttpService(userRepository))

  log.info("System started.")

  override def receive = {
    case Terminated(actor) => onTerminated(actor)
  }

  protected def createUserRepository(): ActorRef = {
    context.actorOf(UserRepository.props(), UserRepository.Name)
  }

  protected def createHttpService(userRepositoryActor: ActorRef): ActorRef = {
    context.actorOf(
      HttpService.props("localhost", 8081, 3 seconds, userRepositoryActor),
      HttpService.Name
    )
  }

  protected def onTerminated(actor: ActorRef): Unit = {
    log.error(s"Terminating the system because ${actor} terminated")
    context.system.terminate()
  }
}
