/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package jp.ken1ma.scala.android.gradle.plugin;

import org.gradle.api.Project;
import org.gradle.api.Plugin;

/**
 * A simple 'hello world' plugin.
 */
public class ScalaAndroidGradlePlugin implements Plugin<Project> {
    public void apply(Project project) {
        // Register a task
        project.getTasks().register("compileVariantScala", task -> {
            task.doLast(s -> System.out.println("Hello from plugin 'jp.ken1ma.scala.android.gradle.plugin'"));
        });
    }
}
