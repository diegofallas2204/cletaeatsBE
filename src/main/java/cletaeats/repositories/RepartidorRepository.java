package cletaeats.repositories;

import cletaeats.config.Conexion;
import cletaeats.models.Repartidor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepartidorRepository {
    private final Conexion conexion = Conexion.getInstancia();

    public boolean crear(Repartidor r) throws SQLException {
        String sql = "INSERT INTO repartidores (usuario_id, cedula, nombre, email, direccion, telefono, tarjeta, estado, km_recorridos, amonestaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, r.getUsuarioId());
            stmt.setString(2, r.getCedula());
            stmt.setString(3, r.getNombre());
            stmt.setString(4, r.getEmail());
            stmt.setString(5, r.getDireccion());
            stmt.setString(6, r.getTelefono());
            stmt.setString(7, r.getTarjeta());
            stmt.setString(8, "disponible");
            stmt.setFloat(9, 0.0f);
            stmt.setInt(10, 0);
            return stmt.executeUpdate() > 0;
        }
    }

    public Repartidor obtenerDisponibleParaAsignacion() throws SQLException {
        // Regla de negocio: primero disponible con menos de 4 amonestaciones
        String sql = "SELECT * FROM repartidores WHERE estado = 'disponible' AND amonestaciones < 4 LIMIT 1";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return mapearResultSetARepartidor(rs);
        }
        return null;
    }

    public boolean actualizarEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE repartidores SET estado = ? WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Repartidor> listarTodos() throws SQLException {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT * FROM repartidores";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearResultSetARepartidor(rs));
            }
        }
        return lista;
    }

    public boolean actualizar(Repartidor r) throws SQLException {
        String sql = "UPDATE repartidores SET nombre=?, email=?, direccion=?, telefono=?, tarjeta=?, estado=?, km_recorridos=?, amonestaciones=? WHERE id=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, r.getNombre());
            stmt.setString(2, r.getEmail());
            stmt.setString(3, r.getDireccion());
            stmt.setString(4, r.getTelefono());
            stmt.setString(5, r.getTarjeta());
            stmt.setString(6, r.getEstado());
            stmt.setFloat(7, r.getKmRecorridos());
            stmt.setInt(8, r.getAmonestaciones());
            stmt.setInt(9, r.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM repartidores WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public Repartidor buscarPorUsuarioId(int usuarioId) throws SQLException {
        String sql = "SELECT * FROM repartidores WHERE usuario_id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapearResultSetARepartidor(rs);
            }
        }
        return null;
    }

    private Repartidor mapearResultSetARepartidor(ResultSet rs) throws SQLException {
        Repartidor r = new Repartidor();
        r.setId(rs.getInt("id"));
        r.setUsuarioId(rs.getInt("usuario_id"));
        r.setCedula(rs.getString("cedula"));
        r.setNombre(rs.getString("nombre"));
        r.setEmail(rs.getString("email"));
        r.setDireccion(rs.getString("direccion"));
        r.setTelefono(rs.getString("telefono"));
        r.setTarjeta(rs.getString("tarjeta"));
        r.setEstado(rs.getString("estado"));
        r.setKmRecorridos(rs.getFloat("km_recorridos"));
        r.setAmonestaciones(rs.getInt("amonestaciones"));
        return r;
    }
}