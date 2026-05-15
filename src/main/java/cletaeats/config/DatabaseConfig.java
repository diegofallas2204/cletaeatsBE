package cletaeats.config;

/**
 * Configuración centralizada de la base de datos MySQL.
 */
public class DatabaseConfig {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_NAME = "cletaeats";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "root";

    public static final String URL = System.getenv().getOrDefault(
            "JDBC_URL",
            String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC&useSSL=false",
                    System.getenv().getOrDefault("DB_HOST", DEFAULT_HOST),
                    System.getenv().getOrDefault("DB_PORT", DEFAULT_PORT),
                    System.getenv().getOrDefault("DB_NAME", DEFAULT_NAME))
    );

    public static final String USER = System.getenv().getOrDefault("DB_USER", DEFAULT_USER);
    public static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);
}
