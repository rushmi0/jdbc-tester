package win.rushmi0.jdbctester

import picocli.CommandLine
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val exitCode = CommandLine(DbTesterCommand())
        .setCaseInsensitiveEnumValuesAllowed(true)
        .execute(*args)

    exitProcess(exitCode)
}
