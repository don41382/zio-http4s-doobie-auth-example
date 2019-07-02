name := "zio-http4s-doobie-auth-example"

version := "0.1"
scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding",
  "UTF-8",
  "-Xlint",
  "-Xverify",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings",
  "-Xlint:-unused",
  "-language:_"
)

resolvers ++= Seq(
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Secured Central Repository" at "https://repo1.maven.org/maven2",
  Resolver.sonatypeRepo("snapshots")
)

val Http4sVersion = "0.21.0-M1"
val CirceVersion = "0.12.0-M3"
val ZIOVersion = "1.0-RC5"
val ScalaTestVersion  = "3.0.5"

libraryDependencies ++= Seq(
  // ZIO
  "org.scalaz" %% "scalaz-zio" % ZIOVersion,
  "org.scalaz" %% "scalaz-zio-interop-cats" % ZIOVersion,
  // http4s
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  // Circe
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-generic-extras" % CirceVersion,
  // log4j
  "org.slf4j" % "slf4j-log4j12" % "1.7.26",
  // doobie
  "org.tpolecat" %% "doobie-core"      % "0.7.0",
  "org.tpolecat" %% "doobie-postgres"  % "0.7.0",
  // config
  "com.github.pureconfig" %% "pureconfig" % "0.11.1",
  // test
  "org.scalatest" %% "scalatest" % ScalaTestVersion % "test",
)