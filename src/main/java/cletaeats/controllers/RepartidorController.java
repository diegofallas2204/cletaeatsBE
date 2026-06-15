package cletaeats.controllers;

import cletaeats.config.RespuestaJSON;
import cletaeats.models.MetodoPago;
import cletaeats.repositories.MetodoPagoRepartidorRepository;
import com.google.gson.Gson;

/**
 * Maneja los metodos de pago del repartidor. Espejo de la parte de tarjetas
 * de ClienteController, usando la tabla tarjetas_repartidor.
 */
public class RepartidorController {
    private final MetodoPagoRepartidorRepository metodoPagoRepository = new MetodoPagoRepartidorRepository();
    private final Gson gson = new Gson();

    public String obtenerTarjetas(int repartidorId) {
        try {
            return gson.toJson(RespuestaJSON.exito(metodoPagoRepository.listarPorRepartidor(repartidorId)));
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar(e.getMessage()));
        }
    }

    public String guardarTarjeta(int repartidorId, String jsonInput) {
        try {
            MetodoPago tarjeta = gson.fromJson(jsonInput, MetodoPago.class);
            int id = metodoPagoRepository.guardar(repartidorId, tarjeta);
            tarjeta.setId(id);
            return gson.toJson(RespuestaJSON.exito(tarjeta));
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar(e.getMessage()));
        }
    }

    public String eliminarTarjeta(int repartidorId, int tarjetaId) {
        try {
            boolean exito = metodoPagoRepository.eliminarTarjeta(repartidorId, tarjetaId);
            if (exito) {
                return gson.toJson(RespuestaJSON.exito("Tarjeta eliminada correctamente"));
            } else {
                return gson.toJson(RespuestaJSON.fallar("No se encontró la tarjeta"));
            }
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar(e.getMessage()));
        }
    }
}
