package com.gabizou.demo.impl

import java.time.Instant

import akka.Done
import com.gabizou.demo.api.{User, UserId}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

sealed trait UserCommand[R] extends ReplyType[R] {

}

case class CreateUser(val registration:User, val timestamp: Instant) extends UserCommand[User] {

}
object CreateUser {
  implicit val format: Format[CreateUser] = Json.format[CreateUser]
}

case class GetUser(val userId: UserId) extends UserCommand[Done]

object GetUser {
  implicit val format: Format[GetUser] = Json.format[GetUser]
}