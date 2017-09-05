/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */
package com.mogobiz.mirakl

import com.mogobiz.mirakl.CommonModel.{AdditionalFieldType,PaymentWorkflow}
import com.mogobiz.mirakl.PaymentModel.PaymentStatus
import com.mogobiz.mirakl.OrderModel.{OrderState, OrderLineState, RefundState}
import com.mogobiz.mirakl.ShippingModel.ShippingFeeErrorCode
import org.json4s.DefaultFormats
import org.json4s.ext.EnumSerializer
import spray.httpx.Json4sJacksonSupport

object JsonSupport extends Json4sJacksonSupport {

  val miraklJsonFormat = DefaultFormats +
    new EnumSerializer(AdditionalFieldType) +
    new EnumSerializer(PaymentWorkflow) +
    new EnumSerializer(PaymentStatus) +
    new EnumSerializer(OrderState) +
    new EnumSerializer(OrderLineState) +
    new EnumSerializer(RefundState) +
    new EnumSerializer(ShippingFeeErrorCode)

  implicit val json4sJacksonFormats = miraklJsonFormat
}