package cletaeats.models;

public class Repartidor {
    private int id;
    private int usuarioId;
    private String cedula;
    private String nombre;
    private String email;
    private String direccion;
    private String telefono;
    private String tarjeta;
    private String estado; // 'disponible', 'ocupado'
    private float kmRecorridos;
    private int amonestaciones;

    public Repartidor() {}

    // Getters y Setters completos
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getTarjeta() { return tarjeta; }
    public void setTarjeta(String tarjeta) { this.tarjeta = tarjeta; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public float getKmRecorridos() { return kmRecorridos; }
    public void setKmRecorridos(float kmRecorridos) { this.kmRecorridos = kmRecorridos; }
    public int getAmonestaciones() { return amonestaciones; }
    public void setAmonestaciones(int amonestaciones) { this.amonestaciones = amonestaciones; }
}