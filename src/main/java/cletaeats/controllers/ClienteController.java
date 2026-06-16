package cletaeats.controllers;

import cletaeats.config.RespuestaJSON;
import cletaeats.models.MetodoPago;
import cletaeats.repositories.MetodoPagoRepository;
import cletaeats.utils.TarjetaUtils;
import com.google.gson.Gson;

import java.util.List;

public class ClienteController {
    private final MetodoPagoRepository metodoPagoRepository = new MetodoPagoRepository();
    private final Gson gson = new Gson();

    public String obtenerTarjetas(int clienteId) {
        try {
            List<MetodoPago> tarjetas = metodoPagoRepository.listarPorCliente(clienteId);
            // El PAN no se enmascara porque el checkout aún lo usa para emparejar
            // la tarjeta; pero el CVV nunca debe devolverse.
            TarjetaUtils.ocultarCvv(tarjetas);
            return gson.toJson(RespuestaJSON.exito(tarjetas));
        } catch (Exception e) {
            System.err.println("CletaEats ClienteController.obtenerTarjetas error: " + e.getMessage());
            return gson.toJson(RespuestaJSON.fallar("No se pudieron obtener las tarjetas."));
        }
    }

    public String guardarTarjeta(int clienteId, String jsonInput) {
        try {
            MetodoPago tarjeta = gson.fromJson(jsonInput, MetodoPago.class);

            String error = TarjetaUtils.validar(tarjeta);
            if (error != null) {
                return gson.toJson(RespuestaJSON.fallar(error));
            }

            if (metodoPagoRepository.existeTarjetaCliente(clienteId, tarjeta.getNumeroTarjeta())) {
                return gson.toJson(RespuestaJSON.fallar("Ya existe una tarjeta con ese número."));
            }

            tarjeta.setClienteId(clienteId);
            int id = metodoPagoRepository.guardar(tarjeta);
            tarjeta.setId(id);
            TarjetaUtils.ocultarCvv(tarjeta); // no devolver el CVV
            return gson.toJson(RespuestaJSON.exito(tarjeta));
        } catch (Exception e) {
            System.err.println("CletaEats ClienteController.guardarTarjeta error: " + e.getMessage());
            return gson.toJson(RespuestaJSON.fallar("No se pudo guardar la tarjeta."));
        }
    }

    public String eliminarTarjeta(int clienteId, int tarjetaId) {
        try {
            boolean exito = metodoPagoRepository.eliminarTarjeta(clienteId, tarjetaId);
            if (exito) {
                return gson.toJson(RespuestaJSON.exito("Tarjeta eliminada correctamente"));
            } else {
                return gson.toJson(RespuestaJSON.fallar("No se encontró la tarjeta"));
            }
        } catch (Exception e) {
            System.err.println("CletaEats ClienteController.eliminarTarjeta error: " + e.getMessage());
            return gson.toJson(RespuestaJSON.fallar("No se pudo eliminar la tarjeta."));
        }
    }
}
