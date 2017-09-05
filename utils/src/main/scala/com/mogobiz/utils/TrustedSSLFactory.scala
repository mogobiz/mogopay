/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

/**
  */
class TrustedSSLFactory {}

import java.security.{GeneralSecurityException, SecureRandom}
import java.security.cert.X509Certificate
import javax.net.ssl._

object TrustedSSLFactory {
  def getTrustingSSLSocketFactory: SSLSocketFactory = {
    TrustedSSLFactory.createSSLSocketFactory
  }

  private def createSSLSocketFactory: SSLSocketFactory = {
    val trustManagers: Array[TrustManager] = Array[TrustManager](new TrustedSSLFactory.NaiveTrustManager)
    var sslContext: SSLContext = null
    try {
      sslContext = SSLContext.getInstance("SSL")
      sslContext.init(new Array[KeyManager](0), trustManagers, new SecureRandom)
      sslContext.getSocketFactory
    } catch {
      case e: GeneralSecurityException => {
        null
      }
    }
  }

  val JaxwsSslSockeetFactory: String = "com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory"

  private class NaiveTrustManager extends X509TrustManager {
    def checkClientTrusted(certs: Array[X509Certificate], authType: String) {}

    def checkServerTrusted(certs: Array[X509Certificate], authType: String) {}

    def getAcceptedIssuers: Array[X509Certificate] = {
      new Array[X509Certificate](0)
    }
  }

}
