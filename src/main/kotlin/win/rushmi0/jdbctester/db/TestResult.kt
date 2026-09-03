package win.rushmi0.jdbctester.db

/** Outcome of one diagnostic step (load driver, connect, run query, ...). */
data class StepResult(
    val name: String,
    val passed: Boolean,
    val durationMillis: Long,
    val detail: String? = null,
    val error: Throwable? = null,
)

/** Everything observed during one invocation of [win.rushmi0.jdbctester.db.DatabaseTester.run]. */
data class SuiteResult(val steps: List<StepResult>) {
    val success: Boolean get() = steps.all { it.passed }
}
