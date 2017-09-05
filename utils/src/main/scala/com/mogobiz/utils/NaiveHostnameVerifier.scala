/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import javax.net.ssl.{HostnameVerifier, SSLSession}

object NaiveHostnameVerifier {
  val JaxwsHostNameVerifier: String = "com.sun.xml.internal.ws.transport.https.client.hostname.verifier"
}

class NaiveHostnameVerifier extends HostnameVerifier {
  def verify(hostName: String, session: SSLSession): Boolean = true
}
