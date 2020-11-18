package jp.ken1ma.scala.android.gradle
package plugin

import org.gradle.api.logging.{Logger, LogLevel}

trait GradleLogHelper {
  def logger: Logger

  val debugLevel = LogLevel.DEBUG
  val infoLevel  = LogLevel.INFO
  val warnLevel  = LogLevel.WARN
  val errorLevel = LogLevel.ERROR

  /*
    Scala 2.13: logger.info("message") results in
      [Error] ...: ambiguous reference to overloaded definition,
      both method info in trait Logger of type (x$1: String, x$2: Object*): Unit
      and  method info in trait Logger of type (x$1: String): Unit
  */
  object log {
    def debug(message: String): Unit = logger.log(debugLevel, message)
    def info (message: String): Unit = logger.log(infoLevel , message)
    def warn (message: String): Unit = logger.log(warnLevel , message)
    def error(message: String): Unit = logger.log(errorLevel, message)

    def debug(message: String, ex: Throwable): Unit = logger.log(debugLevel, message, ex)
    def info (message: String, ex: Throwable): Unit = logger.log(infoLevel , message, ex)
    def warn (message: String, ex: Throwable): Unit = logger.log(warnLevel , message, ex)
    def error(message: String, ex: Throwable): Unit = logger.log(errorLevel, message, ex)
  }
}
