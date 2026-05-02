package cletaeats.models;

/**
 * Modelo para transportar datos de reportes estadísticos de ventas.
 */
public class ReporteRestaurante {
    private String nombreRestaurante;
    private int cantidadPedidos;
    private double totalVendido;

    public ReporteRestaurante() {}

    // Getters y Setters
    public String getNombreRestaurante() { return nombreRestaurante; }
    public void setNombreRestaurante(String nombreRestaurante) { this.nombreRestaurante = nombreRestaurante; }

    public int getCantidadPedidos() { return cantidadPedidos; }
    public void setCantidadPedidos(int cantidadPedidos) { this.cantidadPedidos = cantidadPedidos; }

    public double getTotalVendido() { return totalVendido; }
    public void setTotalVendido(double totalVendido) { this.totalVendido = totalVendido; }
}