package jp.ken1ma.scala.android.gradle
package plugin

import scala.jdk.CollectionConverters._

import com.android.build.gradle.api.{BaseVariant, ApkVariant, LibraryVariant, UnitTestVariant}
import com.android.builder.model.SourceProvider

object AndroidToTexts {
  implicit class BaseVariantTextOps(val variant: BaseVariant) extends AnyVal {
    def toText: String = s"${variant match {
      case apk: ApkVariant => s"ApkVariant" // getClass.getName is ApplicationVariantImpl_Decorated
      case lib: LibraryVariant => s"LibraryVariant"
      case unitTest: UnitTestVariant => s"UnitTestVariant"
      case _ => variant.getClass.getName
    }}(${variant.getName})"
  }

  implicit class SourceProviderTextOps(val sourceProvider: SourceProvider) extends AnyVal {
    def toText: String = s"${sourceProvider.getName} (${sourceProvider.getJavaDirectories.asScala.mkString(", ")})"
  }
}
