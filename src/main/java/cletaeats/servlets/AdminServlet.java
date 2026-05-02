package cletaeats.servlets;

import cletaeats.config.RespuestaJSON;
import cletaeats.models.Combo;
import cletaeats.models.Restaurante;
import cletaeats.repositories.ComboRepository;
import cletaeats.repositories.PedidoRepository;
import cletaeats.repositories.RestauranteRepository;
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
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/dashboard")) {
                Map<String, Object> dashboard = new HashMap<>();
                dashboard.put("ventasTotales", pedidoRepo.sumarVentasTotales());
                dashboard.put("ventasPorRestaurante", pedidoRepo.obtenerVentasAgrupadasPorRestaurante());
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(dashboard)));
            } else if (pathInfo.equals("/restaurantes")) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(restauranteRepo.listarTodos())));
            } else if (pathInfo.startsWith("/combos/")) {
                int restId = Integer.parseInt(pathInfo.substring(8));
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(comboRepo.listarPorRestaurante(restId))));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar(e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
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
            }
        } catch (Exception e) {
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar(e.getMessage())));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        String json = leerCuerpo(req);

        try {
            if (pathInfo.equals("/restaurantes")) {
                Restaurante r = gson.fromJson(json, Restaurante.class);
                restauranteRepo.actualizar(r);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Restaurante actualizado")));
            } else if (pathInfo.equals("/combos")) {
                Combo c = gson.fromJson(json, Combo.class);
                comboRepo.actualizar(c);
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito("Combo actualizado")));
            }
        } catch (Exception e) {
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar(e.getMessage())));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
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
            }
        } catch (Exception e) {
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar(e.getMessage())));
        }
    }

    private String leerCuerpo(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        return sb.toString();
    }
}
