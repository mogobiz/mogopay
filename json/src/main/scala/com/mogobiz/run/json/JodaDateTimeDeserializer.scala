/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.run.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}

/**
  */
class JodaDateTimeDeserializer extends JsonDeserializer[DateTime] {

  val fmt: DateTimeFormatter = ISODateTimeFormat.dateTimeParser()

  override def deserialize(p1: JsonParser, p2: DeserializationContext): DateTime = {
    JodaDateTimeOptionDeserializer.deserialize(p1.getValueAsString())
  }
}

class JodaDateTimeOptionDeserializer extends JsonDeserializer[Option[DateTime]] {

  override def deserialize(p1: JsonParser, p2: DeserializationContext): Option[DateTime] = {
    JodaDateTimeOptionDeserializer.deserializeAsOption(p1.getValueAsString())
  }

  override def getNullValue: Option[DateTime] = None

}

object JodaDateTimeOptionDeserializer {

  val fmt: DateTimeFormatter = ISODateTimeFormat.dateTimeParser()

  def deserializeAsOption(v: String): Option[DateTime] = {
    if (v != null) Some(fmt.parseDateTime(v).toDateTime(DateTimeZone.UTC))
    else None;
  }

  def deserialize(v: String): DateTime = deserializeAsOption(v).orNull
}
