/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.session

import javax.crypto._
import javax.crypto.spec.SecretKeySpec

trait Crypto {

  def sign(message: String, key: Array[Byte]): String = {
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(new SecretKeySpec(key, "HmacSHA1"))
    Codecs.toHexString(mac.doFinal(message.getBytes("utf-8")))
  }

  def sign(message: String, secret: String): String =
    sign(message, secret.getBytes("utf-8"))

}

object Crypto extends Crypto
