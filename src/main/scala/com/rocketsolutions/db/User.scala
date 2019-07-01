package com.rocketsolutions.db

sealed trait UserError
case object UserNotFound extends UserError