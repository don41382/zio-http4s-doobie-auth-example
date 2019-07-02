package com.rocketsolutions.web

import cats.data.OptionT
import cats.effect.ExitCode
import com.rocketsolutions.config.HttpConfig
import com.rocketsolutions.main.{AppEnv, AppTask}
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import scalaz.zio.{ZIO, _}
import scalaz.zio.interop.catz._


object WebServer {

  val dsl: Http4sDsl[AppTask] = Http4sDsl[AppTask]

  def route: HttpRoutes[AppTask] = Router(
      "/" -> Routes.listUsers,
      "/secure" -> Routes.secureArea
    ).mapF { case OptionT(task) => OptionT(squashDefects(task)) }

  def server(conf: HttpConfig): ZIO[AppEnv, Throwable, Unit] =
    ZIO.runtime[AppEnv].flatMap { implicit rts => for {
      _    <- ZIO.accessM[AppEnv](_.userPersistence.createTable)
      _    <- BlazeServerBuilder[AppTask]
                .bindHttp(conf.port, "0.0.0.0")
                .withHttpApp(route.orNotFound)
                .withServiceErrorHandler(Routes.parseError)
                .serve
                .compile[AppTask, AppTask, ExitCode]
                .drain
    } yield ()
  }

  def squashDefects(task: ZIO[AppEnv, Throwable, Option[Response[AppTask]]]): ZIO[AppEnv, Throwable, Option[Response[AppTask]]] =
    task.sandbox.foldM(
      ex => ZIO.effectTotal(println(ex.prettyPrint)) *> ZIO.fail(ex.squash),
      ZIO.succeed)

}