package win.rushmi0.jdbctester.db

import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties

/**
 * Runs a full connectivity health-check against one database: driver load,
 * connect, validity check, metadata, a validation query, and a transactional
 * round-trip. Every step is recorded even when a later one fails, so the
 * caller always gets a complete picture of what was and wasn't reachable.
 */
class DatabaseTester(private val config: ConnectionConfig) {

    fun run(): SuiteResult {
        val steps = mutableListOf<StepResult>()

        step(steps, "Load JDBC driver") {
            Class.forName(config.dbType.driverClassName)
            Unit to "Driver class: ${config.dbType.driverClassName}"
        } ?: return SuiteResult(steps)

        val connection = step(steps, "Open connection") {
            DriverManager.setLoginTimeout(config.connectTimeoutSeconds)
            val props = Properties().apply {
                setProperty("user", config.username)
                setProperty("password", config.password)
            }
            DriverManager.getConnection(config.jdbcUrl, props) to "Connected to ${config.jdbcUrl}"
        } ?: return SuiteResult(steps)

        try {
            runDiagnostics(connection, steps)
        } finally {
            step(steps, "Close connection") {
                connection.close()
                Unit to null
            }
        }

        return SuiteResult(steps)
    }

    private fun runDiagnostics(connection: Connection, steps: MutableList<StepResult>) {
        step(steps, "Validate connection") {
            check(connection.isValid(config.connectTimeoutSeconds)) { "Connection#isValid returned false" }
            Unit to null
        }

        step(steps, "Read database metadata") {
            val meta = connection.metaData
            val detail = "${meta.databaseProductName} ${meta.databaseProductVersion}; " +
                "driver ${meta.driverName} ${meta.driverVersion}; " +
                "JDBC ${meta.jdbcMajorVersion}.${meta.jdbcMinorVersion}"
            Unit to detail
        }

        step(steps, "Run validation query") {
            connection.createStatement().use { stmt ->
                stmt.queryTimeout = config.queryTimeoutSeconds
                stmt.executeQuery(config.validationQuery).use { rs ->
                    val detail = if (rs.next()) "\"${config.validationQuery}\" -> ${rs.getString(1)}"
                    else "\"${config.validationQuery}\" returned no rows"
                    Unit to detail
                }
            }
        }

        step(steps, "Transaction round-trip (rollback)") {
            connection.autoCommit = false
            try {
                connection.createStatement().use { stmt ->
                    stmt.queryTimeout = config.queryTimeoutSeconds
                    stmt.executeQuery(config.validationQuery).use { it.next() }
                }
                connection.rollback()
                Unit to "BEGIN -> query -> ROLLBACK succeeded"
            } finally {
                connection.autoCommit = true
            }
        }
    }

    private inline fun <T> step(
        steps: MutableList<StepResult>,
        name: String,
        block: () -> Pair<T, String?>,
    ): T? {
        val start = System.nanoTime()
        return runCatching(block).fold(
            onSuccess = { (value, detail) ->
                steps += StepResult(name, passed = true, durationMillis = elapsedMs(start), detail = detail)
                value
            },
            onFailure = { e ->
                steps += StepResult(name, passed = false, durationMillis = elapsedMs(start), error = e)
                null
            },
        )
    }

    private fun elapsedMs(startNanos: Long): Long = (System.nanoTime() - startNanos) / 1_000_000
}
