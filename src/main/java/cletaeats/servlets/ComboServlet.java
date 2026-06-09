package cletaeats.servlets;

import cletaeats.config.RespuestaJSON;
import cletaeats.repositories.ComboRepository;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/combos/*")
public class ComboServlet extends HttpServlet {
    private final ComboRepository comboRepo = new ComboRepository();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(comboRepo.listarTodos())));
            } else {
                int restauranteId = Integer.parseInt(pathInfo.substring(1));
                resp.getWriter().write(gson.toJson(RespuestaJSON.exito(comboRepo.listarPorRestaurante(restauranteId))));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("ID de restaurante inválido")));
        } catch (Exception e) {
            System.err.println("CletaEats ComboServlet error: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(RespuestaJSON.fallar("Error interno del servidor.")));
        }
    }
}
