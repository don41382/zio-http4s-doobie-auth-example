package com.rocketsolutions

import cats.effect.Sync
import cats.implicits._
import org.http4s.Headers
import org.http4s.{EntityDecoder, Headers, Method, Request, Response, Status, Uri}
import org.scalatest.Assertion

class HTTPSpec extends UnitSpec {

  protected def request[F[_]](method: Method, uri: String, headers: Headers = Headers.empty): Request[F] =
    Request(method = method, uri = Uri.fromString(uri).toOption.get, headers = headers)

  protected def check[F[_], A](
    actual: F[Response[F]],
    expectedStatus: Status,
    expectedBody: Option[A]
  )(implicit
    F: Sync[F],
    ev: EntityDecoder[F, A]
  ): F[Unit] =
    for {
      actual <- actual
      _ <- F.delay(assert(actual.status == expectedStatus, s"Status was ${actual.status} instead of $expectedStatus."))
      _ <- expectedBody.fold[F[Assertion]](
        actual.body.compile.toVector.map(s => assert(s.isEmpty)))(
        expected => actual.as[A].map(x => assert(x === expected, s"Body was $x instead of $expected."))
      )
    } yield ()
}
