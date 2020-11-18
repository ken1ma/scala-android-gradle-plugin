package jp.ken1ma.scala.android.gradle
package plugin

import scala.jdk.CollectionConverters._
import java.io.File
import java.lang.{Iterable => JavaIterable}

import org.gradle.api.Project
import org.gradle.api.tasks.ScalaRuntime

class ScalaRuntimeForAndroid(project: Project) extends ScalaRuntime(project) {
  // Android Gradle plugin 4.1.0: the jars are prefixed with "jetified-" in the later stages
  val SCALA_JAR_PATTERN = "(jetified-)?scala-(\\w.*?)-(\\d.*).jar".r

  override def findScalaJar(classpath: JavaIterable[File], appendix: String): File = {
    classpath.asScala.find { file =>
      SCALA_JAR_PATTERN.findFirstMatchIn(file.getName).map(_.group(2) == appendix).getOrElse(false)
    }.orNull
  }

  override def getScalaVersion(scalaJar: File): String = {
    SCALA_JAR_PATTERN.findFirstMatchIn(scalaJar.getName).map(_.group(3)).orNull
  }
}
