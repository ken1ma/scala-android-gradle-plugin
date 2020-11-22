package com.example.androidstudioscala

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

// ScalaTest doesn't seem to be able to run in androidTest (see README.md)
import org.junit.Test
import org.junit.Assert._
import org.junit.runner.RunWith

/** Instrumented test, which will execute on an Android device. */
@RunWith(classOf[AndroidJUnit4])
class ExampleInstrumentedTest {
  @Test def useAppContext(): Unit = {
    val appContext = InstrumentationRegistry.getInstrumentation.getTargetContext
    assertEquals(appContext.getPackageName, "com.example.androidstudioscala")
  }
}
