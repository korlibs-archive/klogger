package com.soywiz.klogger

actual object KloggerConsole {
	actual fun error(msg: Any?) {
		System.err.println(msg)
	}

	actual fun log(msg: Any?) {
		System.out.println(msg)
	}
}