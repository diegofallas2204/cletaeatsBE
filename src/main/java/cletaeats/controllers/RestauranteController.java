package cletaeats.controllers;

import cletaeats.config.RespuestaJSON;
import cletaeats.models.Restaurante;
import cletaeats.services.RestauranteService;
import com.google.gson.Gson;
import java.util.List;

public class RestauranteController {
    private final RestauranteService service = new RestauranteService();
    private final Gson gson = new Gson();

    public String listarTodos() {
        try {
            List<Restaurante> lista = service.obtenerTodos();
            return gson.toJson(RespuestaJSON.exito(lista));
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar("Error en la lógica de negocio: " + e.getMessage()));
        }
    }
}