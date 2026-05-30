package cletaeats.servlets;

import cletaeats.controllers.PedidoController;
import cletaeats.controllers.ClienteController;
import cletaeats.models.Cliente;
import cletaeats.models.Usuario;
import cletaeats.models.Valoracion;
import cletaeats.repositories.ClienteRepository;
import cletaeats.repositories.UsuarioRepository;
import cletaeats.repositories.PedidoRepository;
import cletaeats.repositories.ValoracionRepository;
import cletaeats.config.RespuestaJSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    private final PedidoRepository pedidoRepository = new PedidoRepository();
    private final ValoracionRepository valoracionRepository = new ValoracionRepository();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        boolean esValoracion = pathInfo != null && pathInfo.matches("/pedidos/\\d+/valorar");
        if (pathInfo == null || (!pathInfo.equals("/pedidos") && !pathInfo.equals("/tarjetas") && !esValoracion)) {
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
            } else if (esValoracion) {
                // POST /api/cliente/pedidos/{id}/valorar
                String[] partes = pathInfo.split("/");
                int pedidoId = Integer.parseInt(partes[2]);
                if (valoracionRepository.existeParaPedido(pedidoId)) {
                    jsonResponse = gson.toJson(RespuestaJSON.fallar("Este pedido ya fue valorado"));
                } else {
                    JsonObject body = gson.fromJson(sb.toString(), JsonObject.class);
                    int rating = body.get("rating").getAsInt();
                    String comentario = body.has("comentario") && !body.get("comentario").isJsonNull()
                            ? body.get("comentario").getAsString() : null;
                    if (rating < 1 || rating > 5) {
                        jsonResponse = gson.toJson(RespuestaJSON.fallar("El rating debe ser entre 1 y 5"));
                    } else {
                        Valoracion v = new Valoracion();
                        v.setPedidoId(pedidoId);
                        v.setClienteId(cliente.getId());
                        v.setRating(rating);
                        v.setComentario(comentario);
                        valoracionRepository.guardar(v);
                        jsonResponse = gson.toJson(RespuestaJSON.exito("Valoración guardada"));
                    }
                }
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.startsWith("/pedidos/")) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Endpoint no encontrado")));
            return;
        }

        try {
            // Espera /pedidos/{id}/cancelar
            String[] partes = pathInfo.split("/");
            if (partes.length != 4 || !partes[3].equals("cancelar")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Ruta mal formada")));
                return;
            }

            int pedidoId = Integer.parseInt(partes[2]);

            String username = resolveUsername(req);
            if (username == null || username.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("No autorizado")));
                return;
            }

            System.out.println("[ClienteServlet] Intentando cancelar pedidoId: " + pedidoId + " por usuario: " + username);

            // Actualiza el estado a suspendido en la base de datos (conforme al ENUM de MySQL)
            boolean actualizado = pedidoRepository.actualizarEstadoPedido(pedidoId, "suspendido");
            System.out.println("[ClienteServlet] Resultado de actualizacion: " + actualizado);

            if (actualizado) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Pedido cancelado exitosamente")));
            } else {
                resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("No se pudo cancelar el pedido")));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Error: " + e.getMessage())));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.startsWith("/tarjetas/")) {
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
            Cliente cliente = clienteRepository.buscarPorUsuarioId(usuario.getId());

            String[] partes = pathInfo.split("/");
            if (partes.length < 3) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"exito\":false, \"mensaje\":\"Peticion invalida\"}");
                return;
            }
            int tarjetaId = Integer.parseInt(partes[2]);

            String jsonResponse = clienteController.eliminarTarjeta(cliente.getId(), tarjetaId);
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
