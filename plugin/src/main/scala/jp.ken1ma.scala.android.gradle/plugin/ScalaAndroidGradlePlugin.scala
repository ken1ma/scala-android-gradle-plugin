package jp.ken1ma.scala.android.gradle.plugin

import org.gradle.api.{Plugin, Project}

class ScalaAndroidGradlePlugin extends Plugin[Project] {
  def apply(project: Project): Unit = {
    project.getTasks.register("compileVariantScala", task => {
      task.doLast(s => println("Hello from plugin 'jp.ken1ma.scala.android.gradle.plugin'"))
    })
  }
}
