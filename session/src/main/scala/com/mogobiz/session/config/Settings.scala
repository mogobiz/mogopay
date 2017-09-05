/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.session.config

import java.io.File

import com.typesafe.config.ConfigFactory

object Settings {
  private val config = ConfigFactory.load("session").withFallback(ConfigFactory.load("default-session"))

  object Session {
    val CookieSecret = config getString "session.cookie.secret"

    val Folder = new File(config getString "session.folder")

    val EsIndex = config getString "session.esindex"

    val CookieName = config getString "session.cookie.name"

    val MaxAge = config getLong "session.maxage"

    val RememberCookieName = config getString "session.remember.cookie.name"

    val RememberCookieMaxAge = config getLong "session.remember.cookie.maxage"

    require(CookieSecret.nonEmpty, "session.secret must be non-empty")
    require(CookieName.nonEmpty, "session.cookie.name must be non-empty")
    require(RememberCookieName.nonEmpty, "session.remember.cookie.name must be non-empty")
  }
}
