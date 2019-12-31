package com.gabizou.demo.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

trait MembershipService extends Service {

  def addMember(organizationName: String): ServiceCall[UserId, Membership]

  def getMembership(organizationName: String): ServiceCall[UserId, Membership]

  def removeMember(organizationName: String): ServiceCall[UserId, NotUsed]

  def getMembers(organizationName: String): ServiceCall[NotUsed, Set[User]]

  def getOrganizations(userId: UserId): ServiceCall[NotUsed, Set[Organization]]

  def pruneAllMembershipsFor(userId: UserId): ServiceCall[NotUsed, NotUsed]

  override final def descriptor: Descriptor = {
    import com.lightbend.lagom.scaladsl.api.Service._
    named("membership")
      .withCalls(
        restCall(Method.GET, "/api/organization/:organizationName/members", getMembers _),
        restCall(Method.POST, "/api/organization/:organizationName/members", addMember _),
        restCall(Method.DELETE, "/api/organization/:organizationName/members", removeMember _),
        restCall(Method.GET, "/api/user/:id/memberships", getOrganizations _),
        restCall(Method.DELETE, "/api/user/:id/memberships", pruneAllMembershipsFor _),
        restCall(Method.POST, "/api/organization/:organizationName/member", getMembership _)
      )
      .withAutoAcl(true)
  }
}

case class Membership(val organization:OrganizationId, val user: UserId) {


}

object Membership {
  implicit val format: Format[Membership] = Json.format[Membership]

}