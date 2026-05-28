package cletaeats.config;

/**
 * Configuración centralizada de la base de datos MySQL.
 * Compatible con Railway usando variables de entorno.
 */
public class DatabaseConfig {

    // Variables obtenidas desde Railway
    private static final String HOST =
            System.getenv().getOrDefault("MYSQLHOST", "localhost");

    private static final String PORT =
            System.getenv().getOrDefault("MYSQLPORT", "3306");

    private static final String DATABASE =
            System.getenv().getOrDefault("MYSQLDATABASE", "railway");

    public static final String USER =
            System.getenv().getOrDefault("MYSQLUSER", "root");

    public static final String PASSWORD =
            System.getenv().getOrDefault("MYSQLPASSWORD", "");

    // URL JDBC
    public static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
            HOST,
            PORT,
            DATABASE
    );

    // Método opcional para debug
    public static void printConfig() {
        System.out.println("========== DATABASE CONFIG ==========");
        System.out.println("HOST: " + HOST);
        System.out.println("PORT: " + PORT);
        System.out.println("DATABASE: " + DATABASE);
        System.out.println("USER: " + USER);
        System.out.println("URL: " + URL);
        System.out.println("=====================================");
    }
}
/*
package cletaeats.config;

*/
/**
 * Configuración centralizada de la base de datos MySQL.
 *//*

public class DatabaseConfig {
    private static final String DEFAULT_HOST = "yamabiko.proxy.rlwy.net";
    private static final String DEFAULT_PORT = "14589";
    private static final String DEFAULT_NAME = "railway";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "EIEnYaxDAEvxiTYtaDNZynMpvPeXnzLx";

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
*/
