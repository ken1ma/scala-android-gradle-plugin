package jp.ken1ma.scala.android.gradle
package plugin

import scala.math.Ordering, Ordering.Implicits.infixOrderingOps
import scala.jdk.CollectionConverters._
import java.io.File
import javax.inject.Inject

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.{Plugin, Project, Action}
import org.gradle.api.model.ObjectFactory
import org.gradle.api.attributes.Usage
import org.gradle.api.file.{FileCollection, FileTree}
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.scala.{ScalaCompile, ScalaDoc}
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.plugins.scala.{ScalaBasePlugin, ScalaPluginExtension}
import org.gradle.api.plugins.jvm.internal.JvmEcosystemUtilities
import org.gradle.api.internal.tasks.scala.DefaultScalaPluginExtension
import org.gradle.api.logging.Logging
import org.gradle.util.GradleVersion

import AndroidExtensionHelper._
import AndroidToTexts._
import GradleToTexts._

class ScalaAndroidGradlePlugin extends Plugin[Project] with GradleLogHelper {
  val logger = Logging.getLogger(classOf[ScalaAndroidGradlePlugin])
  override val infoLevel = warnLevel // infoLevel doesn't show up in the default
  override val debugLevel = warnLevel

  val DEFAULT_SCALA_ZINC_VERSION = "2.12" // no 2.13 artifact for zinc 1.3.5 // https://mvnrepository.com/artifact/org.scala-sbt/zinc

  val minGradleVersion = GradleVersion.version("6.7")
  implicit val gradleVersionOrdering = new Ordering[GradleVersion] { // TODO: can Ordering be derived from Comparable?
    def compare(a: GradleVersion, b: GradleVersion) = a.compareTo(b)
  }

  var objectFactory: ObjectFactory = null
  var jvmEcosystemUtilities: JvmEcosystemUtilities = null

  @Inject
  def this(objectFactory: ObjectFactory, jvmEcosystemUtilities: JvmEcosystemUtilities) = {
    this()
    this.objectFactory = objectFactory
    this.jvmEcosystemUtilities = jvmEcosystemUtilities
  }

  def apply(project: Project): Unit = {
    // version check
    if (GradleVersion.current < minGradleVersion)
      log.warn(s"${GradleVersion.current} is untested: try $minGradleVersion if it fails")

    /*
      We cannot use org.gradle.api.plugins.scala.ScalaBasePlugin
      because it obtains the source sets from JavaPluginConvention
      but Android Gradle plugin doesn't have JavaPluginConvention
    */
    //project.getPluginManager.apply(classOf[JavaBasePlugin])
    val scalaRuntime = project.getExtensions.create("scalaRuntime", classOf[ScalaRuntimeForAndroid], project)
    val scalaPluginExtension = project.getExtensions.create(classOf[ScalaPluginExtension], "scala", classOf[DefaultScalaPluginExtension])
    val incrementalAnalysisUsage = objectFactory.named(classOf[Usage], "incremental-analysis")

    // scalaCompilerPlugins confuguration
    val scalaCompilerPlugins = project.getConfigurations.create("scalaCompilerPlugins")
    scalaCompilerPlugins.setTransitive(false)
    scalaCompilerPlugins.setCanBeConsumed(false)
    jvmEcosystemUtilities.configureAsRuntimeClasspath(scalaCompilerPlugins)

    // zinc confuguration
    val zinc = project.getConfigurations.create("zinc")
    zinc.setVisible(false)
    zinc.setDescription("The Zinc incremental compiler to be used for this Scala project.")
    val zincVersion = scalaPluginExtension.getZincVersion.get
    log.debug(s"zincVersion = $zincVersion")
    zinc.defaultDependencies { dependencies =>
      dependencies.add(project.getDependencies.create(s"org.scala-sbt:zinc_$DEFAULT_SCALA_ZINC_VERSION:$zincVersion"))
      // maybe check if scala-library version has changed?
    }

    // not sure how incrementalScalaAnalysisElements is used
    // not sure how UsageDisambiguationRules works

    project.afterEvaluate { project => // variants are empty until evaluated
      val androidExt = project.getExtensions.getByType(classOf[BaseExtension])
      val androidBootClasspath = objectFactory.fileCollection
      androidBootClasspath.setFrom(androidExt.getBootClasspath) // this includes android.os package

      // sort is not really needed but it makes easier to debug
      val variants = androidExt.variants.toSeq.sortBy(_.toText) // toText is for example ApkVariant(debug) and ApkVariant(release

      for (variant <- variants) {
        val javaCompile = variant.javaCompile

        val compileVariantJavaWithJavacPattern = "compile(.*)JavaWithJavac".r
        val variantName = javaCompile.getName match {
          case compileVariantJavaWithJavacPattern(variantName) => variantName
          // TODO: Probably use Jill when Jack is configured rather than Javac https://stackoverflow.com/a/36984638
          case name => throw new Exception(s"unexpected javaCompile name: $name")
        }

        val scalaCompileProvider = project.getTasks.register(s"compile${variantName}Scala", classOf[ScalaCompile], { (scalaCompile: ScalaCompile) =>
          scalaCompile.getConventionMapping.map("scalaClasspath", { () =>
            scalaRuntime.inferScalaClasspath(scalaCompile.getClasspath)
          })
          scalaCompile.getConventionMapping.map("zincClasspath", { () =>
            zinc // project.getConfigurations.getAt("zinc")
          })
          scalaCompile.getConventionMapping.map("scalaCompilerPlugins", { () =>
            project.getConfigurations.getAt("scalaCompilerPlugins") // TODO: can scalaCompilerPlugins above used?
          })

          scalaCompile.setSource(deriveSource(variant, javaCompile, project))

          val incrementalAnalysis = project.getConfigurations().create(s"incrementalScalaAnalysis-$variantName")
          incrementalAnalysis.setVisible(false)
          incrementalAnalysis.setCanBeResolved(true)
          incrementalAnalysis.setCanBeConsumed(false)
          //incrementalAnalysis.extendsFrom
          incrementalAnalysis.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, incrementalAnalysisUsage)

          scalaCompile.getAnalysisMappingFile     .set(project.getLayout.getBuildDirectory.file(s"tmp/scala/compilerAnalysis/$variantName.mapping"));
          val incrementalOptions = scalaCompile.getScalaCompileOptions.getIncrementalOptions
          incrementalOptions.getAnalysisFile      .set(project.getLayout.getBuildDirectory.file(s"tmp/scala/compilerAnalysis/$variantName.analysis"))
          incrementalOptions.getClassfileBackupDir.set(project.getLayout.getBuildDirectory.file(s"tmp/scala/compilerAnalysis/$variantName.bak"))
          //incrementalOptions.getPublishedCode.set
          scalaCompile.getAnalysisFiles.from(incrementalAnalysis.getIncoming.artifactView { viewConfiguration =>
            viewConfiguration.lenient(true)
            //viewConfiguration.componentFilter(new IsProjectComponent)
          }.getFiles)

          //scalaCompile.dependsOn(scalaCompile.getAnalysisFiles) // https://github.com/gradle/gradle/issues/14434

          /*
            javaCompile.classpath contains android jars such as core-$ver.api.jar and appcompat-$ver-api.jar as well as androidx jars
            androidExt.bootClasspath contains platforms/android-$apiLevel/android.jar (android.os package) and build-tools/$buildToolVersion/core-lambda-stubs.jar
          */
          scalaCompile.setClasspath(javaCompile.getClasspath.plus(androidBootClasspath))
          scalaCompile.setDestinationDir(javaCompile.getDestinationDir) // compile into build/intermediates/javac directory like the Android plugin

          // inherit the dependencies from javaCompile
          scalaCompile.getDependsOn.addAll(javaCompile.getDependsOn)
        }: Action[ScalaCompile])

        log.info(s"${variant.toText}: substituting $javaCompile with ${scalaCompileProvider.get}") // TODO: don't wanna call get
        javaCompile.dependsOn(scalaCompileProvider)
        javaCompile.getActions.clear() // .java files are mix-compiled
      }


      // scaladoc
      project.getTasks.withType(classOf[ScalaDoc]).configureEach { scalaDoc => // TODO: are they present here?
        // FIXME: JavaPluginConvention is not available
/*
        scalaDoc.getConventionMapping.map("destinationDir", { () =>
          val docsDir = project.getConvention.getPlugin(JavaPluginConvention.class).getDocsDir
          project.file(docsDir.getPath + "/scaladoc")
        })
*/
        scalaDoc.getConventionMapping.map("title", { () =>
          project.getExtensions.getByType(classOf[ReportingExtension]).getApiDocTitle
        })
        scalaDoc.getConventionMapping.map("scalaClasspath", { () =>
          scalaRuntime.inferScalaClasspath(scalaDoc.getClasspath)
        })
      }
    }
  }

  def deriveSource(variant: BaseVariant, javaCompile: JavaCompile, project: Project): FileTree = {
    var results: FileCollection = javaCompile.getSource // this includes build/generated/source/buildConfig
    def addResult(dir: File): Unit = {
      val fileTree = project.fileTree(dir)
      fileTree.setIncludes(Seq(
        "**/*.java",
        "**/*.scala",
      ).asJava)
      results = results.plus(fileTree)
    }

    for (javaDir <- variant.sourceSets.flatMap(_.getJavaDirectories.asScala)) {
      addResult(javaDir)
      if (javaDir.getName == "java")
        addResult(javaDir.toPath.resolveSibling("scala").toFile)
    }

    results.getAsFileTree
  }
}
