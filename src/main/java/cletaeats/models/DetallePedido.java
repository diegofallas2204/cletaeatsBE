package cletaeats.models;

public class DetallePedido {
    private int id;
    private int pedidoId;
    private Integer comboId;
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
    public Integer getComboId() { return comboId; }
    public void setComboId(Integer comboId) { this.comboId = comboId; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public float getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(float precioUnitario) { this.precioUnitario = precioUnitario; }
    public boolean isAgrandado() { return agrandado; }
    public void setAgrandado(boolean agrandado) { this.agrandado = agrandado; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}