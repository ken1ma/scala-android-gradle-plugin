package jp.ken1ma.scala.android.gradle.plugin

import org.gradle.testfixtures.ProjectBuilder

import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class ScalaAndroidGradlePluginTest extends AnyFreeSpec {
  "pluginRegistersATask" in {
    // Create a test project and apply the plugin
    val project = ProjectBuilder.builder.build
    project.getPlugins.apply("jp.ken1ma.scala.android.gradle.plugin")

    // Verify the result
    assert(project.getTasks.findByName("compileVariantScala") != null)
  }
}
