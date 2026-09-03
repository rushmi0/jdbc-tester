package win.rushmi0.jdbctester.db

/**
 * A database engine the tester knows how to reach, along with the JDBC
 * specifics (driver class, default port, health-check query) needed to
 * build a connection without the caller having to know the driver API.
 */
enum class DbType(
    val label: String,
    val defaultPort: Int,
    val driverClassName: String,
    val validationQuery: String,
) {
    ORACLE(
        label = "Oracle",
        defaultPort = 1521,
        driverClassName = "oracle.jdbc.OracleDriver",
        validationQuery = "SELECT 1 FROM DUAL",
    ) {
        override fun buildJdbcUrl(host: String, port: Int, database: String?, sid: String?): String {
            if (sid != null) return "jdbc:oracle:thin:@$host:$port:$sid"
            val serviceName = requireNotNull(database) {
                "Oracle requires --database (service name) or --sid"
            }
            return "jdbc:oracle:thin:@//$host:$port/$serviceName"
        }
    },
    MSSQL(
        label = "SQL Server",
        defaultPort = 1433,
        driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver",
        validationQuery = "SELECT 1",
    ) {
        override fun buildJdbcUrl(host: String, port: Int, database: String?, sid: String?): String {
            val databaseName = requireNotNull(database) { "SQL Server requires --database" }
            return "jdbc:sqlserver://$host:$port;databaseName=$databaseName;encrypt=true;trustServerCertificate=true"
        }
    },
    ;

    /** Builds a driver-appropriate JDBC URL from discrete connection parts. */
    abstract fun buildJdbcUrl(host: String, port: Int, database: String?, sid: String?): String
}
