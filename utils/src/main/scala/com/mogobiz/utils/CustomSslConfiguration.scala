/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import java.security.cert.X509Certificate
import javax.net.ssl.{KeyManager, SSLContext, X509TrustManager}

import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.mogobiz.system.ActorSystemLocator
import spray.can.Http
import spray.client.pipelining._
import spray.http.Uri.Host
import spray.http.{HttpRequest, HttpResponse}
import spray.io.ClientSSLEngineProvider

import scala.concurrent.Future

trait CustomSslConfiguration {
  implicit val trustfulSslContext: SSLContext = {
    object BlindFaithX509TrustManager extends X509TrustManager {
      def checkClientTrusted(chain: Array[X509Certificate], authType: String) = ()

      def checkServerTrusted(chain: Array[X509Certificate], authType: String) = ()

      def getAcceptedIssuers = Array[X509Certificate]()
    }

    val context = SSLContext.getInstance("TLS")
    context.init(Array[KeyManager](), Array(BlindFaithX509TrustManager), null)
    context
  }

  implicit val sslEngineProvider: ClientSSLEngineProvider = {
    // To enable TLS 1.2 you can use the following setting of the VM: -Dhttps.protocols=TLSv1.1,TLSv1.2
    ClientSSLEngineProvider { engine =>
      engine.setEnabledProtocols(Array("TLSv1.2", "TLSv1.1", "SSLv3"))
      engine
    }
  }
  implicit def timeout: Timeout

  def sslPipeline(host: Host): Future[SendReceive] = {
    implicit val system = ActorSystemLocator()
    implicit val _      = system.dispatcher
    val logRequest: HttpRequest => HttpRequest = { r =>
      println(r); r
    }
    val logResponse: HttpResponse => HttpResponse = { r =>
      println(r); r
    }
    for (Http.HostConnectorInfo(connector, _) <- IO(Http) ? Http.HostConnectorSetup(
                                                    host.toString,
                                                    443,
                                                    sslEncryption = true)(system, sslEngineProvider))
      yield logRequest ~> sendReceive(connector) ~> logResponse

  }
}
