package cletaeats.models;

public class Usuario {
    private int id;
    private String username;
    private String password;
    private String rol; // 'cliente', 'repartidor', 'admin'
    private boolean activo;
    private String token; // Campo transitorio para devolver el JWT al cliente

    public Usuario() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}