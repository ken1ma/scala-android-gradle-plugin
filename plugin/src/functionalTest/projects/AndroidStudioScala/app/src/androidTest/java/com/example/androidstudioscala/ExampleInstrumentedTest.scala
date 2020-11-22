package com.example.androidstudioscala

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

// ScalaTest doesn't seem to be able to run in androidTest ([instrumented unit test](https://developer.android.com/training/testing/unit-testing/instrumented-unit-tests)) since it requires [java.lang.invoke.VarHandle.releaseFence](https://github.com/scala/scala/blob/v2.13.3/src/library/scala/runtime/Statics.java#L172)
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
