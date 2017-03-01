package com.deciphernow.gm.agent

import java.io.File

import com.deciphernow.gm.agent.route.decode.RouteDecoder
import com.deciphernow.gm.agent.route.load.RouteLoader
import com.twitter.logging.Logger
import com.typesafe.config.{Config, ConfigFactory}

import com.deciphernow.server.support.{Decryptor, DecryptorManager}

case class ServiceProperties(requestTimeout: Int, maxResponseSize: Int, maxRequestSize: Int, configPath: String)

sealed trait Route {

  // This is the unique route identifier, defined in the first uri segment of the request
  def routeId: String

  // a comma-delimited list of domains and ports
  def addresses: String

  // http protocol
  def protocol: String

  // a path is a uri segment that will be added to the request before prepending the uri
  def path: Option[String]

  // the path with forward slashes added to the beginning and end
  def validPath: Option[String] = path.map { p =>
    doTheSlash(path.get.head, isFront = true) + path.get.tail.dropRight(1) + doTheSlash(path.get.last, isFront = false)
  }

  private def doTheSlash(character: Char, isFront: Boolean): String = if (character == "/".charAt(0)) {
    character.toString
  } else {
    if (isFront) {
      "/" + character
    } else {
      character + "/"
    }
  }
}

sealed trait SSLConfig

case class PlainText(routeId: String, addresses: String, path: Option[String] = None) extends Route {
  override def protocol: String = "http"
}

case class OneWaySSL(routeId: String, addresses: String, path: Option[String] = None) extends Route {
  override def protocol = "https"
}

case class TwoWaySSL(
                      routeId: String,
                      addresses: String,
                      path: Option[String] = None,
                      sslImpl: Option[String] = None,
                      sslConfig: TwoWaySSLConfig
                    ) extends Route {
  override def protocol: String = "https"
}

case class TwoWaySSLConfig(
                            keyStore: String,
                            keyStorePass: String,
                            trustStore: String,
                            trustStorePass: String) extends SSLConfig {

  private lazy val decryptor : Decryptor = DecryptorManager.getInstance

  def decryptedKeyStorePass(): String = {
    decryptor.decryptResource(keyStorePass)
  }

  def decryptedTrustStorePass(): String = {
    decryptor.decryptResource(trustStorePass)
  }
}

case class ServiceConfig(routes: List[Route])

class Routes(serviceProperties: ServiceProperties) extends RouteDecoder with RouteLoader {
  protected val log = Logger.get(getClass)

  log.ifInfo("loading route configuration")

  loadRoutes()


  /**
    * This function rereads the route configuration file and reinitializes the
    * proxied services.
    * Optionally, it can invalidate the config cache as well for a "complete reboot".
    *
    * @param newConf
    * @param invalidateCache
    * @return
    */
  def loadRoutes(newConf: Option[String] = None, invalidateCache: Boolean = false): String = {
    val c = loadConfig(newConf, invalidateCache)
    val d = decodeConfig(c)
    initializeRoutes(d, serviceProperties)
  }

  private [agent] def loadConfig(newConf: Option[String], invalidateCache: Boolean): Config = {
    if (invalidateCache) {
      ConfigFactory.invalidateCaches()
    }

    // either reload the provided configuration file (in the event it was edited)
    // or use the newConf parameter
    val resource = newConf.fold(serviceProperties.configPath)(r => r)

    // parseFileAnySyntax means it's going to parse this file only,
    // as opposed to ConfigFactory.load which will also parse env properties
    // and base Config.
    val a = ConfigFactory.parseFileAnySyntax(new File(resource))
    log.ifDebug("loaded application config")

    // now add in environment properties and etc
    val b = ConfigFactory.load(a)

    log.ifDebug("merged application config with system properties")
    b
  }

}
