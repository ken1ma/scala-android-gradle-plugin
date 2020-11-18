# Android Scala Gradle Plugin

This plugin
1. should compile `.scala` and `.java` files like the Gradle `scala` plugin
1. augments the Android Gradle plugin (`com.android.application`) like `kotlin-android` plugin
1. does not depend on sbt/mill and reluctantly embraces Gradle so that it can keep up with Android SDK


Example Scala code

    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle

    class MainActivity extends AppCompatActivity {
      override def onCreate(savedInstanceState: Bundle) = {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Android resources can be referenced
      }
    }

Example ScalaTest code

    import org.scalatest.freespec.AnyFreeSpec
    import org.scalatestplus.junit.JUnitRunner
    import org.junit.runner.RunWith

    @RunWith(classOf[JUnitRunner])
    class ExampleUnitTest extends AnyFreeSpec {
      "addition" - {
        "correct" in {
          assert(2 + 2 == 4)
        }
      }
    }

Tasks implemented by the plugin:

|Name|Type|Description|
|--|--|--|
|compile*Variant*Scala|[ScalaCompile](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.scala.ScalaCompile.html) ([github](https://github.com/gradle/gradle/blob/master/subprojects/scala/src/main/java/org/gradle/api/tasks/scala/ScalaCompile.java))|A task is created for each variant|

## References

1. [Gradle scala](https://docs.gradle.org/current/userguide/scala_plugin.html) plugin ([github](https://github.com/gradle/gradle/tree/master/subprojects/scala))
1. [com.android.application](https://developer.android.com/studio/build) plugin ([release notes](https://developer.android.com/studio/releases/gradle-plugin), [maven](https://maven.google.com/web/index.html?q=gradle#com.android.tools.build:gradle:4.1.0), [git](https://android.googlesource.com/platform/tools/base/+/refs/tags/studio-4.1.0/build-system/gradle-core/src/main/java/com/android/build/gradle/internal/plugins/))
1. [kotlin-android](https://developer.android.com/kotlin/add-kotlin) plugin ([github](https://github.com/JetBrains/kotlin/tree/master/libraries/tools/kotlin-gradle-plugin), [tasks](https://github.com/JetBrains/kotlin/blob/master/libraries/tools/kotlin-gradle-plugin/Module.md))


# Limitations

1. In `android` configuration, `minSdkVersion` must be `26` or higher, where `invokedyamic` (`MethodHandle.invoke`) is supported.
    1. [Scala 2.12 and later uses invokedynamic bytecode](https://www.scala-lang.org/news/2.12.0/)
    1. Kotlin avoids this limitation by [generating necessary classes for lambdas](https://jakewharton.com/r8-optimization-lambda-groups/)

1. In `android` configuration, `lintOptions.abortOnError` must be `false`, to ignore `Error: Class referenced in the manifest, $MainActivity, was not found in the project or the libraries [MissingClass]`
    1. For example in `app/build.gradle`

            android {
                ...
                lintOptions {
                    abortOnError false
                }
            }

    1. The Android lint seems to be looking for MainActivity.java
        1. I wonder why Kotlin doesn't have this problem
        1. lint classpath seems [complex](https://android.googlesource.com/platform/tools/base/+/studio-master-dev/lint/libs/lint-gradle-api/)

1. ScalaTest doesn't seem to be able to run in [instrumented unit test](https://developer.android.com/training/testing/unit-testing/instrumented-unit-tests) (androidTest) since it requires [java.lang.invoke.VarHandle.releaseFence](https://github.com/scala/scala/blob/v2.13.3/src/library/scala/runtime/Statics.java#L172)


# Applying the plugin

Assuming an Android Java project that can be built with Android Studio 4.x

## Upgrade Gradle

    ./gradlew wrapper --gradle-version=6.7.1

1. With Gradle 6.5, `Extension of type 'BaseExtension' does not exist. Currently registered extension types: [ExtraPropertiesExtension, DefaultArtifactPublicationSet, SourceSetContainer, ReportingExtension, JavaPluginExtension, JavaInstallationRegistry, JavaToolchainService, NamedDomainObjectContainer<BaseVariantOutput>, BaseAppModuleExtension, ScalaRuntimeForAndroid, ScalaPluginExtension]` happens

## Add the plugin as a dependency

In `build.gradle`

    dependencies {
        classpath "com.android.tools.build:gradle:4.1.1"
        // TODO
    }

## Apply the plugin to the project

In `app/build.gradle`

    plugins {
        id 'com.android.application'
        id 'jp.ken1ma.scala.android.gradle.plugin'
        ...
    }

    ...

    dependencies {
        implementation 'org.scala-lang:scala-library:2.13.3'
        testImplementation 'org.scalatest:scalatest_2.13:3.2.3'
        testImplementation 'org.scalatestplus:junit-4-13_2.13:3.2.3.0' // for org.scalatest.junit.JUnitRunner
        ...
        // testImplementation 'junit:junit:4.+' can be removed
    }


# Building the plugin

## Prerequisites

1. macOS / Linux / Windows 10
2. Java 11.0.9 LTS

## Build and whitebox test

    ./gradlew test

1. [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) automatically downloads [Gradle](https://github.com/gradle/gradle)

## Blackbox test

    ./gradlew functionalTest
