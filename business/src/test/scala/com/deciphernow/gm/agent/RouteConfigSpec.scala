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
package com.deciphernow.gm.agent

import classy.config.ConfigDecoder
import classy.generic.auto._
import classy.config._
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

class RouteConfigSpec extends FlatSpec with Matchers {


  "Route Configuration" should "parse to case classes" in {
    val typesafeConfig = ConfigFactory parseString
      s"""
         |{"routes": [
         |  { "plainText": {
         |    "routeId": "abc",
         |    "addresses": "localhost:890"
         |  }}
         |]}
        """.stripMargin
    // level zed - full black magic
    val res = ConfigDecoder[ServiceConfig] decode typesafeConfig


    res isRight match {
      case true =>
        res.right.get.routes should have size 1


      case false =>
        res.left.foreach( e => println(e))
        fail("invalid route configuration")
    }
  }

}
