package cletaeats.servlets;

import cletaeats.controllers.UsuarioController;
import cletaeats.repositories.UsuarioRepository;
import cletaeats.utils.LoginRateLimiter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/usuarios/*")
public class UsuarioServlet extends HttpServlet {
    private final UsuarioController usuarioController = new UsuarioController();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Configurar tipo de respuesta
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo(); // Por ejemplo: /login o /registrar
        if (pathInfo == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Ruta no válida\"}");
            return;
        }

        // Leer el JSON del cuerpo de la petición
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String jsonInput = sb.toString();
        String jsonResponse = "";

        // Enrutar al controlador correspondiente
        switch (pathInfo) {
            case "/login":
                String clientIp = req.getRemoteAddr();
                if (!LoginRateLimiter.isAllowed(clientIp)) {
                    resp.setStatus(429);
                    resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Demasiados intentos. Intente de nuevo en 10 minutos.\"}");
                    return;
                }
                jsonResponse = usuarioController.login(jsonInput);
                break;
            case "/registrar":
                jsonResponse = usuarioController.registrar(jsonInput);
                break;
            case "/logout":
                jsonResponse = usuarioController.logout();
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse = "{\"exito\":false, \"mensaje\":\"Endpoint no encontrado\"}";
                break;
        }

        // Si la respuesta indica fallo, el HTTP status code idealmente debería reflejarlo, pero para simplificar, 
        // muchos APIs envían el error dentro del JSON con status 200 o se extrae.
        // Aquí lo dejamos directo del controlador.
        resp.getWriter().write(jsonResponse);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.equals("/perfil")) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Endpoint no encontrado\"}");
            return;
        }

        try {
            String username = (String) req.getAttribute("username");
            if (username == null || username.isBlank()) {
                if (req.getUserPrincipal() != null) {
                    username = req.getUserPrincipal().getName();
                }
            }

            if (username == null || username.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"exito\":false, \"mensaje\":\"No autorizado: token inválido o ausente\"}");
                return;
            }

            String jsonResponse = usuarioController.getPerfil(username);
            resp.getWriter().write(jsonResponse);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.err.println("CletaEats UsuarioServlet error: " + e.getMessage());
            resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Error interno del servidor.\"}");
        }
    }
}
