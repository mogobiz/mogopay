/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.template

import java.io.{InputStream, InputStreamReader}
import javax.script.{Invocable, ScriptEngine, ScriptEngineManager}

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object Mustache {
  val logger = Logger(LoggerFactory.getLogger("com.mogobiz.template.Mustache"))
  def using[T <: { def close() }](resource: T)(block: T => Unit) = {
    try {
      block(resource)
    } finally {
      if (resource != null) resource.close()
    }
  }

  // http://www.codechewing.com/library/conditional-if-else-check-mustache-js/
  def apply(template: String, customJs: Option[InputStream], jsonString: String): String = {
    logger.debug(jsonString)
    val javaVersion = java.lang.Double.parseDouble(System.getProperty("java.specification.version"))
    val engineName  = if (javaVersion < 1.8) "rhino" else "nashorn"

    val manager: ScriptEngineManager       = new ScriptEngineManager
    val engineManager: ScriptEngineManager = new ScriptEngineManager
    val engine: ScriptEngine               = engineManager.getEngineByName(engineName)
    using(new InputStreamReader(this.getClass.getResourceAsStream("/template/mustache.js"))) { r =>
      engine.eval(r)
    }
    if (this.getClass.getResource("/template/custom.js") != null) {
      using(new InputStreamReader(this.getClass.getResourceAsStream("/template/custom.js"))) { r =>
        engine.eval(r)
      }
    }
    customJs.map { input =>
      using(new InputStreamReader(input)) { r =>
        engine.eval(r)
      }
    }

    val invocable: Invocable = engine.asInstanceOf[Invocable]
    val json: AnyRef         = engine.eval("JSON")
    val data: AnyRef         = invocable.invokeMethod(json, "parse", jsonString)
    val mustache: AnyRef     = engine.eval("Mogobiz")
    invocable.invokeMethod(mustache, "render", template, data).toString
  }

  def main(args: Array[String]): Unit = {
    val js =
      """
        |{"formatedReduction":"\u20ac0.00","coupons":[],"formatedFinalPrice":"\u20ac11.98","count":1,"transactionUuid":"bf75127a-4a33-4a42-9155-9bb2320be290","cartItemVOs":[{"id":"c5bf9aed-5f15-4c78-a395-8ba1ba4a8fcc","productId":643,"productName":"Electric Blue Womens Football Shirt","xtype":"PRODUCT","calendarType":"NO_DATE","skuId":700,"skuName":"short s","quantity":1,"price":999,"endPrice":1198,"tax":20.0,"totalPrice":999,"totalEndPrice":1198,"salePrice":999,"saleEndPrice":1198,"saleTotalPrice":999,"saleTotalEndPrice":1198,"registeredCartItemVOs":[],"shipping":{"id":0,"weight":10,"weightUnit":"KG","width":10,"height":10,"depth":10,"linearUnit":"CM","amount":0,"free":false},"downloadableLink":null,"formatedSaleTotalEndPrice":"\u20ac11.98","formatedSaleTotalPrice":"\u20ac9.99","formatedTotalEndPrice":"\u20ac11.98","formatedSaleEndPrice":"\u20ac11.98","formatedEndPrice":"\u20ac11.98","formatedPrice":"\u20ac9.99","formatedTotalPrice":"\u20ac9.99","formatedSalePrice":"\u20ac9.99"}],"price":999,"formatedEndPrice":"\u20ac11.98","formatedPrice":"\u20ac9.99","finalPrice":1198,"reduction":0,"endPrice":1198,"boCartUuid":"d4a3add7-dd09-408f-b3d9-2b225c9d914d"}
        |      """.stripMargin

    val template = """
        |{{#fn_payment_status}}{{status.name}}{{/fn_payment_status}}
        |{{#fn_payment_status}}{{{transactionUuid}}}{{/fn_payment_status}}
        |Final Price : {{{transactionUuid}}}
        |Thank You
      """.stripMargin
    val res      = Mustache(template, None, js)
    logger.debug(res)
  }
}
