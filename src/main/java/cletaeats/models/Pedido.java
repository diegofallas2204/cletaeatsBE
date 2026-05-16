package cletaeats.models;

import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private int clienteId;
    private int restauranteId;
    private int repartidorId;
    private String estado; // 'preparacion', 'camino', 'entregado', 'suspendido'
    private float subtotal;
    private float costoEnvio;
    private float iva;
    private float total;
    private float distanciaKm;
    private String fechaPedido;
    private String fechaEntrega;
    private String numeroTarjeta;
    private List<DetallePedido> detalles;

    public Pedido() {
        this.detalles = new ArrayList<>();
    }

    // Getters y Setters completos
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public int getRestauranteId() { return restauranteId; }
    public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }
    public int getRepartidorId() { return repartidorId; }
    public void setRepartidorId(int repartidorId) { this.repartidorId = repartidorId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public float getSubtotal() { return subtotal; }
    public void setSubtotal(float subtotal) { this.subtotal = subtotal; }
    public float getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(float costoEnvio) { this.costoEnvio = costoEnvio; }
    public float getIva() { return iva; }
    public void setIva(float iva) { this.iva = iva; }
    public float getTotal() { return total; }
    public void setTotal(float total) { this.total = total; }
    public float getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(float distanciaKm) { this.distanciaKm = distanciaKm; }
    public String getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(String fechaPedido) { this.fechaPedido = fechaPedido; }
    public String getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(String fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }
}