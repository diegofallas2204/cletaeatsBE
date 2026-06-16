package cletaeats.servlets;

import cletaeats.config.RespuestaJSON;
import cletaeats.models.Combo;
import cletaeats.models.Restaurante;
import cletaeats.models.Cliente;
import cletaeats.models.Repartidor;
import cletaeats.repositories.ComboRepository;
import cletaeats.repositories.PedidoRepository;
import cletaeats.repositories.RestauranteRepository;
import cletaeats.repositories.ClienteRepository;
import cletaeats.repositories.RepartidorRepository;
import cletaeats.repositories.ValoracionRepository;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/admin/*")
public class AdminServlet extends HttpServlet {
    private final RestauranteRepository restauranteRepo = new RestauranteRepository();
    private final ComboRepository comboRepo = new ComboRepository();
    private final PedidoRepository pedidoRepo = new PedidoRepository();
    private final ClienteRepository clienteRepo = new ClienteRepository();
    private final RepartidorRepository repartidorRepo = new RepartidorRepository();
    private final ValoracionRepository valoracionRepo = new ValoracionRepository();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!esAdmin(req, resp)) return;
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/dashboard")) {
                Map<String, Object> dashboard = new HashMap<>();
                dashboard.put("ventasTotales", pedidoRepo.sumarVentasTotales());
                dashboard.put("ventasPorRestaurante", pedidoRepo.obtenerVentasAgrupadasPorRestaurante());
                dashboard.put("totalClientes", clienteRepo.contarActivos());
                dashboard.put("totalRepartidores", repartidorRepo.contarActivos());
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(dashboard)));
            } else if (pathInfo.equals("/restaurantes")) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(restauranteRepo.listarTodos())));
            } else if (pathInfo.equals("/restaurantes/inactivos")) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(restauranteRepo.listarInactivos())));
            } else if (pathInfo.equals("/clientes")) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(clienteRepo.listarTodos())));
            } else if (pathInfo.equals("/clientes/inactivos")) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(clienteRepo.listarInactivos())));
            } else if (pathInfo.equals("/repartidores")) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(repartidorRepo.listarTodos())));
            } else if (pathInfo.equals("/repartidores/inactivos")) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(repartidorRepo.listarInactivos())));
            } else if (pathInfo.equals("/combos")) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(comboRepo.listarTodos())));
            } else if (pathInfo.equals("/combos/inactivos")) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(comboRepo.listarInactivos())));
            } else if (pathInfo.startsWith("/combos/")) {
                int restId = Integer.parseInt(pathInfo.substring(8));
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(comboRepo.listarPorRestaurante(restId))));
            } else if (pathInfo.equals("/pedidos")) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(pedidoRepo.listarTodos())));
            } else if (pathInfo.equals("/valoraciones")) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(valoracionRepo.listarTodas())));
            }
        } catch (Exception e) {
            System.err.println("CletaEats AdminServlet doGet: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Error interno del servidor.")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        if (!esAdmin(req, resp)) return;
        String pathInfo = req.getPathInfo();
        String json = leerCuerpo(req);

        try {
            if (pathInfo.equals("/restaurantes")) {
                Restaurante r = gson.fromJson(json, Restaurante.class);
                restauranteRepo.crear(r);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Restaurante creado")));
            } else if (pathInfo.equals("/combos")) {
                Combo c = gson.fromJson(json, Combo.class);
                comboRepo.crear(c);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Combo creado")));
            } else if (pathInfo.equals("/clientes")) {
                Cliente c = gson.fromJson(json, Cliente.class);
                clienteRepo.crear(c);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Cliente creado")));
            } else if (pathInfo.equals("/repartidores")) {
                Repartidor r = gson.fromJson(json, Repartidor.class);
                repartidorRepo.crear(r);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Repartidor creado")));
            }
        } catch (Exception e) {
            System.err.println("CletaEats AdminServlet error: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Error interno del servidor.")));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        if (!esAdmin(req, resp)) return;
        String pathInfo = req.getPathInfo();
        String json = leerCuerpo(req);

        try {
            if (pathInfo.equals("/restaurantes")) {
                Restaurante r = gson.fromJson(json, Restaurante.class);
                restauranteRepo.actualizar(r);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Restaurante actualizado")));
            } else if (pathInfo.startsWith("/restaurantes/reactivar/")) {
                int id = Integer.parseInt(pathInfo.substring(24));
                restauranteRepo.reactivar(id);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Restaurante reactivado")));
            } else if (pathInfo.equals("/combos")) {
                Combo c = gson.fromJson(json, Combo.class);
                comboRepo.actualizar(c);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Combo actualizado")));
            } else if (pathInfo.startsWith("/combos/reactivar/")) {
                int id = Integer.parseInt(pathInfo.substring(18));
                comboRepo.reactivar(id);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Combo reactivado")));
            } else if (pathInfo.equals("/clientes")) {
                Cliente c = gson.fromJson(json, Cliente.class);
                clienteRepo.actualizar(c);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Cliente actualizado")));
            } else if (pathInfo.startsWith("/clientes/reactivar/")) {
                int id = Integer.parseInt(pathInfo.substring(20));
                clienteRepo.reactivar(id);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Cliente reactivado")));
            } else if (pathInfo.equals("/repartidores")) {
                Repartidor r = gson.fromJson(json, Repartidor.class);
                repartidorRepo.actualizar(r);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Repartidor actualizado")));
            } else if (pathInfo.startsWith("/repartidores/reactivar/")) {
                int id = Integer.parseInt(pathInfo.substring(24));
                repartidorRepo.reactivar(id);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Repartidor reactivado")));
            } else if (pathInfo.startsWith("/pedidos/")) {
                int id = Integer.parseInt(pathInfo.substring(9));
                com.google.gson.JsonObject body = gson.fromJson(json, com.google.gson.JsonObject.class);
                String estado = body.get("estado").getAsString();
                pedidoRepo.actualizarEstadoPedido(id, estado);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Estado actualizado")));
            }
        } catch (Exception e) {
            System.err.println("CletaEats AdminServlet error: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Error interno del servidor.")));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        if (!esAdmin(req, resp)) return;
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo.startsWith("/restaurantes/")) {
                int id = Integer.parseInt(pathInfo.substring(14));
                restauranteRepo.eliminar(id);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Restaurante eliminado")));
            } else if (pathInfo.startsWith("/combos/")) {
                int id = Integer.parseInt(pathInfo.substring(8));
                comboRepo.eliminar(id);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Combo eliminado")));
            } else if (pathInfo.startsWith("/clientes/")) {
                int id = Integer.parseInt(pathInfo.substring(10));
                clienteRepo.eliminar(id);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Cliente eliminado")));
            } else if (pathInfo.startsWith("/repartidores/")) {
                int id = Integer.parseInt(pathInfo.substring(14));
                repartidorRepo.eliminar(id);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Repartidor eliminado")));
            } else if (pathInfo.startsWith("/pedidos/")) {
                int id = Integer.parseInt(pathInfo.substring(9));
                pedidoRepo.desactivarPedido(id);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Pedido desactivado")));
            }
        } catch (Exception e) {
            System.err.println("CletaEats AdminServlet error: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Error interno del servidor.")));
        }
    }

    private boolean esAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String rol = (String) req.getAttribute("rol");
        if (!"admin".equals(rol)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Acceso denegado: se requiere rol admin.")));
            return false;
        }
        return true;
    }

    private String leerCuerpo(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        return sb.toString();
    }
}
