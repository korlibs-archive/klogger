import com.soywiz.korlibs.*

apply<KorlibsPlugin>()

korlibs {
}

/*
> Task :klogger:watchosX86Test
Child process terminated with signal 10: Bus error
java.lang.IllegalStateException: command '/usr/bin/xcrun' exited with errors (exit code: 138)

> Task :klogger:allTests FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':klogger:allTests'.
> Failed to execute all tests:
  :klogger:watchosX86Test: java.lang.IllegalStateException: command '/usr/bin/xcrun' exited with errors (exit code: 138)

 */
tasks.getByName("watchosX86Test").enabled = false
