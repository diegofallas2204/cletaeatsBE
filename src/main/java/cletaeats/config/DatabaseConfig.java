package cletaeats.config;

/**
 * Configuración centralizada de la base de datos MySQL.
 * Lee desde variables de entorno en producción, con fallback a valores locales.
 *
 * Variables de entorno esperadas en el servidor:
 *   DB_URL      → ej: jdbc:mysql://host:3306/cletaeats?serverTimezone=UTC
 *   DB_USER     → usuario de la base de datos
 *   DB_PASSWORD → contraseña de la base de datos
 */
public class DatabaseConfig {
    public static final String URL = getEnv("DB_URL",
            "jdbc:mysql://localhost:3306/cletaeats?serverTimezone=UTC");
    public static final String USER = getEnv("DB_USER", "root");
    public static final String PASSWORD = getEnv("DB_PASSWORD", "root");

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
