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
