package cletaeats.models;

import java.sql.Timestamp;

/**
 * Representa una queja interpuesta por un cliente contra un repartidor
 * ligada específicamente a un pedido realizado.
 */
public class Queja {
    private int id;
    private int pedidoId;
    private int repartidorId; // Referencia directa para facilitar reportes
    private String descripcion;
    private Timestamp fecha;

    public Queja() {}

    public Queja(int pedidoId, int repartidorId, String descripcion) {
        this.pedidoId = pedidoId;
        this.repartidorId = repartidorId;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    public int getRepartidorId() { return repartidorId; }
    public void setRepartidorId(int repartidorId) { this.repartidorId = repartidorId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
}