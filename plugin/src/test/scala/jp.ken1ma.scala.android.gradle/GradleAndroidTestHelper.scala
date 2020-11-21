package jp.ken1ma.scala.android.gradle

import java.nio.file.{Path, Files}
import java.io.File

object GradleAndroidTestHelper {
  def generateLocalProperties(projectDir: Path): Unit = {
    if (!Files.exists(projectDir))
      throw new IllegalArgumentException(s"Path not found: ${projectDir.toAbsolutePath}")

    else if (!Files.isDirectory(projectDir))
      throw new IllegalArgumentException(s"Path is not a directory: ${projectDir.toAbsolutePath}")

    else
      Files.writeString(projectDir.resolve("local.properties"), s"""
        sdk.dir=${AndroidTestHelper.ANDROID_SDK_ROOT}
      """)
  }

  def generateLocalProperties(projectDir: File): Unit =
      generateLocalProperties(projectDir.toPath)
}
