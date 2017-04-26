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
import com.twitter.app.GlobalFlag

object requestTimeout extends GlobalFlag[Int](10,"Override the request timeout for the client")
object maxResponseSize extends GlobalFlag[Int](5,"Override the response size in MB for the client")
object maxRequestSize extends GlobalFlag[Int](5,"Override the request size in MB for the client")
object routeConfig extends GlobalFlag[String]("etc/application.conf", "Override the default route configuration location")
