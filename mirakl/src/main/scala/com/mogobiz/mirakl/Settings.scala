/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */
package com.mogobiz.mirakl

import com.typesafe.config.{Config, ConfigFactory}

object Settings {

  lazy val config: Config = ConfigFactory.load("mirakl").withFallback(ConfigFactory.load("default-mirakl"))

  lazy val url = config getString "mirakl.url"
  lazy val frontApiKey = config getString "mirakl.frontApiKey"
  lazy val timeout = config getLong "mirakl.timeout"
}

