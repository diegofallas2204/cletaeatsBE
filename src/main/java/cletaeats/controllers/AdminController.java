package cletaeats.controllers;

import com.google.gson.Gson;
import cletaeats.config.RespuestaJSON;
import cletaeats.models.ReporteRestaurante;
import cletaeats.services.ReporteService;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AdminController {
    private final ReporteService reporteService = new ReporteService();
    private final Gson gson = new Gson();

    /**
     * Genera la respuesta JSON para el dashboard de la App Web.
     */
    public String obtenerDashboard() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("totalVendidoGeneral", reporteService.obtenerTotalVendidoGeneral());
            data.put("ventasPorRestaurante", reporteService.ventasPorRestaurante());

            RespuestaJSON<Map<String, Object>> respuesta = RespuestaJSON.exito(data);
            return gson.toJson(respuesta);
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar("Error al generar dashboard: " + e.getMessage()));
        }
    }
}