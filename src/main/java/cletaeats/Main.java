package cletaeats;

import cletaeats.filters.AuthFilter;
import cletaeats.filters.CorsFilter;
import cletaeats.filters.LoggingFilter;
import cletaeats.servlets.UsuarioServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        Path cwd = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path candidate = cwd.resolve("web-admin");
        if (candidate.toFile().exists()) {
            return candidate.toAbsolutePath().toString();
        }

        URI jarLocation = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        Path jarDir = Paths.get(jarLocation).getParent();
        Path[] possibleRoots = new Path[] {
                jarDir.resolve("web-admin"),
                jarDir.getParent() != null ? jarDir.getParent().resolve("web-admin") : jarDir.resolve("web-admin"),
                jarDir.getParent() != null && jarDir.getParent().getParent() != null ? jarDir.getParent().getParent().resolve("web-admin") : jarDir.resolve("web-admin")
        };

        for (Path path : possibleRoots) {
            Path normalized = path.toAbsolutePath().normalize();
            if (normalized.toFile().exists()) {
                return normalized.toString();
            }
        }

        throw new IllegalStateException("No se encontró la carpeta web-admin en el directorio actual ni relativo al JAR");
    }
}