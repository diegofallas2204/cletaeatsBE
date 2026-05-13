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

public class Main {
    public static void main(String[] args) throws Exception {

        // Puerto: Railway/Render lo asignan via variable de entorno PORT
        int port = 8080;
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.isBlank()) {
            port = Integer.parseInt(portEnv);
        }

        System.out.println("Iniciando CletaEats Backend en puerto " + port + "...");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // Inicializa el conector por defecto

        // Crear un contexto base para los servlets de la API
        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());

        // 1. Registrar Servlets
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

        // Servir archivos estáticos del panel admin
        // Busca web-admin relativo al directorio actual (funciona en dev y en servidor)
        String webAdminPath = resolveWebAdminPath();
        System.out.println("Sirviendo web-admin desde: " + webAdminPath);
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
        System.out.println("✅ Servidor CletaEats corriendo en http://0.0.0.0:" + port);
        System.out.println("   API:   http://0.0.0.0:" + port + "/api/");
        System.out.println("   Admin: http://0.0.0.0:" + port + "/admin/");
        tomcat.getServer().await(); // Mantiene el programa en ejecución
    }

    /**
     * Resuelve la ruta del directorio web-admin.
     * Busca en el directorio actual primero, luego en el directorio del JAR.
     */
    private static String resolveWebAdminPath() {
        // 1. Buscar relativo al directorio de trabajo actual
        File local = new File("web-admin");
        if (local.exists() && local.isDirectory()) {
            return local.getAbsolutePath();
        }

        // 2. Buscar relativo a donde está el JAR ejecutable
        try {
            File jarDir = new File(Main.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getParentFile();
            File nextToJar = new File(jarDir, "web-admin");
            if (nextToJar.exists() && nextToJar.isDirectory()) {
                return nextToJar.getAbsolutePath();
            }
        } catch (Exception ignored) {}

        // 3. Fallback: crear ruta aunque no exista (Tomcat mostrará 404)
        System.err.println("⚠️  Directorio web-admin no encontrado. El panel admin no estará disponible.");
        return new File("web-admin").getAbsolutePath();
    }
}