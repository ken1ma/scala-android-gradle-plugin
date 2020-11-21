package jp.ken1ma.scala.android.gradle
package plugin

import java.nio.file.{Paths, Files}
import org.gradle.testkit.runner.GradleRunner

import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class AndroidStudioScalaTest extends AnyFreeSpec {
  "Simple Android project with the plugin" in {
    val projectDir = Paths.get("src/functionalTest/projects/AndroidStudioScala")
    GradleAndroidTestHelper.generateLocalProperties(projectDir)

    val runner = GradleRunner.create
    runner.withProjectDir(projectDir.toFile)
    runner.withPluginClasspath
    runner.forwardOutput

    assert(runner.withArguments("uninstallAll").build.getOutput.contains("BUILD SUCCESSFUL"))
    assert(runner.withArguments("clean").build.getOutput.contains("BUILD SUCCESSFUL"))

    // build APKs (depends on assembleDebug and assembleRelease)
    val buildOutput = runner.withArguments("build").build.getOutput
    assert(buildOutput.contains("BUILD SUCCESSFUL"))

    // Run unit tests (depends on testDebugUnitTest and testReleaseUnitTest)
    val testOutput = runner.withArguments("test").build.getOutput
    assert(testOutput.contains("BUILD SUCCESSFUL")) // "BUILD FAILED" instead when a test fails

    // Run instrumented tests (on a device)
    // this can fail with "com.android.builder.testing.api.DeviceException: No connected devices!"
    // TODO: should installDebugAndroidTest be tested too? (connectedDebugAndroidTest doesn't include installDebugAndroidTest)
    val androidTestOutput = runner.withArguments("connectedDebugAndroidTest").build.getOutput
    assert(androidTestOutput.contains("BUILD SUCCESSFUL")) // "BUILD FAILED" instead when a test fails

    //assert(runner.withArguments("installDebug").build.getOutput.contains("BUILD SUCCESSFUL"))
    assert(runner.withArguments("uninstallAll").build.getOutput.contains("BUILD SUCCESSFUL"))

    //assert(runner.withArguments("scaladoc").build.getOutput.contains("BUILD SUCCESSFUL"))

    // make sure the tasks have been run
    assert(buildOutput      .contains("Task :app:compileDebugScala"))
    assert(buildOutput      .contains("Task :app:compileReleaseScala"))
    assert(testOutput       .contains("Task :app:compileDebugUnitTestScala"))
    assert(testOutput       .contains("Task :app:compileReleaseUnitTestScala"))
    assert(androidTestOutput.contains("Task :app:compileDebugAndroidTestScala"))
  }
}
