package jp.ken1ma.scala.android.gradle.plugin

import java.nio.file.{Paths, Files}
import org.gradle.testkit.runner.GradleRunner

import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class ScalaAndroidGradlePluginFunctionalTest extends AnyFreeSpec {
  "canRunTask" in {
    // Setup the test build
    val projectDir = Paths.get("build/functionalTest")
    Files.createDirectories(projectDir)
    Files.writeString(projectDir.resolve("settings.gradle"), "")
    Files.writeString(projectDir.resolve("build.gradle"), """
      plugins {
        id('jp.ken1ma.scala.android.gradle.plugin')
      }
    """)

    // Run the build
    val runner = GradleRunner.create
    runner.forwardOutput
    runner.withPluginClasspath
    runner.withArguments("compileVariantScala")
    runner.withProjectDir(projectDir.toFile)
    val result = runner.build

    // Verify the result
    assert(result.getOutput.contains("Hello from plugin 'jp.ken1ma.scala.android.gradle.plugin'"))
  }
}
