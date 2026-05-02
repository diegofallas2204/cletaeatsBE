package cletaeats.models;

/**
 * Representa un comercio afiliado al sistema CletaEats.
 * Según los requerimientos, los combos no se almacenan aquí,
 * sino que el restaurante los provee externamente.
 */
public class Restaurante {
    private int id;
    private String nombre;
    private String cedulaJuridica; // Identificador único legal
    private String direccion;
    private String tipoComida; // Ej: China, Rápida, Saludable

    public Restaurante() {
    }

    /**
     * Constructor completo para creación rápida desde el Controller.
     */
    public Restaurante(int id, String nombre, String cedulaJuridica, String direccion, String tipoComida) {
        this.id = id;
        this.nombre = nombre;
        this.cedulaJuridica = cedulaJuridica;
        this.direccion = direccion;
        this.tipoComida = tipoComida;
    }

    // ========================================
    // GETTERS Y SETTERS
    // ========================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedulaJuridica() {
        return cedulaJuridica;
    }

    public void setCedulaJuridica(String cedulaJuridica) {
        this.cedulaJuridica = cedulaJuridica;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipoComida() {
        return tipoComida;
    }

    public void setTipoComida(String tipoComida) {
        this.tipoComida = tipoComida;
    }

    @Override
    public String toString() {
        return "Restaurante{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", cedulaJuridica='" + cedulaJuridica + '\'' +
                ", tipoComida='" + tipoComida + '\'' +
                '}';
    }
}