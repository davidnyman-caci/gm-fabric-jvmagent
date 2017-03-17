package com.deciphernow.gm.agent.rest
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

import com.deciphernow.gm.agent.Routes
import com.deciphernow.server.security.UserAuthentication
import com.twitter.finagle.http.{HeaderMap, Request}
import com.twitter.finatra.http.Controller
import com.twitter.logging.Logger

class GMScalaAgentController(routes: Routes) extends Controller {

  /*
  WARNING: order of REST API definitions is important. Should place more generic API calls that can
  accidently be called instead of a static one can cause issue.
   */
  private val log = Logger.get(getClass)

  private def reloadRouteConfig(pathToConfig: Option[String], invalidateCache: Boolean = false): String = {
    routes.loadRoutes(pathToConfig, invalidateCache)
  }

  /**
    * Keep this. Use to see if the service is accepting connections.
    */
  get("/ping") { _: Request =>
    log.ifDebug("get(/ping)")
    response.ok("pong").toFuture
  }

  get("/reloadRoute") { request: Request =>
    val c = request.params.get("configPath")
    val i = request.params.get("invalidateCache").fold(false)(s => s.equals("true"))
    val message = reloadRouteConfig(c, i)

    response.ok(message).toFuture
  }

  /*
      Basic documentation
   */
  get("/") { _: Request =>
    response.ok.file("/public/home.html")
  }
  get("/home.html") { _: Request =>
    response.ok.file("/public/home.html")
  }
  get("/rest.html") { _: Request =>
    response.ok.file("/public/rest.html")
  }
  get("/routes.html") { _: Request =>
    response.ok.file("/public/routes.html")
  }

  any("/:*") { request: Request => try {
      if (missingUserToken(request.headerMap)) {
        response.unauthorized("The user_dn header is missing.").toFuture
      } else {
        val r = routes.clients(request.uri).processRequest(request).head
        log.ifDebug(s"completed request to: ${request.uri}")
        r
      }
    } catch {
      case e: Exception => response.badRequest(createExceptionMessage(e.getMessage)).toFuture
    }
  }

  private def createExceptionMessage(exceptionMessage: String): String =
    s"""
      |===================
      |Request rejected: $exceptionMessage
      |===================
    """.stripMargin

  def missingUserToken(headers: HeaderMap) : Boolean = {
    UserAuthentication.current.foreach( v => headers.put("user_dn",v.user))
    headers.get("user_dn").fold(true)(_.length == 0)
  }

}
