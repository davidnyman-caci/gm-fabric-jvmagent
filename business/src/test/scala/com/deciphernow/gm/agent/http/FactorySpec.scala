package com.deciphernow.gm.agent.http

import org.scalatest.{FlatSpec, Matchers}

import com.deciphernow.gm.agent.{TwoWaySSL, TwoWaySSLConfig}

class FactorySpec extends FlatSpec with Matchers {

  "A twoway ssl client" should "decrypt passwords" in {
    val keyStorePass = "keyStorePass"
    val trustStorePass = "trustStorePass"
    val tws = TwoWaySSL(routeId = "a", addresses = "b", sslConfig = TwoWaySSLConfig("a", keyStorePass, "b", trustStorePass))
    tws.sslConfig.decryptedKeyStorePass shouldBe keyStorePass
    tws.sslConfig.decryptedTrustStorePass shouldBe trustStorePass
  }
}
