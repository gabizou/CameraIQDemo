package com.gabizou.demo.api

import java.util.UUID

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.deser.PathParamSerializer
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

trait UserService extends Service {

  def lookupUser(userId: UserId): ServiceCall[NotUsed, User]

  def createUser: ServiceCall[UserRegistration, User]

  def getUsers: ServiceCall[NotUsed, Set[User]]

  def getUsersByIds: ServiceCall[Source[UserId, NotUsed], Source[User, NotUsed]]

  override final def descriptor: Descriptor = {
    import Service._
    named("user")
      .withCalls(
        restCall(Method.POST, "/api/user/", createUser _),
        restCall(Method.GET, "/api/user/:uuid", lookupUser _),
        restCall(Method.GET, "/api/user/", getUsers _),
        restCall(Method.POST, "/api/user/", getUsersByIds _)
      )
      .withAutoAcl(true)
  }


}

object UserService {

}

case class User(val userId: UserId, val info: UserRegistration) {

}
object User {
  implicit val format: Format[User] = Json.format[User]
}

case class UserId(val userId: UUID) {

  /**
    * We specifically use the underlying [[UUID]] string representation
    * to synonymously represent the id between a uuid and organization id
    *
    * @return The uuid string representation
    */
  override def toString: String = userId.toString

}

object UserId {
  implicit val format: Format[UserId] = Json.format[UserId]
  implicit val pathParamSerializer: PathParamSerializer[UserId] = PathParamSerializer.required("UserId")(UserId.of)(_.toString)

  def of(s: String) = new UserId(UUID.fromString(s))

}

case class UserRegistration(val firstName:String, val lastName:String, val address:String, val email:String, val phoneNumber:String) {

}

object UserRegistration {
  implicit val format: Format[UserRegistration] = Json.format[UserRegistration]

}