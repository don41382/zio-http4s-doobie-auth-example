package com.rocketsolutions.config

import pureconfig.generic.auto._
import scalaz.zio.{TaskR, ZIO}

case class DBConfig(url: String, username: String, password: String)
case class HttpConfig(port: Int)

case class AppConfiguration (
  db: DBConfig,
  http: HttpConfig
)

object Configuration {

  def load: TaskR[Any, AppConfiguration] = ZIO.effect(pureconfig.loadConfigOrThrow[AppConfiguration])

}
