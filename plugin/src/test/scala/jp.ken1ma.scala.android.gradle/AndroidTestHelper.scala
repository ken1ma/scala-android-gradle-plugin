package jp.ken1ma.scala.android.gradle

import scala.util.Properties.{envOrElse, userHome, isWin}

object AndroidTestHelper {
  lazy val ANDROID_SDK_ROOT = envOrElse("ANDROID_SDK_ROOT", {
    if (isWin)
      s"$userHome/AppData/Local/Android/sdk"
    else
      s"$userHome/Library/Android/sdk" // macOS, Linux
  })
}
