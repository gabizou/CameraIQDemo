package com.gabizou.demo.api

import java.util.UUID

import akka.NotUsed
import com.gabizou.cameraiq.demo.util.UUIDType5
import com.lightbend.lagom.scaladsl.api.deser.PathParamSerializer
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

trait OrganizationService extends Service {

  /**
    *
    * @param name
    * @return
    */
  def organization(name: String): ServiceCall[NotUsed, Organization]

  def getOrganization(orgId: OrganizationId): ServiceCall[NotUsed, Organization]

  def createOrganization(): ServiceCall[OrganizationRegistration, Organization]

  def getOrganizations(): ServiceCall[NotUsed, Set[Organization]]

  override def descriptor: Descriptor = {
    import Service._
    // @formatter:off

    named("lagom-with-gdpr")
      .withCalls(
        restCall(Method.PUT, "/api/organization/", createOrganization _),
        restCall(Method.POST, "/api/organization/:name", organization _),
        restCall(Method.POST, "/api/organization/", getOrganizations _),
        restCall(Method.POST, "/api/organization/:orgId", getOrganization _)
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}

object OrganizationService {
}

case class OrganizationId(val organizationId: UUID) {

  /**
    * We specifically use the underlying [[UUID]] string representation
    * to synonymously represent the id between a uuid and organization id
    *
    * @return The uuid string representation
    */
  override def toString: String = organizationId.toString

}

object OrganizationId {
  implicit val format: Format[OrganizationId] = Json.format[OrganizationId]
  implicit val pathParamSerializer: PathParamSerializer[OrganizationId] = PathParamSerializer.required("OrganizationId")(OrganizationId.of)(_.toString)

  def of(s: String): OrganizationId = new OrganizationId(UUID.fromString(s))

}

case class OrganizationRegistration(val name: String, val address: String, val phoneNumber: String) {

  def getEntityId(): String = UUIDType5.nameUUIDFromNamespaceAndString(UUIDType5.NAMESPACE_OID, this.name).toString

  override def equals(other: Any): Boolean = other match {
    case that: OrganizationRegistration =>
      name == that.name &&
        address == that.address &&
        phoneNumber == that.phoneNumber
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(name, address, phoneNumber)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = s"OrganizationRegistration($name)"

}

object OrganizationRegistration {
  implicit val format: Format[OrganizationRegistration] = Json.format[OrganizationRegistration]
}

/**
  * Model representation of an Organization for passing around
  * through different service calls with the [[OrganizationService]].
  * Immutable by nature, we allow separating the id from the orgainzation
  * information, allowing for organization renames, information updates,
  * while retaining an immutable [[java.util.UUID]] for it's references
  * throughout the service/system.
  *
  * @param orgId
  * @param info
  */
case class Organization(val orgId: OrganizationId, val info: OrganizationRegistration) {

  override def equals(other: Any): Boolean = other match {
    case that: Organization =>
      orgId == that.orgId &&
        info == that.info
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(orgId, info)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = s"Organization($orgId, ${info.name})"

}

object Organization {
  implicit val format: Format[Organization] = Json.format[Organization]
}
