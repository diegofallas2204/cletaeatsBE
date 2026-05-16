package cletaeats.controllers;

import cletaeats.config.RespuestaJSON;
import cletaeats.models.MetodoPago;
import cletaeats.repositories.MetodoPagoRepository;
import com.google.gson.Gson;

public class ClienteController {
    private final MetodoPagoRepository metodoPagoRepository = new MetodoPagoRepository();
    private final Gson gson = new Gson();

    public String obtenerTarjetas(int clienteId) {
        try {
            return gson.toJson(RespuestaJSON.exito(metodoPagoRepository.listarPorCliente(clienteId)));
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar(e.getMessage()));
        }
    }

    public String guardarTarjeta(int clienteId, String jsonInput) {
        try {
            MetodoPago tarjeta = gson.fromJson(jsonInput, MetodoPago.class);
            tarjeta.setClienteId(clienteId);
            int id = metodoPagoRepository.guardar(tarjeta);
            tarjeta.setId(id);
            return gson.toJson(RespuestaJSON.exito(tarjeta));
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar(e.getMessage()));
        }
    }
}
