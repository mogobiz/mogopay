/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.pay.common

import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}
import com.mogobiz.run.json.{JodaDateTimeOptionDeserializer, JodaDateTimeOptionSerializer}
import org.joda.time.DateTime

case class CartRate(code: String,
  numericCode: Int,
  rate: Double = 0.01,
  fractionDigits: Int = 2)

case class Cart(count: Int,
                rate: CartRate,
                price: Long = 0,
                endPrice: Long = 0,
                taxAmount: Long = 0,
                reduction: Long = 0,
                finalPrice: Long = 0,
                shippingRulePrice: Option[Long] = None,
                shopCarts: List[ShopCart] = Nil,
                customs: Map[String, Any] = Map(),
                compagnyAddress: Option[CompanyAddress] = None)

case class ShopCart(shopId: String,
                    rate: CartRate,
                    price: Long = 0,
                    endPrice: Long = 0,
                    taxAmount: Long = 0,
                    reduction: Long = 0,
                    finalPrice: Long = 0,
                    cartItems: List[CartItem] = Nil,
                    coupons: List[Coupon] = Nil,
                    customs: Map[String, Any] = Map())

case class CartItem(id: String,
    name: String,
    picture: String,
    shopUrl: String,
    quantity: Int,
    price: Long,
    endPrice: Long,
    tax: Float,
    taxAmount: Long = 0,
    totalPrice: Long,
    totalEndPrice: Long,
    totalTaxAmount: Long = 0,
    salePrice: Long,
    saleEndPrice: Long,
    saleTaxAmount: Long = 0,
    saleTotalPrice: Long,
    saleTotalEndPrice: Long,
    saleTotalTaxAmount: Long = 0,
    registeredCartItems: List[RegisteredCartItem],
    shipping: Option[Shipping],
    downloadableLink: String,
    externalCode: Option[ExternalCode],
    customs: Map[String, Any] = Map()) {

  val isExternalItem = externalCode.nonEmpty

  def isExternalItemFor(provider: ExternalProvider.ExternalProvider) = {
    externalCode.map {_.provider == provider}.getOrElse(false)
  }
}

case class Coupon(code: String,
  @JsonSerialize(using = classOf[JodaDateTimeOptionSerializer])@JsonDeserialize(using = classOf[JodaDateTimeOptionDeserializer]) startDate: Option[DateTime] = None,
  @JsonSerialize(using = classOf[JodaDateTimeOptionSerializer])@JsonDeserialize(using = classOf[JodaDateTimeOptionDeserializer]) endDate: Option[DateTime] = None,
  price: Long = 0,
  customs: Map[String, Any] = Map())

case class Shipping(weight: Long,
                    weightUnit: String,
                    width: Long,
                    height: Long,
                    depth: Long,
                    linearUnit: String,
                    amount: Long,
                    free: Boolean,
                    customs: Map[String, Any] = Map()){

  val isDefine = !(height == 0 || width == 0 || weight == 0 || weightUnit == null || weightUnit.isEmpty || linearUnit == null || linearUnit.isEmpty)
}

case class ShippingWithQuantity(quantity: Long, shipping: Shipping)

case class RegisteredCartItem(id: String,
  email: String,
  firstname: Option[String] = None,
  lastname: Option[String] = None,
  phone: Option[String] = None,
  @JsonSerialize(using = classOf[JodaDateTimeOptionSerializer])@JsonDeserialize(using = classOf[JodaDateTimeOptionDeserializer]) birthdate: Option[DateTime] = None,
  qrCodeContent: Option[String] = None,
  customs: Map[String, Any])

case class CompanyAddress(company: String,
                          road: String,
                          road2: String,
                          city: String,
                          zipCode: String,
                          country: String,
                          state: Option[String],
                          phone: Option[String] = None,
                          shippingInternational: Boolean)

case class ExternalCode(val provider: ExternalProvider.ExternalProvider, val code: String)

object ExternalCode {
  def fromString(externalCode: Option[String]) : Option[ExternalCode] = {
    externalCode.map { externalCode =>
      val index = externalCode.indexOf("::")
      if (index > 0) {
        val provider = externalCode.substring(0, index).toUpperCase
        val code = externalCode.substring(index + 2)
        Some(ExternalCode(provider, code))
      }
      else None
    }.getOrElse(None)
  }
  def toString(externalCodes: Option[ExternalCode]) : Option[String] = {
    externalCodes.map {externalCode: ExternalCode => (externalCode.provider + "::" + externalCode.code)}
  }
}

object ExternalProvider {
  type ExternalProvider = String

  val MIRAKL = "MIRAKL"
}