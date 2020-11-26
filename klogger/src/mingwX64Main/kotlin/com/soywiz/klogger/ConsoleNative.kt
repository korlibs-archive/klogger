package com.soywiz.klogger

import platform.posix.*

actual inline fun Console.error(vararg msg: Any?) {
    fprintf(stderr, "%s\n", msg.joinToString(", "))
}

actual inline fun Console.log(vararg msg: Any?) {
	println(msg.joinToString(", "))
}

actual inline fun Console.warn(vararg msg: Any?) {
    println(msg.joinToString(", "))
}
