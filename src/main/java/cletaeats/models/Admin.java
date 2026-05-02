package cletaeats.models;

/**
 * Entidad para los usuarios administradores que gestionan el backend.
 */
public class Admin {
    private int id;
    private int usuarioId;
    private String nombre;

    public Admin() {}

    public Admin(int usuarioId, String nombre) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}