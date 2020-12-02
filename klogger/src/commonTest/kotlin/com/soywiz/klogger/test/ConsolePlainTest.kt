package com.soywiz.klogger.test

import com.soywiz.klogger.*
import kotlin.test.*

class ConsolePlainTest {
    @Test
    fun test() {
        Console.log("log", "hello", "world", 42)
        Console.warn("warn", "hello", "world", 42)
        Console.error("warn", "hello", "world", 42)
    }
}
