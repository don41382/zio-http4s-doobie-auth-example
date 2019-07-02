package com.rocketsolutions

import com.rocketsolutions.db.user.UserRepository
import scalaz.zio.TaskR
import scalaz.zio.clock.Clock
import scalaz.zio.console.Console

package object main {

  type AppEnv = Clock with UserRepository
  type AppTask[A] = TaskR[AppEnv, A]

}
