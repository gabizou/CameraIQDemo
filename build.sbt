
organization in ThisBuild := "com.gabizou.demo"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.13.0"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8" % Test

lazy val `lagom-with-gdpr` = (project in file("."))
  .aggregate(`demo-utils`, `membership-api`, `membership-impl`, `user-api`, `user-impl`, `organization-api`, `organization-impl`, `common-events`)
lazy val `demo-utils` = (project in file("demo-utils"))

//
lazy val `user-api` = (project in file("user-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`demo-utils`)
lazy val `organization-api` = (project in file("organization-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`demo-utils`)
lazy val `membership-api` = (project in file("membership-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
    )
  )
  .dependsOn(`demo-utils`, `user-api`, `organization-api`)
lazy val `common-events` = (project in file("common-events"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslApi
    )
  )
  .dependsOn(`demo-utils`, `user-api`, `organization-api`,`membership-api`)

lazy val `user-impl` = (project in file("user-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`user-api`, `common-events`)

lazy val `organization-impl` = (project in file("organization-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`organization-api`, `common-events`)
lazy val `membership-impl` = (project in file("membership-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`membership-api`, `common-events`)
