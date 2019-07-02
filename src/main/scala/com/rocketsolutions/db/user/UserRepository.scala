package com.rocketsolutions.db.user

import com.rocketsolutions.db.{UserError}
import com.rocketsolutions.model.User
import scalaz.zio._

trait UserRepository {
  val userPersistence: UserRepository.Service[Any]
}

object UserRepository {

  val defaultUser = List(
    User(1,"Felix","password"),
    User(2,"Klaus","123456")
  )

  trait Service[R] {
    def createTable: UIO[Unit]
    def testDatabase: UIO[Unit]
    def insertUser(u: User): UIO[Unit]
    def listAllUsers: UIO[List[User]]
    def authUser(name: String, password: String): IO[UserError, User]
  }

}

