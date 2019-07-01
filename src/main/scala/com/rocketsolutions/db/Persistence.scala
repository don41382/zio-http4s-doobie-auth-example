package com.rocketsolutions.db

import com.rocketsolutions.config.DBConfig
import com.rocketsolutions.model.User
import doobie.implicits._
import doobie.util.transactor.Transactor
import scalaz.zio._
import scalaz.zio.interop.catz._

trait Persistence {
  val userPersistence: Persistence.Service[Any]
}

object Persistence {

  trait Service[R] {
    def createTable: UIO[Unit]
    def testDatabase: UIO[Unit]
    def listAllUsers: UIO[List[User]]
    def authUser(name: String, password: String): IO[UserError, User]
  }

  trait Live extends Persistence {
    protected def transactor: Transactor[Task]

    override val userPersistence: Service[Any] = new Service[Any] {

      override def createTable: UIO[Unit] =
        sql"""
             | DROP TABLE IF EXISTS Users;
             | CREATE TABLE Users (id int PRIMARY KEY, name varchar, password varchar);
             | INSERT INTO Users VALUES (1, 'Felix', 'password');
             | INSERT INTO Users VALUES (2, 'Klaus', '123456');
           """.stripMargin
        .update
        .run
        .transact(transactor)
        .orDie
        .unit

      override def testDatabase: UIO[Unit] =
        sql"""SELECT 1"""
          .query[Int]
          .unique
          .transact(transactor)
          .orDie
          .unit

      override def listAllUsers: UIO[List[User]] =
        sql"""SELECT id, name FROM Users"""
        .query[User]
        .to[List]
        .transact(transactor)
        .orDie


      override def authUser(name: String, password: String): IO[UserError, User] =
          sql"""SELECT id, name FROM Users u where LOWER(u.name) = ${name.toLowerCase} AND LOWER(u.password) = ${password.toLowerCase}"""
        .query[User]
        .option
        .transact(transactor)
        .orDie
        .flatMap {
          case Some(name)                    => ZIO.succeed(name)
          case None                          => ZIO.fail(UserNotFound)
        }

    }
  }

  def xa(db: DBConfig) = Transactor.fromDriverManager[Task](
    "org.postgresql.Driver", db.url, db.username, db.password
  )

}

