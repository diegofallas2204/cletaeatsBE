package cletaeats.servlets;

import cletaeats.config.RespuestaJSON;
import cletaeats.models.Pedido;
import cletaeats.models.Repartidor;
import cletaeats.models.Usuario;
import cletaeats.repositories.PedidoRepository;
import cletaeats.repositories.RepartidorRepository;
import cletaeats.repositories.UsuarioRepository;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/repartidor/*")
public class RepartidorServlet extends HttpServlet {
    private final PedidoRepository pedidoRepository = new PedidoRepository();
    private final RepartidorRepository repartidorRepository = new RepartidorRepository();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.equals("/pedidos")) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Endpoint no encontrado")));
            return;
        }

        try {
            // Obtener identidad
            String username = (String) req.getAttribute("username");
            Usuario usuario = usuarioRepository.findByUsername(username);
            Repartidor repartidor = repartidorRepository.buscarPorUsuarioId(usuario.getId());

            if (repartidor == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("El usuario no tiene perfil de repartidor")));
                return;
            }

            // Listar pedidos asignados al repartidor que no esten entregados
            List<Pedido> asignados = pedidoRepository.listarPorRepartidor(repartidor.getId());
            resp.getWriter().write(gson.toJson(RespuestaJSON.exito(asignados)));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Error: " + e.getMessage())));
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
            // Ejemplo de ruta: /pedidos/1/estado
            String[] partes = pathInfo.split("/");
            if (partes.length != 4 || !partes[3].equals("estado")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Ruta mal formada")));
                return;
            }

            int pedidoId = Integer.parseInt(partes[2]);

            // Leer JSON para obtener el nuevo estado
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            Map<String, String> body = gson.fromJson(sb.toString(), Map.class);
            String nuevoEstado = body.get("estado"); // 'camino', 'entregado'

            if (nuevoEstado == null || nuevoEstado.isEmpty()) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Falta el campo estado")));
                return;
            }

            boolean actualizado = pedidoRepository.actualizarEstadoPedido(pedidoId, nuevoEstado);
            
            if (actualizado && nuevoEstado.equals("entregado")) {
                // Si ya lo entregó, vuelve a estar disponible
                String username = (String) req.getAttribute("username");
                Usuario usuario = usuarioRepository.findByUsername(username);
                Repartidor repartidor = repartidorRepository.buscarPorUsuarioId(usuario.getId());
                repartidorRepository.actualizarEstado(repartidor.getId(), "disponible");
            }

            if (actualizado) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Estado actualizado a: " + nuevoEstado)));
            } else {
                resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("No se pudo actualizar el pedido")));
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Error: " + e.getMessage())));
        }
    }
}
