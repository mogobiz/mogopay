/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import java.io.File

import org.specs2.mutable.Specification

/**
 *
 */
class HashToolsSpec extends Specification{

  "HashTools" should {
    "generate file MD5 hash" in {
      val file = new File(HashTools.getClass.getResource("document").getPath)
      file exists() must_== true
      val hash = HashTools.generateFileMD5(file).getOrElse("unknown")
      hash  == "gsLEJDmpHKcrIiS3S6HW1Q=="
    }
    "generate file SHA-1 hash" in {
      val file = new File(HashTools.getClass.getResource("document").getPath)
      file exists() must_== true
      val hash = HashTools.generateFileSHA1(file).getOrElse("unknown")
      hash  == "59X5qlB60M22q0YJuqaD8XjSW9Q="
    }
    "generate file SHA-256 hash" in {
      val file = new File(HashTools.getClass.getResource("document").getPath)
      file exists() must_== true
      val hash = HashTools.generateFileSHA256(file).getOrElse("unknown")
      hash  == "XrLMo6Oaah4F1YU9PwYVR9UfBmiTCK19Zs8ZCrLlThQ="
    }
  }

}
