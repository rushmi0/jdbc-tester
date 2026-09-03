package win.rushmi0.jdbctester.db

/** Resolved parameters for a single test run, independent of how they were parsed. */
data class ConnectionConfig(
    val dbType: DbType,
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val connectTimeoutSeconds: Int,
    val queryTimeoutSeconds: Int,
    val validationQuery: String,
)
