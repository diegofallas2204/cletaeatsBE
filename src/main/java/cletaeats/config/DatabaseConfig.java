package cletaeats.config;

/**
 * Configuración centralizada de MySQL.
 * Prioridad de variables:
 * 1) JDBC_URL (URL JDBC completa)
 * 2) MYSQL_URL / DATABASE_URL (URL tipo mysql://...)
 * 3) MYSQLHOST, MYSQLPORT, MYSQLDATABASE, MYSQLUSER, MYSQLPASSWORD (Railway)
 * 4) DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD (Render / legacy)
 * 5) Valores por defecto para desarrollo local (localhost / cletaeats)
 */
public final class DatabaseConfig {

    private static final String HOST = firstNonBlank(
            System.getenv("MYSQLHOST"),
            System.getenv("DB_HOST"),
            "localhost"
    );

    private static final String PORT = firstNonBlank(
            System.getenv("MYSQLPORT"),
            System.getenv("DB_PORT"),
            "3306"
    );

    private static final String DATABASE = firstNonBlank(
            System.getenv("MYSQLDATABASE"),
            System.getenv("DB_NAME"),
            "cletaeats"
    );

    public static final String USER = firstNonBlank(
            System.getenv("MYSQLUSER"),
            System.getenv("DB_USER"),
            "root"
    );

    public static final String PASSWORD = firstNonBlank(
            System.getenv("MYSQLPASSWORD"),
            System.getenv("DB_PASSWORD"),
            ""
    );

    public static final String URL = resolveJdbcUrl();

    private DatabaseConfig() {
    }

    private static String resolveJdbcUrl() {
        String jdbcUrl = firstNonBlank(
                System.getenv("JDBC_URL"),
                toJdbcUrl(System.getenv("MYSQL_URL")),
                toJdbcUrl(System.getenv("DATABASE_URL"))
        );
        if (jdbcUrl != null) {
            return jdbcUrl;
        }
        return String.format(
                "jdbc:mysql://%s:%s/%s?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                HOST,
                PORT,
                DATABASE
        );
    }

    /**
     * Convierte mysql://user:pass@host:port/db a URL JDBC.
     */
    private static String toJdbcUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }
        if (rawUrl.startsWith("jdbc:")) {
            return rawUrl;
        }
        if (rawUrl.startsWith("mysql://")) {
            return "jdbc:" + rawUrl + (rawUrl.contains("?") ? "&" : "?")
                    + "serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
        }
        return null;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    public static void printConfig() {
        System.out.println("========== DATABASE CONFIG ==========");
        System.out.println("HOST: " + HOST);
        System.out.println("PORT: " + PORT);
        System.out.println("DATABASE: " + DATABASE);
        System.out.println("USER: " + USER);
        System.out.println("PASSWORD: " + (PASSWORD.isEmpty() ? "(vacío)" : "********"));
        System.out.println("URL: " + URL);
        System.out.println("=====================================");
    }

    /** Prueba rápida de conexión al arrancar el servidor. */
    public static boolean testConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (var conn = java.sql.DriverManager.getConnection(URL, USER, PASSWORD)) {
                return conn.isValid(3);
            }
        } catch (Exception e) {
            System.err.println("CletaEats: fallo de conexión a MySQL: " + e.getMessage());
            return false;
        }
    }
}
