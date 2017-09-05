/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */
package com.mogobiz.mirakl

import java.util.concurrent.TimeUnit

import com.mogobiz.json.JacksonConverter
import com.mogobiz.mirakl.CommonModel.MiraklError
import com.mogobiz.mirakl.OrderModel.{OrderBean, OrderCreatedDTO}
import com.mogobiz.mirakl.PaymentModel.{OrderPaymentsDto, RefundedOrderLinesBean}
import com.mogobiz.mirakl.ShippingModel.{ShopShippingFeesDto, ShippingFeeErrorCode}
import com.mogobiz.system.ActorSystemLocator
import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse, StatusCodes}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

object MiraklClient {

  val frontApiKey = Settings.frontApiKey
  val TIMEOUT = Duration.create(Settings.timeout, TimeUnit.SECONDS)

  implicit val system = ActorSystemLocator.apply()
  import system.dispatcher

  private def defaultHeaders() = {
    (addHeader("Accept", "application/json") ~> addHeader("Authorization", frontApiKey))
  }
  private def basicPipeline: HttpRequest => Future[HttpResponse] = defaultHeaders ~> sendReceive //~> unmarshal[HttpResponse]

  def confimDebit(body: OrderPaymentsDto): Boolean = {
    val responseFuture = basicPipeline {
      MiraklApi.PA01(body)
    }
    val response = Await.result(responseFuture, TIMEOUT)
    response.status == StatusCodes.NoContent
  }

  def confirmRefund(body: RefundedOrderLinesBean): Boolean = {
    val responseFuture = basicPipeline {
      MiraklApi.PA02(body)
    }
    val response = Await.result(responseFuture, TIMEOUT)
    response.status == StatusCodes.NoContent
  }

  def createOrder(body: OrderBean) : Try[OrderCreatedDTO] = {
    val responseFuture = basicPipeline {
      MiraklApi.OR01(body)
    }
    val response = Await.result(responseFuture, TIMEOUT)
    if (response.status == StatusCodes.OK) {
      val json : String = response.entity.asString
      Success(JacksonConverter.deserialize[OrderCreatedDTO](json))
    }
    else {
      val json : String = response.entity.asString
      Failure(new MiraklCreateOrderException(JacksonConverter.deserialize[MiraklError](json)))
    }
  }

  def getShopShippingsFees(shippingZoneCode: String, offerIdsAndQuantity: List[(Long, Int)]) : (Option[ShippingFeeErrorCode.ShippingFeeErrorCode], Option[ShopShippingFeesDto]) = {
    val responseFuture = basicPipeline {
      MiraklApi.SH01(shippingZoneCode, offerIdsAndQuantity)
    }
    val response = Await.result(responseFuture, TIMEOUT)
    val json : String = response.entity.asString
    if (response.status == StatusCodes.OK) {
      (None, Some(JacksonConverter.deserialize[ShopShippingFeesDto](json)))
    }
    else {
      val error = JacksonConverter.deserialize[MiraklError](json)
      if (error.message.startsWith("Invalid value for field 'shippingZoneCode'")) {
        (Some(ShippingFeeErrorCode.SHIPPING_ZONE_NOT_ALLOWED), None)
      }
      else (None, None)
    }
  }
}
