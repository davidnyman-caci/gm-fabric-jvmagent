package com.deciphernow.gm.agent.http

import java.io.File

import com.deciphernow.gm.agent._
import com.deciphernow.server.tls.TlsConfigUtil.{createSslContext, mkClient}
import com.twitter.conversions.storage._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.ssl.Engine
import com.twitter.finagle.transport.Transport
import com.twitter.finagle.{Http, Service}
import com.twitter.logging.Logger
import com.twitter.util.Duration

/**
  * Helper class that creates new Http Clients.
  */
object HttpClientFactory {

  private val log = Logger.get(getClass)

  /**
    * All services will have the same basic properties created here
    * @param props
    * @return
    */
  private def baseClient(props: ServiceProperties): Http.Client = Http.client
    //.withStreaming(true)
    .withRequestTimeout(Duration.fromSeconds(props.requestTimeout))
    .withMaxRequestSize(props.maxRequestSize.megabytes)
    .withMaxResponseSize(props.maxResponseSize.megabytes)

  /**
    * Create the appropriate service based on the route type
    * @param route
    * @param properties
    * @return
    */
  def createService(route: Route, properties: ServiceProperties): Service[Request, Response] = route match {
    case plainText: PlainText =>
      log.ifInfo(s"configuring a plaintext client: ${route.routeId}")
      baseClient(properties)
        .newClient(route.addresses, route.routeId).toService
    case oneWay: OneWaySSL =>
      log.ifInfo(s"configuring a onewayssl client: ${route.routeId}")
      baseClient(properties)
        .withTlsWithoutValidation
        .newClient(route.addresses, route.routeId)
        .toService
    case twoWay: TwoWaySSL =>
      log.ifInfo(s"configuring a twowayssl client: ${route.routeId}")
      twoWayClient(twoWay, baseClient(properties))
  }

  private [http] def twoWayClient(route: TwoWaySSL, baseClient: Http.Client): Service[Request, Response] = {

    log.ifDebug(s"keystore path: ${route.sslConfig.keyStore}")
    log.ifDebug(s"truststore path: ${route.sslConfig.trustStore}")

    val f1 = new File(route.sslConfig.keyStore)
    log.ifDebug("found keystore file")

    val f2 = new File(route.sslConfig.trustStore)
    log.ifDebug("found truststore file")


    baseClient.configured(Transport.TLSClientEngine(Some(_ =>
      new Engine(mkClient(
        createSslContext(
          f1,
          route.sslConfig.keyStorePass,
          f2,
          route.sslConfig.trustStorePass)))
    ))).newClient(route.addresses, route.routeId).toService

  }

}
