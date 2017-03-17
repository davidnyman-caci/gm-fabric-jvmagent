/*
 Copyright 2017 Decipher Technology Studios LLC

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.deciphernow.gm.agent.route.load

import classy.core.DecodeError
import com.deciphernow.gm.agent.http.HttpClientFactory.createService
import com.deciphernow.gm.agent.{ServiceConfig, ServiceProperties}
import com.deciphernow.gm.agent.http.HttpClient
import com.twitter.logging.Logger

import scala.util.{Failure, Success, Try}

trait RouteLoader {
  private var _clients: Map[String, HttpClient] = Map.empty
  implicit protected val log: Logger

  def initializeRoutes(
                        config: Either[DecodeError, ServiceConfig],
                        properties: ServiceProperties): String = config.fold(de => {
    de.toString
  }, r => {
    _clients = r.routes.map { r =>
      log.ifInfo(s"Initializing client for route: ${r.routeId}")
      r.routeId -> new HttpClient(r, createService(r, properties))
    }.toMap
    "success"
  })

  def clients(uri: String): HttpClient = {
    _clients(getRouteId(uri).get)
  }

  private [agent] def getRouteId(uri: String)(implicit log: Logger): Try[String] = {
    log.ifDebug(s"getRouteId: $uri")

    // split the uri
    val s = uri.split("/").toSeq.filter(a => a != "")

    // the routeid will be the first segment of the uri, or head here, if it exists
    s.headOption match {

      // matches an existing route
      case Some(a) if _clients.keySet.contains(a) =>
        val r = s.tail.fold("") { (a, b) => a + "/" + b }
        log.ifTrace(s"matched route $r :: $a")
        Success(a)

      case Some(a) if !_clients.keySet.contains(a) =>

        // did not match route but there is a default route defined
        if (_clients.keySet.contains("default")) {
          log.ifTrace(s"using default route")

          Success("default")

        } else {
          //
          log.ifTrace(s"fail no matching route id and no default route defined")
          Failure(new RuntimeException("Unable to find default or provided route"))
        }

      case None =>
        // did not find a routeid but there is a default defined
        if (_clients.keySet.contains("default")) {
          log.ifTrace("using default route after not finding a routeid in the uri")
          Success("default")
        } else {
          // another failed condition- no routeid segment was found in the uri and no default route is configured
          log.ifTrace("failed without a routeid and without a default route to fall back on")
          Failure(new RuntimeException("Unable to find default or provided route"))
        }
    }
  }
}
