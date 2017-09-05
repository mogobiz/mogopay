/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import java.util.{Locale, ResourceBundle => JResourceBundle}

import scala.collection.JavaConverters._
import scala.util.Try

class ResourceBundle(baseName: String) {

  protected def bundle(locale: Locale) = JResourceBundle.getBundle(baseName, locale)

  def contains(key: String, locale: Locale) =
    bundle(locale).containsKey(key)

  def get(key: String, locale: Locale): String =
    bundle(locale).getString(key)

  def find(key: String, locale: Locale): Option[String] =
    Try(get(key, locale)).toOption

  def getOrElse(key: String, locale: Locale, default: => String) =
    find(key, locale).getOrElse(default)

  def getWithParams(key: String, locale: Locale, params: Seq[Any]) = {
    val raw = get(key, locale)
    format(raw, params)
  }

  def findWithParams(key: String, locale: Locale, params: Seq[Any]) =
    find(key, locale).map(v => format(v, params))

  def iterator(locale: Locale) =
    bundle(locale).getKeys.asScala

  def keySet(locale: Locale) =
    bundle(locale).keySet.asScala

  protected def format(s: String, params: Seq[Any]) =
    params.zipWithIndex.foldLeft(s) {
      case (res, (value, index)) => res.replace("{" + index + "}", value.toString)
    }
}

object ResourceBundle {
  def apply(baseName: String) =
    new ResourceBundle(baseName)
}
