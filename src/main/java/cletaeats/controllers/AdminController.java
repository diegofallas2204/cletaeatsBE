package cletaeats.controllers;

import com.google.gson.Gson;
import cletaeats.config.RespuestaJSON;
import cletaeats.services.ReporteService;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Maneja la lógica de administración y reportes para el Dashboard Web.
 * Sigue la arquitectura de capas llamando a ReporteService.
 */
public class AdminController {
    private final ReporteService reporteService = new ReporteService();
    private final Gson gson = new Gson();

    /**
     * Genera la respuesta JSON para el dashboard de la App Web.
     * Consolida datos de ventas generales y por restaurante.
     */
    public String obtenerDashboard() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("totalVendidoGeneral", reporteService.obtenerTotalVendidoGeneral());
            data.put("ventasPorRestaurante", reporteService.ventasPorRestaurante());

            // Especificamos el tipo Map<String, Object> para la respuesta
            RespuestaJSON<Map<String, Object>> respuesta = RespuestaJSON.exito(data);
            return gson.toJson(respuesta);
        } catch (Exception e) {
            // En caso de error, devolvemos el formato JSON de fallo
            return gson.toJson(RespuestaJSON.fallar("Error al generar dashboard: " + e.getMessage()));
        }
    }
}