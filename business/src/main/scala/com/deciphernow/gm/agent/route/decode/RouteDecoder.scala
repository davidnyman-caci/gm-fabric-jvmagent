package com.deciphernow.gm.agent.route.decode

import classy.core.DecodeError
import com.deciphernow.gm.agent.ServiceConfig
import com.typesafe.config.Config


trait RouteDecoder {

  /*
  This function is in a trait to access the implicit decoders.
  Previously it would lose access when part of another class.
  Separating it into this trait solved the problem.
   */
  def decodeConfig(c: Config): Either[DecodeError, ServiceConfig] = {

    import classy.config._
    import classy.generic.auto._

    ConfigDecoder[ServiceConfig] decode c

  }
}
