package com.soywiz.klogger.test

import com.soywiz.klogger.*
import kotlin.test.*

class ConsoleTest {
    @Test
    fun test() {
        Console.error("hello", "world")
        Console.warn("hello", "world")
        Console.log("hello", "world")
    }
}
