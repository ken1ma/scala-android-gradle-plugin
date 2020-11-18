package jp.ken1ma.scala.android.gradle
package plugin

import scala.jdk.CollectionConverters._

import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import com.android.build.gradle.{BaseExtension, AppExtension, LibraryExtension, FeatureExtension}
import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.SourceProvider

import AndroidToTexts._

object AndroidExtensionHelper {
  implicit class BaseExtensionOps(val ext: BaseExtension) extends AnyVal {
    def variants: collection.Set[BaseVariant] = ext match {
      case app: AppExtension => app.getApplicationVariants.asScala ++ app.getTestVariants.asScala ++ app.getUnitTestVariants.asScala
      case lib: LibraryExtension => lib.getLibraryVariants.asScala ++ lib.getTestVariants.asScala ++ lib.getUnitTestVariants.asScala
        // TODO: maybe need to add FeatureExtension.getFeatureVariants
    }
  }

  implicit class BaseVariantOps(val variant: BaseVariant) extends AnyVal {
    def javaCompile: JavaCompile = Option(variant.getJavaCompileProvider.getOrNull)
        .getOrElse(throw new Exception(s"${variant.toText}: no JavaCompile"))

    def sourceSets: Seq[SourceProvider] = variant.getSourceSets.asScala.toSeq
  }
}
