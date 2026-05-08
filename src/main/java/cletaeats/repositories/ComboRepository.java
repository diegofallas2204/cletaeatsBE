package cletaeats.repositories;

import cletaeats.config.Conexion;
import cletaeats.models.Combo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComboRepository {
    private final Conexion conexion = Conexion.getInstancia();

    public boolean crear(Combo combo) throws SQLException {
        String sql = "INSERT INTO combos (restaurante_id, numero_combo, nombre, precio) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, combo.getRestauranteId());
            stmt.setInt(2, combo.getNumeroCombo());
            stmt.setString(3, combo.getNombre());
            stmt.setFloat(4, combo.getPrecio());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Combo> listarTodos() throws SQLException {
        List<Combo> lista = new ArrayList<>();
        String sql = "SELECT * FROM combos";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearResultSetACombo(rs));
            }
        }
        return lista;
    }

    public List<Combo> listarPorRestaurante(int restauranteId) throws SQLException {
        List<Combo> lista = new ArrayList<>();
        String sql = "SELECT * FROM combos WHERE restaurante_id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, restauranteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSetACombo(rs));
                }
            }
        }
        return lista;
    }

    public boolean actualizar(Combo combo) throws SQLException {
        String sql = "UPDATE combos SET nombre=?, precio=? WHERE id=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, combo.getNombre());
            stmt.setFloat(2, combo.getPrecio());
            stmt.setInt(3, combo.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM combos WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Combo mapearResultSetACombo(ResultSet rs) throws SQLException {
        Combo c = new Combo();
        c.setId(rs.getInt("id"));
        c.setRestauranteId(rs.getInt("restaurante_id"));
        c.setNumeroCombo(rs.getInt("numero_combo"));
        c.setNombre(rs.getString("nombre"));
        c.setPrecio(rs.getFloat("precio"));
        return c;
    }
}
