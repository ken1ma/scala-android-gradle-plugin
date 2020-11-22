package com.example.androidstudioscala

import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class ExampleUnitTest extends AnyFreeSpec {
  "addition" - {
    "correct" in {
      assert(2 + 2 == 4)
    }
  }
}
