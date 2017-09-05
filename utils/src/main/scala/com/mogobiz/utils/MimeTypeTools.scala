/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import java.io.File
import java.util.regex.Pattern

import org.apache.tika.Tika

import scala.util.{Failure, Success, Try}

/**
  *
  */
object MimeTypeTools {

  val FORMAT = Pattern.compile("(.*)\\/(.*)")

  def detectMimeType(file: File): Option[String] = {
    if (file != null && file.exists()) {
      Try(new Tika().detect(file)) match {
        case Success(s) => Some(s)
        case Failure(f) => None
      }
    } else {
      None
    }
  }

  def toFormat(file: File): Option[String] = {
    toFormat(detectMimeType(file))
  }

  def toFormat(mimeType: Option[String]): Option[String] = {
    mimeType match {
      case Some(s) =>
        val matcher = FORMAT.matcher(s)
        if (matcher.find() && matcher.groupCount() > 1) {
          Some(matcher.group(2))
        } else {
          None
        }
      //        s match {
      //          case r"(.*)\/(.*)${format}" => Some(format)
      //          case _ => None
      //        }
      case None => None
    }
  }
}
