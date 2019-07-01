package com.rocketsolutions

import com.rocketsolutions.config.Configuration
import com.rocketsolutions.db.Persistence
import com.rocketsolutions.web.WebServer
import scalaz.zio._
import scalaz.zio.clock.Clock
import scalaz.zio.console.Console

object Main extends App {

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] = {

    val program: ZIO[Environment, Throwable, Unit] = for {
      conf <- Configuration.load
      _    <- WebServer
              .server(conf.http)
              .provideSome[Environment](_ => new Console.Live with Clock.Live with Persistence.Live {
                 override protected def transactor = Persistence.xa(conf.db)
              })
    } yield ()

    program.foldM(
      err =>
        ZIO.effectTotal(println(s"error during runtime: ${err.getMessage}")) *> ZIO.effectTotal(err.printStackTrace()) *> ZIO.succeed(1),
      res =>
        ZIO.effectTotal(println(res)) *> IO.succeed(0)
    )

  }

}