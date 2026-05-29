package cletaeats.controllers;

import cletaeats.config.RespuestaJSON;
import cletaeats.models.DetallePedido;
import cletaeats.models.Pedido;
import cletaeats.services.PedidoService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class PedidoController {
    private final PedidoService pedidoService = new PedidoService();
    private final Gson gson = new Gson();

    public String crearPedido(String jsonInput, String username) {
        try {
            JsonObject jobj = gson.fromJson(jsonInput, JsonObject.class);
            if (jobj == null) {
                throw new Exception("Cuerpo JSON vacío o inválido");
            }

            final Pedido pedido;
            final boolean esFeriado;

            if (jobj.has("pedido") && !jobj.get("pedido").isJsonNull()) {
                pedido = gson.fromJson(jobj.get("pedido"), Pedido.class);
                esFeriado = readBoolean(jobj, "esFeriado", false);
            } else if (jobj.has("restauranteId") && jobj.has("items")) {
                pedido = parsePedidoMovil(jobj);
                esFeriado = readBoolean(jobj, "esFeriado", false);
            } else {
                throw new Exception("Formato de pedido no reconocido. Envíe {pedido, esFeriado} o {restauranteId, items, numeroTarjeta}.");
            }

            int idGenerado = pedidoService.procesarNuevoPedido(pedido, esFeriado, username);
            return gson.toJson(RespuestaJSON.exito("Pedido creado con ID: " + idGenerado));
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar(e.getMessage()));
        }
    }

    /**
     * Formato app móvil / API documentada:
     * { "restauranteId": 1, "items": [{ "comboId": 1, "cantidad": 2 }], "numeroTarjeta": "...", "esFeriado": false }
     */
    private Pedido parsePedidoMovil(JsonObject jobj) throws Exception {
        Pedido pedido = new Pedido();
        pedido.setRestauranteId(jobj.get("restauranteId").getAsInt());

        String numeroTarjeta = readString(jobj, "numeroTarjeta", null);
        if (numeroTarjeta == null || numeroTarjeta.isBlank()) {
            throw new Exception("numeroTarjeta es requerido");
        }
        pedido.setNumeroTarjeta(numeroTarjeta.trim());

        pedido.setDistanciaKm(readFloat(jobj, "distanciaKm", 5.0f));

        JsonArray items = jobj.getAsJsonArray("items");
        if (items == null || items.isEmpty()) {
            throw new Exception("El pedido debe incluir al menos un item");
        }

        List<DetallePedido> detalles = new ArrayList<>();
        for (JsonElement element : items) {
            JsonObject item = element.getAsJsonObject();
            DetallePedido det = new DetallePedido();
            det.setComboId(item.get("comboId").getAsInt());
            det.setCantidad(item.get("cantidad").getAsInt());

            String notas = readString(item, "notas", null);
            if (notas != null) {
                det.setNotas(notas);
                if ("Agrandado".equalsIgnoreCase(notas)) {
                    det.setAgrandado(true);
                }
            }
            detalles.add(det);
        }
        pedido.setDetalles(detalles);
        return pedido;
    }

    private boolean readBoolean(JsonObject obj, String key, boolean defaultValue) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            return defaultValue;
        }
        return obj.get(key).getAsBoolean();
    }

    private String readString(JsonObject obj, String key, String defaultValue) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            return defaultValue;
        }
        return obj.get(key).getAsString();
    }

    private float readFloat(JsonObject obj, String key, float defaultValue) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            return defaultValue;
        }
        return obj.get(key).getAsFloat();
    }

    public String historial(int clienteId) {
        try {
            return gson.toJson(RespuestaJSON.exito(pedidoService.obtenerHistorialCliente(clienteId)));
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar(e.getMessage()));
        }
    }
}
