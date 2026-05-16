package cletaeats.repositories;

import cletaeats.config.Conexion;
import cletaeats.models.Usuario;
import java.sql.*;

public class UsuarioRepository {
    private final Conexion conexion = Conexion.getInstancia();

    public int crear(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (username, password, rol, activo) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getRol());
            stmt.setBoolean(4, true);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Error al crear usuario, no se afectaron filas.");

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                else throw new SQLException("Error al crear usuario, no se obtuvo el ID.");
            }
        }
    }

    public int crear(Usuario usuario, Connection conn) throws SQLException {
        String sql = "INSERT INTO usuarios (username, password, rol, activo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getRol());
            stmt.setBoolean(4, true);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Error al crear usuario, no se afectaron filas.");

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                else throw new SQLException("Error al crear usuario, no se obtuvo el ID.");
            }
        }
    }

    public Usuario findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password, rol, activo FROM usuarios WHERE username = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password")); // Necesario para verificar BCrypt
                    u.setRol(rs.getString("rol"));
                    u.setActivo(rs.getBoolean("activo"));
                    return u;
                }
            }
        }
        return null;
    }

    public boolean existeUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}