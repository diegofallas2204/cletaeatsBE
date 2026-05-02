package cletaeats.controllers;

import cletaeats.config.RespuestaJSON;
import cletaeats.models.Restaurante;
import cletaeats.repositories.RestauranteRepository;
import java.util.List;
import com.google.gson.Gson;

public class RestauranteController {
    private final RestauranteRepository repo = new RestauranteRepository();
    private final Gson gson = new Gson();

    public String listarTodos() {
        try {
            List<Restaurante> lista = repo.listarTodos();
            return gson.toJson(RespuestaJSON.exito(lista));
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar(e.getMessage()));
        }
    }
}
