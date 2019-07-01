package com.rocketsolutions.web

import com.rocketsolutions.db.UserNotFound
import com.rocketsolutions.main.{AppEnv, AppTask}
import io.circe.Encoder
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.ServiceErrorHandler
import org.http4s.server.middleware.authentication.BasicAuth
import org.http4s.server.middleware.authentication.BasicAuth.BasicAuthenticator
import scalaz.zio.ZIO
import scalaz.zio.interop.catz._

object Routes {

  implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[AppTask, A] = jsonEncoderOf[AppTask, A]

  val dsl: Http4sDsl[AppTask] = Http4sDsl[AppTask]
  import dsl._

  val listUsers = HttpRoutes.of[AppTask] {
    case GET -> Root =>
      ZIO.accessM(_.userPersistence.listAllUsers.flatMap(users => Ok(users)))
  }

  val secureArea = WebAuth.auth(AuthedRoutes.of[String, AppTask] {
    case GET -> Root as user =>
      Ok(s"Welcome $user to the secure area!")
  })

  case class GlobalHttpError(reason: String)

  def parseError: ServiceErrorHandler[AppTask] = _ => {
    case ex: Exception => InternalServerError(GlobalHttpError(ex.getMessage))
  }
}

object WebAuth {

  val authStore:  BasicAuthenticator[AppTask, String] = (cred: BasicCredentials) =>
    ZIO.access[AppEnv](_.userPersistence.authUser(cred.username, cred.password))
      .flatten
      .fold({
        case UserNotFound => None
      },
        u => Some(u.name)
      )

  val auth = BasicAuth("secure", authStore)
}

