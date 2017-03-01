package com.deciphernow.gm.agent

import com.twitter.app.GlobalFlag

object requestTimeout extends GlobalFlag[Int](10,"Override the request timeout for the client")
object maxResponseSize extends GlobalFlag[Int](5,"Override the response size in MB for the client")
object maxRequestSize extends GlobalFlag[Int](5,"Override the request size in MB for the client")
object routeConfig extends GlobalFlag[String]("/etc/application.conf", "Override the default route configuration location")
