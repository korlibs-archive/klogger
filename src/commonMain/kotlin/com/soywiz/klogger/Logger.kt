package com.soywiz.klogger

import com.soywiz.klogger.internal.*
import kotlinx.atomicfu.*

/**
 * Utility to log messages.
 */
class Logger private constructor(val name: String, val dummy: Boolean) {
    init {
        // @TODO: kotlin-native this produces a freeze error
        if (!isNative) {
            Logger.loggers += mapOf(name to this)
        }
    }

    private var _level: Level? by atomic<Level?>(null)
    private var _output: Output? by atomic<Output?>(null)

    /** [Level] of this [Logger]. If not set, it will use the [Logger.defaultLevel] */
    var level: Level
        set(value) = run { _level = value }
        get() = _level ?: Logger.defaultLevel ?: Level.WARN

    /** [Output] of this [Logger]. If not set, it will use the [Logger.defaultOutput] */
    var output: Output
        set(value) = run { _output = value }
        get() = _output ?: Logger.defaultOutput

    /** Check if the [level] is set for this [Logger] */
    val isLocalLevelSet: Boolean get() = _level != null

    /** Check if the [output] is set for this [Logger] */
    val isLocalOutputSet: Boolean get() = _output != null

    companion object {
        private var loggers: Map<String, Logger> by atomic(mapOf())

        /** The default [Level] used for all [Logger] that doesn't have its [Logger.level] set */
        var defaultLevel: Level? by atomic<Level?>(null)
        /** The default [Output] used for all [Logger] that doesn't have its [Logger.output] set */
        var defaultOutput: Output by atomic<Output>(ConsoleLogOutput)

        /** Gets a [Logger] from its [name] */
        operator fun invoke(name: String) = loggers[name] ?: Logger(name, true)
    }

    /** Logging [Level] */
    enum class Level(val index: Int) {
        NONE(0), FATAL(1), ERROR(2),
        WARN(3), INFO(4), DEBUG(5), TRACE(6)
    }

    /** Logging [Output] to handle logs */
    interface Output {
        fun output(logger: Logger, level: Logger.Level, msg: Any?)
    }

    /** Default [Output] to emit logs over the [Console] */
    object ConsoleLogOutput : Logger.Output {
        override fun output(logger: Logger, level: Logger.Level, msg: Any?) {
            when (level) {
                Logger.Level.ERROR -> Console.error(logger.name, msg)
                else -> Console.log(logger.name, msg)
            }
        }
    }

    /** Returns if this [Logger] has at least level [Level] */
    fun isEnabled(level: Level) = level.index <= this.level.index

    /** Returns if this [Logger] has at least level [Level.FATAL] */
    inline val isFatalEnabled get() = isEnabled(Level.FATAL)
    /** Returns if this [Logger] has at least level [Level.ERROR] */
    inline val isErrorEnabled get() = isEnabled(Level.ERROR)
    /** Returns if this [Logger] has at least level [Level.WARN] */
    inline val isWarnEnabled get() = isEnabled(Level.WARN)
    /** Returns if this [Logger] has at least level [Level.INFO] */
    inline val isInfoEnabled get() = isEnabled(Level.INFO)
    /** Returns if this [Logger] has at least level [Level.DEBUG] */
    inline val isDebugEnabled get() = isEnabled(Level.DEBUG)
    /** Returns if this [Logger] has at least level [Level.TRACE] */
    inline val isTraceEnabled get() = isEnabled(Level.TRACE)

    /** Traces the lazily executed [msg] if the [Logger.level] is at least [level] */
    inline fun log(level: Level, msg: () -> Any?) = run { if (isEnabled(level)) actualLog(level, msg()) }

    /** Traces the lazily executed [msg] if the [Logger.level] is at least [Level.FATAL] */
    inline fun fatal(msg: () -> Any?) = log(Level.FATAL, msg)
    /** Traces the lazily executed [msg] if the [Logger.level] is at least [Level.ERROR] */
    inline fun error(msg: () -> Any?) = log(Level.ERROR, msg)
    /** Traces the lazily executed [msg] if the [Logger.level] is at least [Level.WARN] */
    inline fun warn(msg: () -> Any?) = log(Level.WARN, msg)
    /** Traces the lazily executed [msg] if the [Logger.level] is at least [Level.INFO] */
    inline fun info(msg: () -> Any?) = log(Level.INFO, msg)
    /** Traces the lazily executed [msg] if the [Logger.level] is at least [Level.DEBUG] */
    inline fun debug(msg: () -> Any?) = log(Level.DEBUG, msg)
    /** Traces the lazily executed [msg] if the [Logger.level] is at least [Level.TRACE] */
    inline fun trace(msg: () -> Any?) = log(Level.TRACE, msg)

    @Deprecated("Potential performance problem. Use inline to lazily compute the message.", ReplaceWith("fatal { msg }"))
    fun fatal(msg: String) = fatal { msg }
    @Deprecated("potential performance problem. Use inline to lazily compute the message.", ReplaceWith("error { msg }"))
    fun error(msg: String) = error { msg }
    @Deprecated("potential performance problem. Use inline to lazily compute the message.", ReplaceWith("warn { msg }"))
    fun warn(msg: String) = warn { msg }
    @Deprecated("potential performance problem. Use inline to lazily compute the message.", ReplaceWith("info { msg }"))
    fun info(msg: String) = info { msg }
    @Deprecated("potential performance problem. Use inline to lazily compute the message.", ReplaceWith("debug { msg }"))
    fun debug(msg: String) = debug { msg }
    @Deprecated("potential performance problem. Use inline to lazily compute the message.", ReplaceWith("trace { msg }"))
    fun trace(msg: String) = trace { msg }

    @PublishedApi
    internal fun actualLog(level: Level, msg: Any?) = run { output.output(this, level, msg) }
}

