package com.rocketsolutions.web

import com.rocketsolutions.{HTTPSpec, web}
import com.rocketsolutions.db.user.{DoobieUserRepository, UserRepository}
import com.rocketsolutions.db.user.UserRepository.InMemoryUserRepository
import com.rocketsolutions.main.{AppEnv, AppTask}
import com.rocketsolutions.model.User
import io.circe.generic.auto._
import io.circe.Decoder
import org.http4s
import org.http4s.circe._
import org.http4s.headers.Authorization
import org.http4s.implicits._
import org.http4s.{Status, _}
import scalaz.zio.clock.Clock
import scalaz.zio._
import scalaz.zio.interop.catz._

class RouteSpec extends HTTPSpec {

  val app = WebServer.route.orNotFound

  implicit def jsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[AppTask, A] = jsonOf[AppTask, A]

  def createTable: AppTask[Unit] = ZIO.accessM[UserRepository](_.userPersistence.createTable)

  describe("RouterSpec") {

    it("should list all user") {
      val req = request[AppTask](Method.GET, "/")
      RouteSpec.runWithEnv(
        check[AppTask,List[User]](
          createTable *> app.run(req),
          Status.Ok,
          Some(List(
            User(1,"Felix","password"),
            User(2,"Klaus","123456"))
          ))
      )
    }

    it("should return unauthorized on invalid login") {
      val req = request[AppTask](
        Method.GET,
        "/secure",
        http4s.Headers.of(Authorization(BasicCredentials("Felix", "wrong")))
      )
      RouteSpec.runWithEnv(
        check[AppTask,List[User]](
          createTable *> app.run(req),
          Status.Unauthorized,
          None
        )
      )
    }

    it("should login authorized user") {
      val req = request[AppTask](
        Method.GET,
        "/secure",
        http4s.Headers.of(Authorization(BasicCredentials("Felix", "password")))
      )
      RouteSpec.runWithEnv(
        check[AppTask,String](
          createTable *> app.run(req),
          Status.Ok,
          Some("Welcome Felix to the secure area!")
        )
      )
    }

  }

}

object RouteSpec extends DefaultRuntime {

  def runWithEnv[E, A](task: AppTask[A]) =
    unsafeRun (for {
        ref <- Ref.make(List.empty[User])
        env: AppEnv = new Clock.Live with UserRepository {
          override val userPersistence: UserRepository.Service[Any] = InMemoryUserRepository(ref)
        }
        task <- task.provide(env)
      } yield task
    )


}
