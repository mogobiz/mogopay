/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import com.mogobiz.utils.GlobalUtil._
import org.specs2.mutable.Specification

class GlobalUtilSpec extends Specification {
  "caseClassToMap" should {
    "make a Map out of a case class" in {
      case class A(foo: String, bar: Int)
      val a = A("aze", 10)
      caseClassToMap(a) must havePairs("foo" -> "aze", "bar" -> 10)
    }
  }

  "mapToQueryString" should {
    "make a query string out of a Map" in {
      val m = Map("foo" -> 1, "bar" -> "2")
      mapToQueryString(m) must_== "foo=1&bar=2"
    }
  }

  "hideStringExceptLastN" should {
    "hide the string except the last N characters" in {
      hideStringExceptLastN("abc", -1) must throwAn[IllegalArgumentException]
      hideStringExceptLastN("abc", 0, "X") must_== "XXX"
      hideStringExceptLastN("abc", 0) must_== "***"
      hideStringExceptLastN("abc", 1) must_== "**c"
      hideStringExceptLastN("abc", 2) must_== "*bc"
      hideStringExceptLastN("abc", 3) must_== "abc"
      hideStringExceptLastN("abc", 4) must_== "***"
    }
  }
}