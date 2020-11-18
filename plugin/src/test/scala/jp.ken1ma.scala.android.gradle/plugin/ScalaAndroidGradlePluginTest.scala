package jp.ken1ma.scala.android.gradle
package plugin

import com.android.build.gradle.BaseExtension
import org.gradle.testfixtures.ProjectBuilder

import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class ScalaAndroidGradlePluginTest extends AnyFreeSpec {
  "pluginRegistersATask" in {
    val project = ProjectBuilder.builder.build
    GradleAndroidTestHelper.generateLocalProperties(project.getProjectDir)
    project.getPlugins.apply("com.android.application")
    project.getPlugins.apply("jp.ken1ma.scala.android.gradle.plugin")

    // avoid "compileSdkVersion is not specified. Please add it to build.gradle"
    project.getExtensions.getByType(classOf[BaseExtension]).setCompileSdkVersion(26)

    project.afterEvaluate { project =>
      assert(project.getTasks.findByName("compileDebugScala") != null)
      assert(project.getTasks.findByName("compileFooScala") == null) // not registered
    }

    // evaluate
    project.getTasksByName("tasks", false) // https://stackoverflow.com/a/47581917
  }
}
