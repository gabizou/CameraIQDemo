package com.gabizou.demo.impl

import com.gabizou.demo.api.UserService
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import play.api.libs.ws.ahc.AhcWSComponents
import com.softwaremill.macwire._

class UserLoader extends LagomApplicationLoader {


  override def loadDevMode(context: LagomApplicationContext): LagomApplication = super.loadDevMode(context)

  override def load(context: LagomApplicationContext): LagomApplication = {
    new UserApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }
  }
}

abstract class UserApplication(context: LagomApplicationContext) extends LagomApplication(context)
  with AhcWSComponents
  with CassandraPersistenceComponents {

  override def lagomServer: LagomServer = serverFor[UserService]({
    val repository = wire[UserRepository]
    wire[UserServiceImpl]
  })

  override def jsonSerializerRegistry: JsonSerializerRegistry = UserSerializerRegistry
}
