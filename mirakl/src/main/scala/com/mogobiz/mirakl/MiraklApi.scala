/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */
package com.mogobiz.mirakl

import java.util.regex.Pattern

import com.mogobiz.mirakl.PaymentModel._
import com.mogobiz.mirakl.OrderModel._
import spray.http.HttpRequest
import spray.httpx.RequestBuilding._

object MiraklApi {

  val baseUrl = Settings.url

  import JsonSupport._ // pour la conversion en json

  private def completeUrl(url: String, params: List[(String, String)] = Nil): String = {
    if (params.isEmpty) baseUrl + url
    else {
      val keyValue = params.head
      completeUrl(url.replaceAll(Pattern.quote(keyValue._1), keyValue._2), params.tail)
    }
  }

  def PA01(body: OrderPaymentsDto) : HttpRequest = {
    Put(completeUrl("/api/payment/debit"), body)
  }

  def PA02(body: RefundedOrderLinesBean) : HttpRequest = {
    Put(completeUrl("/api/payment/refund"), body)
  }

  def OR01(body: OrderBean) : HttpRequest = {
    Post(completeUrl("/api/orders"), body)
  }

  def SH01(shippingZoneCode: String, offerIdsAndQuantity: List[(Long, Int)]) : HttpRequest = {
    val offers = offerIdsAndQuantity.map( idQuantity => (idQuantity._1.toString + "|" + idQuantity._2.toString) ).mkString(",")
    Get(completeUrl("/api/shipping/fees?shipping_zone_code=" + shippingZoneCode + "&offers=" + offers))
  }
}
