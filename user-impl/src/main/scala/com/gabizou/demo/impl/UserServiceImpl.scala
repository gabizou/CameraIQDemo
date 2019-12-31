package com.gabizou.demo.impl

import java.time.Instant

import akka.NotUsed
import akka.actor.Status.Success
import akka.stream.scaladsl.Source
import com.gabizou.cameraiq.demo.util.UUIDType5
import com.gabizou.demo.api.{User, UserId, UserRegistration, UserService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.apache.log4j.{LogManager, Logger}

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(registry: PersistentEntityRegistry, repo: UserRepository) extends UserService {
  override def lookupUser(userId: UserId): ServiceCall[NotUsed, User] = _ => {
    entityRef.ask(GetUser(userId))
      .flatMap(_ => {
        UserServiceImpl.LOGGER.info(s"Looking up user $userId")
        repo.lookupUser(userId)
      })(ExecutionContext.global)
  }

  override def createUser: ServiceCall[UserRegistration, User] = registrationInfo => {
    val uuid = UUIDType5.nameUUIDFromNamespaceAndString(UUIDType5.NAMESPACE_OID, registrationInfo.email)
    val user = User(UserId(uuid), registrationInfo)
    UserServiceImpl.LOGGER.info(s"Creating new user $registrationInfo")
    repo.saveUser(user)
        .flatMap(saved => {
          entityRef.ask(CreateUser(saved, Instant.now()))
        })(ExecutionContext.global)
  }

  override def getUsers: ServiceCall[NotUsed, Set[User]] = _ => repo.getUsers

  override def getUsersByIds: ServiceCall[Source[UserId, NotUsed], Source[User, NotUsed]] = users => {
    val source = users.watchTermination() { (_, done) =>
      done.onComplete {
        case Success => UserSErviceImpl.LOG
      }

    }
  }

  private def entityRef = registry.refFor[UserEntity](UserServiceImpl.ENTITY_KEY)
}
object UserServiceImpl {
  private val LOGGER: Logger = LogManager.getLogger("UserService")
  private val ENTITY_KEY = classOf[UserServiceImpl].getName
}
