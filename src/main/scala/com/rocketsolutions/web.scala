package com.rocketsolutions

import com.rocketsolutions.db.Persistence
import scalaz.zio.TaskR
import scalaz.zio.clock.Clock
import scalaz.zio.console.Console

package object main {

  type AppEnv = Console with Clock with Persistence
  type AppTask[A] = TaskR[AppEnv, A]

}
