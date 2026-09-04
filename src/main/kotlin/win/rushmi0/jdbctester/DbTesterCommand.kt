package win.rushmi0.jdbctester

import picocli.CommandLine.Command
import picocli.CommandLine.Help.Ansi
import picocli.CommandLine.Option
import win.rushmi0.jdbctester.db.ConnectionConfig
import win.rushmi0.jdbctester.db.DatabaseTester
import win.rushmi0.jdbctester.report.ConsoleReporter
import java.util.concurrent.Callable

@Command(
    name = "jdbc-tester",
    mixinStandardHelpOptions = true,
    version = ["jdbc-tester 1.0.0"],
    description = ["Tests connectivity to an Oracle or SQL Server database."],
)
class DbTesterCommand : Callable<Int> {

    @Option(names = ["-t", "--type"], required = true, description = ["Database type: \${COMPLETION-CANDIDATES}"])
    lateinit var type: win.rushmi0.jdbctester.db.DbType

    @Option(names = ["-H", "--host"], required = true, description = ["Database host"])
    lateinit var host: String

    @Option(names = ["-P", "--port"], description = ["Database port (default: type-specific, e.g. 1521/1433)"])
    var port: Int? = null

    @Option(names = ["-d", "--database"], description = ["Database / service name (Oracle: or use --sid)"])
    var database: String? = null

    @Option(names = ["--sid"], description = ["Oracle SID, as an alternative to --database"])
    var sid: String? = null

    @Option(names = ["-u", "--username"], required = true, description = ["Database username"])
    lateinit var username: String

    @Option(names = ["-p", "--password"], description = ["Database password (or set the DB_PASSWORD env var)"])
    var password: String? = null

    override fun call(): Int {
        val resolvedPassword = password ?: System.getenv("DB_PASSWORD")
        if (resolvedPassword == null) {
            System.err.println("Password required: pass -p/--password or set the DB_PASSWORD env var.")
            return 2
        }

        val resolvedPort = port ?: type.defaultPort
        val jdbcUrl = runCatching { type.buildJdbcUrl(host, resolvedPort, database, sid) }
            .getOrElse { e ->
                System.err.println("Invalid arguments: ${e.message}")
                return 2
            }

        val config = ConnectionConfig(
            dbType = type,
            jdbcUrl = jdbcUrl,
            username = username,
            password = resolvedPassword,
            connectTimeoutSeconds = 10,
            queryTimeoutSeconds = 10,
            validationQuery = type.validationQuery,
        )

        System.err.println("INFO: Testing ${type.label} connection to $host:$resolvedPort")

        val result = DatabaseTester(config).run()
        ConsoleReporter(Ansi.AUTO).print(type, jdbcUrl, result)

        return if (result.success) 0 else 1
    }
}
