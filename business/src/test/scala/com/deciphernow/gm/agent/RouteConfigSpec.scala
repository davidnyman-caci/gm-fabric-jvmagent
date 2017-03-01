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
