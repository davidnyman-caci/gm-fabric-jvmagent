package com.deciphernow.gm.agent

import org.scalatest.{FlatSpec, Matchers}

class PathSpec extends FlatSpec with Matchers {

  "A path string" should "add slashes if missing" in {
    val pt = PlainText("1", "localhost:80", Some("fat/chance"))

    pt.validPath.get shouldBe "/fat/chance/"

  }

  it should "not add slashes if they exist" in {
    val pt = PlainText("1", "localhost:80", Some("/fat/chance/"))
    pt.validPath.get shouldBe "/fat/chance/"

  }

  it should "ignore a non-existing path" in {
    val pt = PlainText("1", "localhost:80", None)
    pt.validPath shouldBe None
  }

  it should "correct only one if necessary" in {
    val pt = PlainText("1", "localhost:80", Some("fat/chance/"))
    pt.validPath.get shouldBe "/fat/chance/"

  }

  it should "correct the other one if necessary" in {
    val pt = PlainText("1", "localhost:80", Some("/fat/chance"))
    pt.validPath.get shouldBe "/fat/chance/"

  }
}
