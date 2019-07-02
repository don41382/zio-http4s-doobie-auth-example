package com.rocketsolutions.db.user

import com.rocketsolutions.db.{UserError, UserNotFound}
import com.rocketsolutions.model.User
import scalaz.zio._

trait UserRepository {
  val userPersistence: UserRepository.Service[Any]
}

object UserRepository {

  trait Service[R] {
    def createTable: UIO[Unit]
    def testDatabase: UIO[Unit]
    def listAllUsers: UIO[List[User]]
    def authUser(name: String, password: String): IO[UserError, User]
  }

  case class InMemoryUserRepository(ref: Ref[List[User]]) extends Service[Any] {
    override def createTable: UIO[Unit] = {
      ref.set(List(
        User(1,"Felix","password"),
        User(2,"Klaus","123456")
      ))
    }

    override def testDatabase: UIO[Unit] = ZIO.unit

    override def listAllUsers: UIO[List[User]] = ref.get

    override def authUser(name: String, password: String): IO[UserError, User] =
      ref.get.map(_.find(u => u.name.equalsIgnoreCase(name) && u.password.equalsIgnoreCase(password))).flatMap {
        case Some(user) =>
          IO.succeed(user)
        case None =>
          IO.fail(UserNotFound)
      }
  }
}

