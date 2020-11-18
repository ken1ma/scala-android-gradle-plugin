package jp.ken1ma.scala.android.gradle

import java.nio.file.{Path, Files}
import java.io.File

object GradleAndroidTestHelper {
  def generateLocalProperties(projectDir: Path): Unit = {
    Files.writeString(projectDir.resolve("local.properties"), s"""
      sdk.dir=${AndroidTestHelper.ANDROID_SDK_ROOT}
    """)
  }

  def generateLocalProperties(projectDir: File): Unit =
      generateLocalProperties(projectDir.toPath)
}
