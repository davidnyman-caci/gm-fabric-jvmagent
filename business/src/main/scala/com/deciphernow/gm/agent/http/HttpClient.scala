package com.deciphernow.gm.agent.http

import com.deciphernow.gm.agent.Route
import com.twitter.finagle.Service
import com.twitter.finagle.http.Version.Http11
import com.twitter.finagle.http.{Request => HttpRequest, Response => HttpResponse}
import com.twitter.logging.Logger
import com.twitter.util.Future


class HttpClient(route: Route, httpService: Service[HttpRequest, HttpResponse]) {

  private val log = Logger.get(getClass)

  /**
    * Assume the routeId is the first segment of the URI, remove it.
    * If a path attribute for the route was provided, prepend it.
    *
    * @param uri - the uri of the request, with the routeId in need of removal
    * @return - the updated uri, with optional path prepended
    */
  private[http] def parseRoute(uri: String): String = {
    val s = uri.split("/").toSeq.filter(a => a != "")
    s.length match {
      case a if a > 1 =>
        route.path.getOrElse("") + s.tail.fold("") { (a, b) => a + "/" + b }
      case _ =>
        route.path.getOrElse("")
    }
  }

  def processRequest(request: HttpRequest): Vector[Future[HttpResponse]] = for {
    r <- Vector(request)
    nr = buildRequest(r)
    xr = copyAttributes(nr, r)
  } yield {
    log.ifDebug(s"initiating request for uri: ${request.uri}")
    httpService.apply(xr)
  }


  private def copyAttributes(newReq: HttpRequest, oldReq: HttpRequest) = {
    newReq.host = route.addresses
    newReq.setContentString(oldReq.getContentString())
    newReq.uri = parseRoute(oldReq.uri)
    newReq
  }

  private def buildRequest(request: HttpRequest) = {
    log.ifDebug(s"${request.method}::partialUri : ${request.uri} : addresses : ${route.addresses}")
    val r = HttpRequest(Http11, request.method, request.uri, request.reader)
    request.headerMap.foreach(h => r.headerMap.set(h._1, h._2))
    r.setChunked(false)
    r
  }
}
