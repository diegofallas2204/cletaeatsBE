package cletaeats.filters;

import cletaeats.config.RespuestaJSON;
import cletaeats.utils.JwtUtil;
import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// Protegeremos todas las rutas bajo /api/ excepto usuarios (login y registro)
@WebFilter("/api/*")
public class AuthFilter implements Filter {
    private final Gson gson = new Gson();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        String path = req.getRequestURI();
        
        // Excluir endpoints públicos
        if (path.contains("/api/usuarios/login") || path.contains("/api/usuarios/registrar")) {
            chain.doFilter(request, response);
            return;
        }

        // Verificar token
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = JwtUtil.validarToken(token);
            
            if (username != null) {
                // Token válido, podemos guardar el usuario en el request
                req.setAttribute("username", username);
                chain.doFilter(request, response);
                return;
            }
        }

        // Token inválido o ausente
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(gson.toJson(RespuestaJSON.fallar("No autorizado. Token inválido o ausente.")));
    }

    @Override
    public void destroy() {}
}
