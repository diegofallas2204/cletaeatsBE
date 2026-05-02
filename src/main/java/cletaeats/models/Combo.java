package cletaeats.models;

public class Combo {
    private int id;
    private int restauranteId;
    private int numeroCombo; // 1-9
    private String nombre;
    private float precio;

    public Combo() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRestauranteId() { return restauranteId; }
    public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }
    public int getNumeroCombo() { return numeroCombo; }
    public void setNumeroCombo(int numeroCombo) { this.numeroCombo = numeroCombo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public float getPrecio() { return precio; }
    public void setPrecio(float precio) { this.precio = precio; }
}