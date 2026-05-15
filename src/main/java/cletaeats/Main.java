package cletaeats;

import cletaeats.filters.AuthFilter;
import cletaeats.filters.CorsFilter;
import cletaeats.filters.LoggingFilter;
import cletaeats.servlets.UsuarioServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando Tomcat Embebido...");

        Tomcat tomcat = new Tomcat();

        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8080")
        );

        tomcat.setPort(port);
        tomcat.getConnector();

        // Crear un contexto base con el directorio actual del proceso.
        String baseDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().toString();
        Context ctx = tomcat.addContext("", baseDir);

        // 1. Registrar el Servlets
        Tomcat.addServlet(ctx, "UsuarioServlet", new UsuarioServlet());
        ctx.addServletMappingDecoded("/api/usuarios/*", "UsuarioServlet");

        Tomcat.addServlet(ctx, "ClienteServlet", new cletaeats.servlets.ClienteServlet());
        ctx.addServletMappingDecoded("/api/cliente/*", "ClienteServlet");

        Tomcat.addServlet(ctx, "RepartidorServlet", new cletaeats.servlets.RepartidorServlet());
        ctx.addServletMappingDecoded("/api/repartidor/*", "RepartidorServlet");

        Tomcat.addServlet(ctx, "AdminServlet", new cletaeats.servlets.AdminServlet());
        ctx.addServletMappingDecoded("/api/admin/*", "AdminServlet");

        Tomcat.addServlet(ctx, "RestauranteServlet", new cletaeats.servlets.RestauranteServlet());
        ctx.addServletMappingDecoded("/api/restaurantes/*", "RestauranteServlet");

        // Servir archivos estáticos del admin
        String webAdminPath = resolveWebAdminPath();
        Context adminCtx = tomcat.addContext("/admin", webAdminPath);
        Tomcat.addServlet(adminCtx, "default", "org.apache.catalina.servlets.DefaultServlet");
        adminCtx.addServletMappingDecoded("/", "default");

        // 0. Registrar Filtro de Logs (Primero de todos)
        FilterDef logFilterDef = new FilterDef();
        logFilterDef.setFilterName("LoggingFilter");
        logFilterDef.setFilterClass(LoggingFilter.class.getName());
        ctx.addFilterDef(logFilterDef);

        FilterMap logFilterMap = new FilterMap();
        logFilterMap.setFilterName("LoggingFilter");
        logFilterMap.addURLPattern("/*");
        ctx.addFilterMap(logFilterMap);

        // 2. Registrar Filtro CORS (Global)
        FilterDef corsFilterDef = new FilterDef();
        corsFilterDef.setFilterName("CorsFilter");
        corsFilterDef.setFilterClass(CorsFilter.class.getName());
        ctx.addFilterDef(corsFilterDef);

        FilterMap corsFilterMap = new FilterMap();
        corsFilterMap.setFilterName("CorsFilter");
        corsFilterMap.addURLPattern("/*");
        ctx.addFilterMap(corsFilterMap);

        // 3. Registrar Filtro JWT (Protegido)
        FilterDef authFilterDef = new FilterDef();
        authFilterDef.setFilterName("AuthFilter");
        authFilterDef.setFilterClass(AuthFilter.class.getName());
        ctx.addFilterDef(authFilterDef);

        FilterMap authFilterMap = new FilterMap();
        authFilterMap.setFilterName("AuthFilter");
        authFilterMap.addURLPattern("/api/*");
        ctx.addFilterMap(authFilterMap);

        // 4. Arrancar el servidor
        tomcat.start();
        System.out.println("Servidor corriendo en http://localhost:" + port);
        tomcat.getServer().await(); // Mantiene el programa en ejecución
    }

    private static String resolveWebAdminPath() throws Exception {
        String envPath = System.getenv("WEB_ADMIN_PATH");
        if (envPath != null && !envPath.isBlank()) {
            Path envDir = Paths.get(envPath).toAbsolutePath().normalize();
            if (Files.isDirectory(envDir)) {
                return envDir.toString();
            }
            throw new IllegalStateException("WEB_ADMIN_PATH está definido pero no apunta a un directorio válido: " + envDir);
        }

        Path cwd = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path jarLocation = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path jarDir = jarLocation.getParent();

        List<Path> possibleRoots = List.of(
                cwd.resolve("web-admin"),
                jarDir.resolve("web-admin"),
                jarDir.getParent() != null ? jarDir.getParent().resolve("web-admin") : jarDir.resolve("web-admin"),
                jarDir.getParent() != null && jarDir.getParent().getParent() != null ? jarDir.getParent().getParent().resolve("web-admin") : jarDir.resolve("web-admin")
        );

        for (Path candidate : possibleRoots) {
            Path normalized = candidate.toAbsolutePath().normalize();
            if (Files.isDirectory(normalized)) {
                return normalized.toString();
            }
        }

        throw new IllegalStateException("No se encontró la carpeta web-admin. Se buscaron estas rutas: " + possibleRoots);
    }
}