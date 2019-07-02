package com.rocketsolutions.config

import com.typesafe.config.ConfigFactory
import pureconfig.generic.auto._
import scalaz.zio.{TaskR, ZIO}

import scala.reflect.io.Path

case class DBConfig(url: String, username: String, password: String)
case class HttpConfig(port: Int)

case class AppConfiguration (
  db: DBConfig,
  http: HttpConfig
)

object Configuration {

  def loadLive: TaskR[Any, AppConfiguration] = ZIO.effect(pureconfig.loadConfigOrThrow[AppConfiguration])
  def loadTest: TaskR[Any, AppConfiguration] = ZIO.effect(pureconfig.loadConfigOrThrow[AppConfiguration](ConfigFactory.load("application-test.conf")))
}
