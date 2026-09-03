package win.rushmi0.jdbctester

import io.getstream.log.JvmStreamLogger
import io.getstream.log.Priority
import picocli.CommandLine
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    JvmStreamLogger.install(minPriority = Priority.INFO)

    val exitCode = CommandLine(DbTesterCommand())
        .setCaseInsensitiveEnumValuesAllowed(true)
        .execute(*args)

    exitProcess(exitCode)
}
