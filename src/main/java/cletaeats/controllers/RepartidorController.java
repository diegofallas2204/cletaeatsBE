package cletaeats.controllers;

import cletaeats.config.RespuestaJSON;
import cletaeats.models.MetodoPago;
import cletaeats.repositories.MetodoPagoRepartidorRepository;
import cletaeats.utils.TarjetaUtils;
import com.google.gson.Gson;

import java.util.List;

/**
 * Maneja los metodos de pago del repartidor. Espejo de la parte de tarjetas
 * de ClienteController, usando la tabla tarjetas_repartidor.
 */
public class RepartidorController {
    private final MetodoPagoRepartidorRepository metodoPagoRepository = new MetodoPagoRepartidorRepository();
    private final Gson gson = new Gson();

    public String obtenerTarjetas(int repartidorId) {
        try {
            List<MetodoPago> tarjetas = metodoPagoRepository.listarPorRepartidor(repartidorId);
            TarjetaUtils.enmascarar(tarjetas); // nunca exponer PAN completo ni CVV
            return gson.toJson(RespuestaJSON.exito(tarjetas));
        } catch (Exception e) {
            System.err.println("CletaEats RepartidorController.obtenerTarjetas error: " + e.getMessage());
            return gson.toJson(RespuestaJSON.fallar("No se pudieron obtener las tarjetas."));
        }
    }

    public String guardarTarjeta(int repartidorId, String jsonInput) {
        try {
            MetodoPago tarjeta = gson.fromJson(jsonInput, MetodoPago.class);

            String error = TarjetaUtils.validar(tarjeta);
            if (error != null) {
                return gson.toJson(RespuestaJSON.fallar(error));
            }

            if (metodoPagoRepository.existeTarjetaRepartidor(repartidorId, tarjeta.getNumeroTarjeta())) {
                return gson.toJson(RespuestaJSON.fallar("Ya existe una tarjeta con ese número."));
            }

            int id = metodoPagoRepository.guardar(repartidorId, tarjeta);
            tarjeta.setId(id);
            TarjetaUtils.enmascarar(tarjeta); // no devolver PAN completo ni CVV
            return gson.toJson(RespuestaJSON.exito(tarjeta));
        } catch (Exception e) {
            System.err.println("CletaEats RepartidorController.guardarTarjeta error: " + e.getMessage());
            return gson.toJson(RespuestaJSON.fallar("No se pudo guardar la tarjeta."));
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
            System.err.println("CletaEats RepartidorController.eliminarTarjeta error: " + e.getMessage());
            return gson.toJson(RespuestaJSON.fallar("No se pudo eliminar la tarjeta."));
        }
    }
}
