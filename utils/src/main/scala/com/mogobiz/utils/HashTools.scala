/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import java.io._

import org.apache.commons.io.input.ReaderInputStream

/**
  *
  */
object HashTools {

  private val BufferSize = 8192

  def generateFileMD5(file: File): Option[String] = {
    hashFile(file, "MD5")
  }

  def generateFileSHA1(file: File): Option[String] = {
    hashFile(file, "SHA-1")
  }

  def generateFileSHA256(file: File): Option[String] = {
    hashFile(file, "SHA-256")
  }

  def hashFile(file: File, algorithm: String = "MD5"): Option[String] = {
    if (file.exists()) {
      val stream = new BufferedInputStream(new FileInputStream(file))
      hashStream(stream, algorithm)
    } else {
      None
    }
  }

  def hashString(data: String, algorithm: String = "MD5"): Option[String] = {
    val stream = new ByteArrayInputStream(data.getBytes("UTF-8"))
    hashStream(stream, algorithm)
  }

  def hashStream(stream: InputStream, algorithm: String = "MD5"): Option[String] = {
    import java.security.{MessageDigest, DigestInputStream}
    val digest = MessageDigest.getInstance(algorithm)
    try {
      val dis    = new DigestInputStream(stream, digest)
      val buffer = new Array[Byte](BufferSize)
      while (dis.read(buffer) >= 0) {}
      dis.close()
      Some(Base64.encodeBytes(digest.digest()))
    } finally {
      stream.close()
    }
  }
}
