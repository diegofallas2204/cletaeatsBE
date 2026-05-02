package cletaeats.controllers;

import cletaeats.config.RespuestaJSON;
import cletaeats.models.Pedido;
import cletaeats.services.PedidoService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PedidoController {
    private final PedidoService pedidoService = new PedidoService();
    private final Gson gson = new Gson();

    public String crearPedido(String jsonInput) {
        try {
            // Extraer el objeto pedido y el flag de feriado del JSON raíz
            JsonObject jobj = gson.fromJson(jsonInput, JsonObject.class);
            Pedido pedido = gson.fromJson(jobj.get("pedido"), Pedido.class);
            boolean esFeriado = jobj.get("esFeriado").getAsBoolean();

            int idGenerado = pedidoService.procesarNuevoPedido(pedido, esFeriado);

            return gson.toJson(RespuestaJSON.exito("Pedido creado con ID: " + idGenerado));
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar(e.getMessage()));
        }
    }

    public String historial(int clienteId) {
        try {
            return gson.toJson(RespuestaJSON.exito(pedidoService.obtenerHistorialCliente(clienteId)));
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar(e.getMessage()));
        }
    }
}