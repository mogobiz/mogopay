/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */
package com.mogobiz.mirakl

import java.util.{Locale, UUID}

import com.mogobiz.mirakl.CommonModel._
import com.mogobiz.mirakl.OrderModel._
import com.mogobiz.system.ActorSystemLocator
import org.specs2.mutable._
import spray.testkit.Specs2RouteTest

import scala.util.Success

class MiraklClientSpec extends Specification with Specs2RouteTest {

  def actorRefFactory = system // connect the DSL to the test ActorSystem
  ActorSystemLocator(system)

  sequential

  "MiraclClient " should {

    "returns failure when bad parameters are send" in {
      val billing_address = new Address("Paris",
        Some("Mme"),
        None,
        "France",
        "FRA",
        Some("MyFirstName"),
        "MyLastName",
        None,
        None,
        None,
        "10 rue du commerce",
        None,
        Some("75015"))

      val shipping_address = new ShippingAddress("Paris",
        Some("Mme"),
        None,
        "France",
        "FRA",
        Some("MyFirstName"),
        "MyLastName",
        None,
        None,
        None,
        "20 rue du Paradis",
        None,
        Some("13000"),
        None,
        None)

      val customer = Customer("000-001",
        Some("Mme"),
        "MyFirstName",
        "MyLastName",
        "email@gmail.com",
        Some("fr_FR"),
        billing_address: Address,
        shipping_address: ShippingAddress)

      val offer = new Offer("EUR",
        None,
        1206,
        456,
        Nil,
        None,
        456,
        1,
        60,
        "STD",
        Nil,
        Nil)

      val order = new OrderBean(Some("CHANNEL1"),
        "OR01",
        customer: Customer,
        List(offer),
        Nil,
        None,
        None,
        true,
        "INT")

      val res = MiraklClient.createOrder(order)
      res must beFailedTry.withThrowable[MiraklCreateOrderException]
    }

    "returns the created order" in {
      val billing_address = new Address("Paris",
        Some("Mme"),
        None,
        "France",
        "FRA",
        Some("MyFirstName"),
        "MyLastName",
        None,
        None,
        None,
        "10 rue du commerce",
        None,
        Some("75015"))

      val shipping_address = new ShippingAddress("Paris",
        Some("Mme"),
        None,
        "France",
        "FRA",
        Some("MyFirstName"),
        "MyLastName",
        None,
        None,
        None,
        "20 rue du Paradis",
        None,
        Some("13000"),
        None,
        None)

      val customer = Customer("000-001",
        Some("Mme"),
        "MyFirstName",
        "MyLastName",
        "email@gmail.com",
        Some("fr_FR"),
        billing_address: Address,
        shipping_address: ShippingAddress)

      val offer = new Offer("EUR",
        None,
        2095,
        456,
        Nil,
        None,
        456,
        1,
        60,
        "STD",
        Nil,
        Nil)

      val order = new OrderBean(None,
        UUID.randomUUID().toString,
        customer: Customer,
        List(offer),
        Nil,
        None,
        Some(PaymentWorkflow.PAY_ON_ACCEPTANCE),
        true,
        "FR1")

      val res = MiraklClient.createOrder(order)
      res must beSuccessfulTry
      res match {
        case Success(order : OrderCreatedDTO) => {
          order.offers_not_shippable must size(0)
          order.orders must not size(0)
        }
      }
    }
  }
}