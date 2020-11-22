package jp.ken1ma.scala.android.gradle
package plugin

import java.nio.file.{Paths, Files}
import org.gradle.testkit.runner.GradleRunner

import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class ScalaAndroidGradlePluginFunctionalTest extends AnyFreeSpec {
  "canRunTask" in {
    // Setup the test build
    val projectDir = Paths.get("build/functionalTest/ScalaAndroidGradlePluginFunctionalTest")
    Files.createDirectories(projectDir.resolve("src/main"))
    GradleAndroidTestHelper.generateLocalProperties(projectDir)
    Files.writeString(projectDir.resolve("settings.gradle"), """
/*
      pluginManagement {
          repositories {
              google()
          }

          // avoid "could not resolve plugin artifact 'com.android.application:com.android.application.gradle.plugin:4.1.0'"
          // https://stackoverflow.com/a/62075979
          resolutionStrategy {
              eachPlugin {
                if (requested.id.namespace == "com.android")
                  useModule("com.android.tools.build:gradle:${requested.version}")
              }
          }
      }
*/
    """)
    Files.writeString(projectDir.resolve("build.gradle"), """
      plugins {
          id 'com.android.application'
          id 'jp.ken1ma.scala.android.gradle.plugin'
      }
      android {
          compileSdkVersion 29 // required
          defaultConfig {
/*
              applicationId "com.example.myapplication" // required
              //minSdkVersion 26 // minimum version that supports invokedynamic
*/
          }
      }
      dependencies {
          implementation 'org.scala-lang:scala-library:2.13.4'
      }
      repositories {
          google()
          jcenter()
      }
    """)
    Files.writeString(projectDir.resolve("src/main/AndroidManifest.xml"), """
      <?xml version="1.0" encoding="utf-8"?>
      <manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="com.example.myapplication">

      </manifest>
    """.trim)

    // Run the build
    val runner = GradleRunner.create
    runner.forwardOutput
    runner.withPluginClasspath
    runner.withArguments(
      "compileDebugScala",
      "compileDebugUnitTestScala",
      "compileDebugAndroidTestScala",
      "compileReleaseScala",
      "compileReleaseUnitTestScala",
    )
    runner.withProjectDir(projectDir.toFile)
    val result = runner.build

    // Verify the result
    assert(result.getOutput.contains("Task :compileDebugScala"))
    assert(result.getOutput.contains("Task :compileDebugUnitTestScala"))
    assert(result.getOutput.contains("Task :compileDebugAndroidTestScala"))
    assert(result.getOutput.contains("Task :compileReleaseScala"))
    assert(result.getOutput.contains("Task :compileReleaseUnitTestScala"))
  }
}
