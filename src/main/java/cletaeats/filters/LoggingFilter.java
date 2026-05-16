package cletaeats.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Filtro para registrar todos los requests entrantes en la consola.
 */
@WebFilter("/*")
public class LoggingFilter implements Filter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        
        String method = req.getMethod();
        String uri = req.getRequestURI();
        String ip = req.getRemoteAddr();
        String timestamp = LocalDateTime.now().format(formatter);

        // Imprimir el log en la consola
        System.out.printf("[%s] REQUEST: %s %s desde IP: %s%n", timestamp, method, uri, ip);

        long startTime = System.currentTimeMillis();
        chain.doFilter(request, response);
        long duration = System.currentTimeMillis() - startTime;

        System.out.printf("[%s] RESPONSE: %s %s completado en %dms%n", timestamp, method, uri, duration);
    }

    @Override
    public void destroy() {}
}
