package com.soywiz.klogger

object LoggerManager {
	val loggers = LinkedHashMap<String, Logger>()
	var defaultLevel: LogLevel? = null

	fun getLogger(name: String) = loggers.getOrPut(name) { Logger(name, true) }

	fun setLevel(name: String, level: LogLevel) = getLogger(name).apply { this.level = level }

	fun setOutput(name: String, output: LoggerOutput) = getLogger(name).apply { this.output = output }

	var defaultOutput: LoggerOutput = ConsoleLoggerOutput
}

object ConsoleLoggerOutput : LoggerOutput {
	override fun output(logger: Logger, level: LogLevel, msg: String) {
		val line = "[${logger.name}]: $msg"
		when (level) {
			LogLevel.ERROR -> KloggerConsole.error(line)
			else -> KloggerConsole.log(line)
		}
	}

}

interface LoggerOutput {
	fun output(logger: Logger, level: LogLevel, msg: String)
}

enum class LogLevel(val index: Int) { NONE(0), FATAL(1), ERROR(2), WARN(3), INFO(4), DEBUG(5), TRACE(6) }

class Logger internal constructor(val name: String, val dummy: Boolean) {
	companion object {
		operator fun invoke(name: String) = LoggerManager.getLogger(name)
	}

	init {
		LoggerManager.loggers[name] = this
	}

	var level: LogLevel? = null
	var output: LoggerOutput? = null

	val processedLevel: LogLevel get() = level ?: LoggerManager.defaultLevel ?: LogLevel.WARN
	val processedOutput: LoggerOutput get() = output ?: LoggerManager.defaultOutput

	inline fun log(level: LogLevel, msg: () -> String) {
		if (isEnabled(level)) {
			processedOutput.output(this, level, msg())
		}
	}

	inline fun fatal(msg: () -> String) = log(LogLevel.FATAL, msg)
	inline fun error(msg: () -> String) = log(LogLevel.ERROR, msg)
	inline fun warn(msg: () -> String) = log(LogLevel.WARN, msg)
	inline fun info(msg: () -> String) = log(LogLevel.INFO, msg)
	inline fun debug(msg: () -> String) = log(LogLevel.DEBUG, msg)
	inline fun trace(msg: () -> String) = log(LogLevel.TRACE, msg)

	@Deprecated("potential performance problem", ReplaceWith("fatal { msg }"))
	fun fatal(msg: String) = fatal { msg }

	@Deprecated("potential performance problem", ReplaceWith("error { msg }"))
	fun error(msg: String) = error { msg }

	@Deprecated("potential performance problem", ReplaceWith("warn { msg }"))
	fun warn(msg: String) = warn { msg }

	@Deprecated("potential performance problem", ReplaceWith("info { msg }"))
	fun info(msg: String) = info { msg }

	@Deprecated("potential performance problem", ReplaceWith("debug { msg }"))
	fun debug(msg: String) = debug { msg }

	@Deprecated("potential performance problem", ReplaceWith("trace { msg }"))
	fun trace(msg: String) = trace { msg }

	inline fun isEnabled(level: LogLevel) = level.index <= processedLevel.index

	inline val isFatalEnabled get() = isEnabled(LogLevel.FATAL)
	inline val isErrorEnabled get() = isEnabled(LogLevel.ERROR)
	inline val isWarnEnabled get() = isEnabled(LogLevel.WARN)
	inline val isInfoEnabled get() = isEnabled(LogLevel.INFO)
	inline val isDebugEnabled get() = isEnabled(LogLevel.DEBUG)
	inline val isTraceEnabled get() = isEnabled(LogLevel.TRACE)
}
