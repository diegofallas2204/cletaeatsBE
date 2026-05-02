package cletaeats.servlets;

import cletaeats.controllers.UsuarioController;
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
}
