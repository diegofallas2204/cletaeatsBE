package cletaeats.servlets;

import cletaeats.controllers.PedidoController;
import cletaeats.controllers.ClienteController;
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
    private final ClienteController clienteController = new ClienteController();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final ClienteRepository clienteRepository = new ClienteRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || (!pathInfo.equals("/pedidos") && !pathInfo.equals("/tarjetas"))) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Endpoint no encontrado\"}");
            return;
        }

        try {
            String username = resolveUsername(req);
            if (username == null || username.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"exito\":false, \"mensaje\":\"No autorizado: token inválido o ausente\"}");
                return;
            }

            Usuario usuario = usuarioRepository.findByUsername(username);
            if (usuario == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Usuario no encontrado\"}");
                return;
            }

            Cliente cliente = clienteRepository.buscarPorUsuarioId(usuario.getId());
            if (cliente == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"exito\":false, \"mensaje\":\"El usuario no tiene perfil de cliente\"}");
                return;
            }

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String jsonResponse = "";
            if (pathInfo.equals("/pedidos")) {
                jsonResponse = pedidoController.crearPedido(sb.toString(), username);
            } else if (pathInfo.equals("/tarjetas")) {
                jsonResponse = clienteController.guardarTarjeta(cliente.getId(), sb.toString());
            }
            resp.getWriter().write(jsonResponse);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Error interno: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || (!pathInfo.equals("/pedidos/historial") && !pathInfo.equals("/tarjetas"))) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Endpoint no encontrado\"}");
            return;
        }

        try {
            String username = resolveUsername(req);
            if (username == null || username.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"exito\":false, \"mensaje\":\"No autorizado: token inválido o ausente\"}");
                return;
            }

            Usuario usuario = usuarioRepository.findByUsername(username);
            if (usuario == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Usuario no encontrado\"}");
                return;
            }

            Cliente cliente = clienteRepository.buscarPorUsuarioId(usuario.getId());
            if (cliente == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"exito\":false, \"mensaje\":\"El usuario no tiene perfil de cliente\"}");
                return;
            }

            String jsonResponse = "";
            if (pathInfo.equals("/pedidos/historial")) {
                jsonResponse = pedidoController.historial(cliente.getId());
            } else if (pathInfo.equals("/tarjetas")) {
                jsonResponse = clienteController.obtenerTarjetas(cliente.getId());
            }
            resp.getWriter().write(jsonResponse);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Error interno: " + e.getMessage() + "\"}");
        }
    }

    private String resolveUsername(HttpServletRequest req) {
        String username = (String) req.getAttribute("username");
        if (username == null || username.isBlank()) {
            if (req.getUserPrincipal() != null) {
                username = req.getUserPrincipal().getName();
            }
        }
        return username;
    }
}
