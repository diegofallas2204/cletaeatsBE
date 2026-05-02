package cletaeats.servlets;

import cletaeats.controllers.PedidoController;
import cletaeats.models.Cliente;
import cletaeats.models.Usuario;
import cletaeats.repositories.ClienteRepository;
import cletaeats.repositories.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/cliente/*")
public class ClienteServlet extends HttpServlet {
    private final PedidoController pedidoController = new PedidoController();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final ClienteRepository clienteRepository = new ClienteRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.equals("/pedidos")) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Endpoint no encontrado\"}");
            return;
        }

        // Leer el JSON del cuerpo
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
        // Llamar al controlador existente para crear pedido
        String jsonResponse = pedidoController.crearPedido(sb.toString());
        resp.getWriter().write(jsonResponse);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.equals("/pedidos/historial")) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Endpoint no encontrado\"}");
            return;
        }

        try {
            // Extraer el username del token validado por AuthFilter
            String username = (String) req.getAttribute("username");
            Usuario usuario = usuarioRepository.findByUsername(username);
            Cliente cliente = clienteRepository.buscarPorUsuarioId(usuario.getId());

            if (cliente == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"exito\":false, \"mensaje\":\"El usuario no tiene perfil de cliente\"}");
                return;
            }

            // Llamar al controlador para obtener historial
            String jsonResponse = pedidoController.historial(cliente.getId());
            resp.getWriter().write(jsonResponse);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Error interno: " + e.getMessage() + "\"}");
        }
    }
}
