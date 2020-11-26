package com.soywiz.klogger

import platform.Foundation.*

actual inline fun Console.error(vararg msg: Any?) {
    //NSLog("ERROR: %@", msg.joinToString(", "))
    NSLog("ERROR: ---")
}

actual inline fun Console.log(vararg msg: Any?) {
    //NSLog("LOG: %@", msg.joinToString(", "))
    NSLog("LOG: ---")
}

actual inline fun Console.warn(vararg msg: Any?) {
    //NSLog("WARN: %@", msg.joinToString(", "))
    NSLog("WARN: ---")
}
