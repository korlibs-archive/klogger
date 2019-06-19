package com.soywiz.korlibs.targets

import com.soywiz.korlibs.gkotlin
import com.soywiz.korlibs.hasAndroid
import com.soywiz.korlibs.tasks
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Project

fun Project.configureTargetNative() {
    gkotlin.apply {
        iosX64()
        iosArm32()
        iosArm64()
        macosX64()
        linuxX64()
        mingwX64()

        if (System.getProperty("idea.version") != null) {
            when {
                Os.isFamily(Os.FAMILY_WINDOWS) -> run { mingwX64("nativeCommon"); mingwX64("nativePosix") }
                Os.isFamily(Os.FAMILY_MAC) -> run { macosX64("nativeCommon"); macosX64("nativePosix") }
                else -> run { linuxX64("nativeCommon"); linuxX64("nativePosix") }
            }
        }

        sourceSets.apply {
            fun dependants(name: String, on: Set<String>) {
                val main = maybeCreate("${name}Main")
                val test = maybeCreate("${name}Test")
                for (o in on) {
                    maybeCreate("${o}Main").dependsOn(main)
                    maybeCreate("${o}Test").dependsOn(test)
                }
            }

            val none = setOf<String>()
            val android = if (hasAndroid) setOf() else setOf("android")
            val jvm = setOf("jvm")
            val js = setOf("js")
            val ios = setOf("iosX64", "iosArm32", "iosArm64")
            val macos = setOf("macosX64")
            val linux = setOf("linuxX64")
            val mingw = setOf("mingwX64")
            val apple = ios + macos
            val allNative = apple + linux + mingw
            val jvmAndroid = jvm + android
            val allTargets = allNative + js + jvm + android

            dependants("iosCommon", ios)
            dependants("nativeCommon", allNative)
            dependants("nonNativeCommon", allTargets - allNative)
            dependants("nativePosix", allNative - mingw)
            dependants("nativePosixNonApple", allNative - mingw - apple)
            dependants("nativePosixApple", apple)
            dependants("nonJs", allTargets - js)
        }
    }

    afterEvaluate {
        for (target in listOf("macosX64", "linuxX64", "mingwX64")) {
            tasks {
                tasks.getByName("${target}Test")
            }
        }
    }
}
