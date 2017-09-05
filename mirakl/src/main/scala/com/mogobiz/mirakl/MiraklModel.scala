/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */
package com.mogobiz.mirakl

import java.util.Date

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

object CommonModel {

  object AdditionalFieldType extends Enumeration {
    type AdditionalFieldType = Value

    val STRING = Value("STRING")
    val DATE = Value("DATE")
    val NUMERIC = Value("NUMERIC")
    val BOOLEAN = Value("BOOLEAN")
    val LINK = Value("LINK")
    val REGEX = Value("REGEX")
    val TEXTAREA = Value("TEXTAREA")
    val LIST = Value("LIST")
    val MULTIPLE_VALUES_LIST = Value("MULTIPLE_VALUES_LIST")
  }
  class AdditionalFieldTypeRef extends TypeReference[AdditionalFieldType.type]

  object PaymentWorkflow extends Enumeration {
    type PaymentWorkflow = Value

    val PAY_ON_ACCEPTANCE = Value("PAY_ON_ACCEPTANCE")
    val PAY_ON_DELIVERY = Value("PAY_ON_DELIVERY")
  }
  class PaymentWorkflowRef extends TypeReference[PaymentWorkflow.type]

  case class MiraklError(message: String, status: Long)

  case class Channel(code: String, label: String)

  case class Address(city: String,
                     civility: Option[String],
                     company: Option[String],
                     country: String,
                     country_iso_code: String,
                     firstname: Option[String],
                     lastname: String,
                     phone: Option[String],
                     phone_secondary: Option[String],
                     state: Option[String],
                     street_1: String,
                     street_2: Option[String],
                     zip_code: Option[String])

  case class ShippingAddress(city: String,
                             civility: Option[String],
                             company: Option[String],
                             country: String,
                             country_iso_code: String,
                             firstname: Option[String],
                             lastname: String,
                             phone: Option[String],
                             phone_secondary: Option[String],
                             state: Option[String],
                             street_1: String,
                             street_2: Option[String],
                             zip_code: Option[String],
                             additional_info: Option[String],
                             internal_additional_info: Option[String])

  case class Customer(customer_id: String,
                      civility: Option[String],
                      firstname: String,
                      lastname: String,
                      email: String,
                      locale: Option[String],
                      billing_address: Address,
                      shipping_address: ShippingAddress)

  case class AdditionalFieldWithType(code: String,
                                     @JsonScalaEnumeration(classOf[AdditionalFieldTypeRef]) `type`: Option[AdditionalFieldType.AdditionalFieldType],
                                     value: String)

  case class Range(price: BigDecimal, quantity_threshold: Long)

  case class Tax(amount: BigDecimal, code: String)

  case class ShippingTax(amount: BigDecimal, code: String)

  case class OfferDiscout(discount_price: Option[BigDecimal],
                          end_date: Option[Date],
                          origin_price: BigDecimal,
                          ranges: Option[List[Range]],
                          start_date: Option[Date])
}

object PaymentModel {
  import com.mogobiz.mirakl.CommonModel._

  object PaymentStatus extends Enumeration {
    type PaymentStatus = Value

    val OK = Value("OK")
    val REFUSED = Value("REFUSED")
  }
  class PaymentStatusRef extends TypeReference[PaymentStatus.type]

  case class OrderPaymentsDto(orders: List[OrderPayment])

  case class OrderPayment(order_id: String,
                          customer_id: String,
                          @JsonScalaEnumeration(classOf[PaymentStatusRef]) payment_status: PaymentStatus.PaymentStatus,
                          amount: Option[BigDecimal] = None,
                          currency_iso_code: Option[String] = None,
                          transaction_date: Option[Date] = None,
                          transaction_number: Option[String] = None)

  case class RefundedOrderLinesBean(refunds: List[Refund])

  case class Refund(amount: Option[BigDecimal] = None,
                    currency_iso_code: Option[String] = None,
                    @JsonScalaEnumeration(classOf[PaymentStatusRef]) payment_status: PaymentStatus.PaymentStatus,
                    refund_id: String,
                    transaction_date: Option[Date] = None,
                    transaction_number: Option[String] = None)

  case class DebitOrderList(order: List[DebitOrder])

  case class DebitOrder(amount: Option[BigDecimal],
                        customer_id: String,
                        currency_iso_code: Option[String],
                        order_commercial_id: Option[String],
                        order_id: Option[String],
                        payment_workflow: Option[PaymentWorkflow.PaymentWorkflow],
                        shop_id: Option[String],
                        order_lines: Option[DebitOrderLineList])

  case class DebitOrderLineList(order_line: List[DebitOrderLine])

  case class DebitOrderLine(order_line_amount: BigDecimal,
                            order_line_quantity: Long,
                            offer_id: String,
                            order_line_id: String)

  case class RefundOrderList(order: List[RefundOrder])

  case class RefundOrder(amount: Option[BigDecimal],
                        customer_id: String,
                        currency_iso_code: Option[String],
                        order_commercial_id: Option[String],
                        order_id: Option[String],
                        payment_workflow: Option[PaymentWorkflow.PaymentWorkflow],
                        shop_id: Option[String],
                        order_lines: Option[RefundOrderLineList])

  case class RefundOrderLineList(order_line: List[RefundOrderLine])

  case class RefundOrderLine(order_line_amount: BigDecimal,
                            @Deprecated
                            order_line_quantity: Long,
                            offer_id: String,
                            order_line_id: String,
                            refunds: Option[RefundOrderLineRefundList])

  case class RefundOrderLineRefundList(refund: List[RefundOrderLineRefund])

  case class RefundOrderLineRefund(amount: BigDecimal, id: String)
}

object OrderModel {
  import com.mogobiz.mirakl.CommonModel._

  object OrderState extends Enumeration {
    type OrderState = Value

    val STAGING = Value("STAGING")
    val WAITING_ACCEPTANCE = Value("WAITING_ACCEPTANCE")
    val WAITING_DEBIT = Value("WAITING_DEBIT")
    val WAITING_DEBIT_PAYMENT = Value("WAITING_DEBIT_PAYMENT")
    val SHIPPING = Value("SHIPPING")
    val SHIPPED = Value("SHIPPED")
    val TO_COLLECT = Value("TO_COLLECT")
    val RECEIVED = Value("RECEIVED")
    val CLOSED = Value("CLOSED")
    val REFUSED = Value("REFUSED")
    val CANCELED = Value("CANCELED")
  }
  class OrderStateRef extends TypeReference[OrderState.type]

  object OrderLineState extends Enumeration {
    type OrderLineState = Value

    val STAGING = Value("STAGING")
    val WAITING_ACCEPTANCE = Value("WAITING_ACCEPTANCE")
    val WAITING_DEBIT = Value("WAITING_DEBIT")
    val WAITING_DEBIT_PAYMENT = Value("WAITING_DEBIT_PAYMENT")
    val SHIPPING = Value("SHIPPING")
    val SHIPPED = Value("SHIPPED")
    val TO_COLLECT = Value("TO_COLLECT")
    val RECEIVED = Value("RECEIVED")
    val CLOSED = Value("CLOSED")
    val REFUSED = Value("REFUSED")
    val CANCELED = Value("CANCELED")
    val INCIDENT_OPEN = Value("INCIDENT_OPEN")
    @Deprecated
    val WAITING_REFUND = Value("WAITING_REFUND")
    @Deprecated
    val WAITING_REFUND_PAYMENT = Value("WAITING_REFUND_PAYMENT")
    val REFUNDED = Value("REFUNDED")
  }
  class OrderLineStateRef extends TypeReference[OrderLineState.type]

  object RefundState extends Enumeration {
    type RefundState = Value

    val WAITING_REFUND = Value("WAITING_REFUND")
    val WAITING_REFUND_PAYMENT = Value("WAITING_REFUND_PAYMENT")
    val REFUNDED = Value("REFUNDED")
  }
  class RefundStateRef extends TypeReference[RefundState.type]

  case class OrderBean(channel_code: Option[String] = None,
                       commercial_id: String,
                       customer: Customer,
                       offers: List[Offer],
                       order_additional_fields: List[AdditionalFieldWithType],
                       payment_info: Option[PaymentInfo],
                       @JsonScalaEnumeration(classOf[PaymentWorkflowRef]) payment_workflow: Option[PaymentWorkflow.PaymentWorkflow],
                       scored: Boolean,
                       shipping_zone_code: String)

  case class PaymentInfo(imprint_number: Option[String], payment_type: Option[String])

  case class Offer(currency_iso_code: String,
                   leadtime_to_ship: Option[Integer],
                   offer_id: Long,
                   offer_price: BigDecimal,
                   order_line_additional_fields: List[AdditionalFieldWithType],
                   order_line_id: Option[String], // should be unique
                   price: BigDecimal, // = offer_price * quantity
                   quantity: Integer,
                   shipping_price: BigDecimal,
                   shipping_type_code: String,
                   shipping_taxes: List[ShippingTax],
                   taxes: List[Tax])

  case class OrderCreatedDTO(offers_not_shippable: List[OfferNotShippable],
                             orders: List[OrderCreated])

  case class OfferNotShippable(error_code: String, offer_id: Long)

  @JsonIgnoreProperties(ignoreUnknown = true)
  case class OrderCreated(acceptance_decision_date: Option[Date],
                          can_cancel: Boolean,
                          can_evaluate: Boolean,
                          channel: Option[Channel],
                          commercial_id: String,
                          created_date: Date,
                          currency_iso_code: String,
                          customer: Customer,
                          customer_debited_date: Option[Date],
                          has_customer_message: Boolean,
                          has_incident: Boolean,
                          has_invoice: Boolean,
                          imprint_number: String,
                          last_updated_date: Date,
                          leadtime_to_ship: Integer,
                          order_additional_fields: List[AdditionalFieldWithType],
                          order_id: String,
                          order_lines: List[OrderLine],
                          @JsonScalaEnumeration(classOf[OrderStateRef]) order_state: OrderState.OrderState,
                          order_state_reason_code: Option[String],
                          order_state_reason_label: Option[String],
                          payment_type: String,
                          @JsonScalaEnumeration(classOf[PaymentWorkflowRef]) payment_workflow: PaymentWorkflow.PaymentWorkflow,
                          price: BigDecimal,
                          quote_id: Option[String],
                          shipping_carrier_code: Option[String],
                          shipping_company: Option[String],
                          shipping_price: BigDecimal,
                          shipping_tracking: Option[String],
                          shipping_tracking_url: Option[String],
                          shipping_type_code: String,
                          shipping_type_label: String,
                          shipping_zone_code: String,
                          shipping_zone_label: String,
                          shop_id: Long,
                          shop_name: String,
                          total_commission: BigDecimal,
                          total_price: BigDecimal)

  @JsonIgnoreProperties(ignoreUnknown = true)
  case class OrderLine(can_open_incident: Boolean,
                       can_refund: Boolean,
                       cancelations: List[Cancelation],
                       category_code: String,
                       category_label: String,
                       commission_fee: BigDecimal,
                       commission_taxes: List[CommissionTax],
                       created_date: Date,
                       debited_date: Option[Date],
                       description: String,
                       last_updated_date: Date,
                       offer_id: Long,
                       offer_sku: String,
                       offer_state_code: String,
                       order_line_additional_fields: List[AdditionalFieldWithType],
                       order_line_id: String,
                       order_line_index: Int,
                       @JsonScalaEnumeration(classOf[OrderLineStateRef]) order_line_state: OrderLineState.OrderLineState,
                       order_line_state_reason_code: Option[String],
                       order_line_state_reason_label: Option[String],
                       price: BigDecimal,
                       price_additional_info: Option[String],
                       price_unit: BigDecimal,
                       product_medias: List[ProductMedia],
                       product_sku: String,
                       product_title: String,
                       quantity: Int,
                       received_date: Option[Date],
                       refunds: List[OrderLineRefund],
                       shipped_date: Option[Date],
                       shipping_price: BigDecimal,
                       shipping_taxes: List[ShippingTax],
                       total_commission: BigDecimal,
                       total_price: BigDecimal)

  case class OrderLineRefund(amount: Option[BigDecimal],
                             commission_amount: BigDecimal,
                             commission_taxes: List[CommissionTax],
                             commission_total_amount: BigDecimal,
                             created_date: Date,
                             id: String,
                             quantity: Option[Int],
                             reason_code: String,
                             shipping_amount: Option[BigDecimal],
                             shipping_taxes: List[ShippingTax],
                             @JsonScalaEnumeration(classOf[RefundStateRef]) state: RefundState.RefundState,
                             taxes: List[Tax],
                             transaction_date: Date,
                             transaction_number: String)

  case class CommissionTax(amount: BigDecimal,
                           code: String,
                           rate: BigDecimal,
                           created_date: Date)

  case class Cancelation(amount: Option[BigDecimal],
                         commission_amount: BigDecimal,
                         commission_taxes: List[CommissionTax],
                         commission_total_amount: BigDecimal,
                         created_date: Date,
                         id: String,
                         quantity: Option[Long],
                         reason_code: String,
                         shipping_amount: Option[BigDecimal],
                         shipping_taxes: List[ShippingTax],
                         taxes: List[Tax])

  case class ProductMedia(media_url: String,
                          mime_type: String,
                          `type`: String)
}

object ShippingModel {

  import com.mogobiz.mirakl.CommonModel._

  object ShippingFeeErrorCode extends Enumeration {
    type ShippingFeeErrorCode = Value

    val SHIPPING_ZONE_NOT_ALLOWED = Value("SHIPPING_ZONE_NOT_ALLOWED")
    val SHIPPING_TYPE_NOT_ALLOWED = Value("SHIPPING_TYPE_NOT_ALLOWED")
    val OFFER_NOT_FOUND = Value("OFFER_NOT_FOUND")
  }
  class ShippingFeeErrorCodeRef extends TypeReference[ShippingFeeErrorCode.type]

  case class ShopShippingFeesDto(offers_not_found: List[Long],
                                 shops: List[ShopShippingFees],
                                 total_count: Long)

  @JsonIgnoreProperties(ignoreUnknown = true)
  case class ShopShippingFees(currency_iso_code: String,
                              @JsonScalaEnumeration(classOf[ShippingFeeErrorCodeRef]) error_code: Option[ShippingFeeErrorCode.ShippingFeeErrorCode],
                              error_message: String,
                              leadtime_to_ship: Long,
                              offers: List[Offer],
                              selected_shipping_type: SelectedShippingType,
                              shipping_types: List[ShopShippingType],
                              shop_id: Long,
                              shop_name: String)

  case class SelectedShippingType(code: String,
                                  label: String,
                                  shipping_additional_fields: List[AdditionalFieldWithType])

  case class OfferShippingType(code: String,
                          label: String,
                          line_only_shipping_price: BigDecimal,
                          line_only_total_price: BigDecimal,
                          line_shipping_price: BigDecimal,
                          line_total_price: BigDecimal,
                          shipping_price_additional_unit: BigDecimal,
                          shipping_price_unit: BigDecimal)

  case class ShopShippingType(code: String,
                              label: String,
                              shipping_additional_fields: List[AdditionalFieldWithType],
                              total_shipping_price: BigDecimal)

  @JsonIgnoreProperties(ignoreUnknown = true)
  case class Offer(allow_quote_requests: Boolean,
                   line_only_shipping_price: BigDecimal,
                   line_only_total_price: BigDecimal,
                   line_original_quantity: Long,
                   line_price: BigDecimal,
                   line_quantity: Long,
                   line_shipping_price: BigDecimal,
                   line_total_price: BigDecimal,
                   offer_additional_fields: List[AdditionalFieldWithType],
                   offer_discount: Option[OfferDiscout],
                   offer_id: Long,
                   offer_price: BigDecimal,
                   offer_quantity: Long,
                   product_category_code: String,
                   selected_shipping_type: SelectedShippingType,
                   shipping_price_additional_unit: BigDecimal,
                   shipping_price_unit: BigDecimal,
                   shipping_types: List[OfferShippingType])
}
