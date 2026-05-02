package cletaeats.models;

public class DetallePedido {
    private int id;
    private int pedidoId;
    private int comboId;
    private int cantidad;
    private float precioUnitario;
    private boolean agrandado;
    private String notas;

    public DetallePedido() {}

    // Getters y Setters completos
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    public int getComboId() { return comboId; }
    public void setComboId(int comboId) { this.comboId = comboId; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public float getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(float precioUnitario) { this.precioUnitario = precioUnitario; }
    public boolean isAgrandado() { return agrandado; }
    public void setAgrandado(boolean agrandado) { this.agrandado = agrandado; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}