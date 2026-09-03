package win.rushmi0.jdbctester.report

import picocli.CommandLine.Help.Ansi
import win.rushmi0.jdbctester.db.DbType
import win.rushmi0.jdbctester.db.SuiteResult

/** Renders a [SuiteResult] as a human-readable, optionally colored, report. */
class ConsoleReporter(private val ansi: Ansi) {

    fun print(dbType: DbType, jdbcUrl: String, result: SuiteResult) {
        val out = StringBuilder()
        out.appendLine(ansi.string("@|bold JDBC connection test: ${dbType.label} -> $jdbcUrl|@"))
        out.appendLine()

        result.steps.forEach { step ->
            val status = if (step.passed) "@|bold,green PASS|@" else "@|bold,red FAIL|@"
            out.appendLine(ansi.string("[$status] ${step.name} (${step.durationMillis} ms)"))
            step.detail?.let { out.appendLine("       $it") }
            step.error?.let {
                out.appendLine(ansi.string("       @|red ${it.javaClass.simpleName}: ${it.message}|@"))
            }
        }

        out.appendLine()
        val passedCount = result.steps.count { it.passed }
        val summary = if (result.success) {
            "@|bold,green ALL CHECKS PASSED|@"
        } else {
            "@|bold,red ${result.steps.size - passedCount} CHECK(S) FAILED|@"
        }
        out.append(ansi.string("Summary: $passedCount/${result.steps.size} steps passed - $summary"))

        println(out)
    }
}
