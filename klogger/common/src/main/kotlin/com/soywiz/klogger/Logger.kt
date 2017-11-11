package com.soywiz.klogger

object LoggerManager {
	val loggers = LinkedHashMap<String, Logger>()
	var defaultLevel: LogLevel? = null

	fun getLogger(name: String) = loggers.getOrPut(name) { Logger(name, true) }

	fun setLevel(name: String, level: LogLevel) = getLogger(name).apply { this.level = level }
}

enum class LogLevel(val index: Int) { NONE(0), FATAL(1), ERROR(2), WARN(3), INFO(4), TRACE(5) }

class Logger internal constructor(val name: String, val dummy: Boolean) {
	companion object {
		operator fun invoke(name: String) = LoggerManager.getLogger(name)
	}

	init {
		LoggerManager.loggers[name] = this
	}

	var level: LogLevel? = null

	val processedLevel: LogLevel get() = level ?: LoggerManager.defaultLevel ?: LogLevel.WARN

	@PublishedApi
	internal fun actualLog(level: LogLevel, msg: String) {
		val line = "[$name]: $msg"
		when (level) {
			LogLevel.ERROR -> KloggerConsole.error(line)
			else -> KloggerConsole.log(line)
		}
	}

	inline fun log(level: LogLevel, msg: () -> String) {
		if (level.index <= processedLevel.index) {
			actualLog(level, msg())
		}
	}

	fun fatal(msg: String) = log(LogLevel.FATAL) { msg }
	fun error(msg: String) = log(LogLevel.ERROR) { msg }
	fun warn(msg: String) = log(LogLevel.WARN) { msg }
	fun info(msg: String) = log(LogLevel.INFO) { msg }
	fun trace(msg: String) = log(LogLevel.TRACE) { msg }

	inline fun fatal(msg: () -> String) = log(LogLevel.FATAL, msg)
	inline fun error(msg: () -> String) = log(LogLevel.ERROR, msg)
	inline fun warn(msg: () -> String) = log(LogLevel.WARN, msg)
	inline fun info(msg: () -> String) = log(LogLevel.INFO, msg)
	inline fun trace(msg: () -> String) = log(LogLevel.TRACE, msg)
}
