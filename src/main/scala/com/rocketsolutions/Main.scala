package com.rocketsolutions

import com.rocketsolutions.config.{AppConfiguration, Configuration}
import com.rocketsolutions.db.user.DoobieUserRepository
import com.rocketsolutions.web.WebServer
import scalaz.zio._
import scalaz.zio.clock.Clock

object Main extends App {

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] = {

    def program(appConf: TaskR[Any, AppConfiguration]) : ZIO[Environment, Throwable, Unit] = for {
      conf <- appConf
      _    <- WebServer
              .server(conf.http)
              .provide(new Clock.Live with DoobieUserRepository {
                 override protected def transactor = DoobieUserRepository.xa(conf.db)
              })
    } yield ()

    program(Configuration.loadLive).foldM(
      err =>
        ZIO.effectTotal(println(s"error during runtime: ${err.getMessage}")) *> ZIO.effectTotal(err.printStackTrace()) *> ZIO.succeed(1),
      res =>
        ZIO.effectTotal(println(res)) *> IO.succeed(0)
    )

  }

}