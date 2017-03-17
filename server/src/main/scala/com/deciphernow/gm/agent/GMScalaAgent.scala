package com.deciphernow.gm.agent
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

import java.io.File

import com.deciphernow.gm.agent.rest.GMScalaAgentController
import com.deciphernow.server.filters.GenericUriStatsFilter
import com.deciphernow.server.security.{AclRestFilter, FileWhitelistImpersonationAccessManager}
import com.deciphernow.server.{GMFabricServer, RestServer}

object GMScalaAgent extends GMFabricServer {

  // When using impersonating security filters, we need an access manager
  var accessManager: FileWhitelistImpersonationAccessManager = _

  // The access manager will require a whitelist file.  This is one way to use configuration for the file path
  private val whitelistFile = flag[File]("acl.whitelist.file", "ACL whitelist file for user impersonation")

  var routes: Routes = _

  premain {

    log.ifDebug("In premain")

    accessManager = new FileWhitelistImpersonationAccessManager(
      whitelistFile()
    )

    val rt =requestTimeout.apply
    val mrs = maxRequestSize.apply
    val maxrs = maxResponseSize.apply
    val configPath = routeConfig.apply

    log.ifInfo(s"route client requesttimeout: $rt")
    log.ifInfo(s"route client maxResponseSize: $mrs")
    log.ifInfo(s"route client maxRequestSize: $maxrs")

    routes = new Routes(ServiceProperties(rt, mrs, maxrs, configPath))
  }

  // The agent does not provide a thrift interface.
  def thrift = None

  /*
       Filters, Controllers
   */
  def rest = Some( RestServer(
    Vector(new AclRestFilter(accessManager), new GenericUriStatsFilter),
    Vector(new GMScalaAgentController(routes))
  ))

}
