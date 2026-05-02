package cletaeats.repositories;

import cletaeats.config.Conexion;
import cletaeats.models.Restaurante;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestauranteRepository {
    private final Conexion conexion = Conexion.getInstancia();

    public boolean crear(Restaurante res) throws SQLException {
        String sql = "INSERT INTO restaurantes (nombre, cedula_juridica, direccion, tipo_comida) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, res.getNombre());
            stmt.setString(2, res.getCedulaJuridica());
            stmt.setString(3, res.getDireccion());
            stmt.setString(4, res.getTipoComida());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Restaurante res) throws SQLException {
        String sql = "UPDATE restaurantes SET nombre=?, cedula_juridica=?, direccion=?, tipo_comida=? WHERE id=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, res.getNombre());
            stmt.setString(2, res.getCedulaJuridica());
            stmt.setString(3, res.getDireccion());
            stmt.setString(4, res.getTipoComida());
            stmt.setInt(5, res.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM restaurantes WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Restaurante> listarTodos() throws SQLException {
        List<Restaurante> lista = new ArrayList<>();
        String sql = "SELECT * FROM restaurantes";
        try (Connection conn = conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Restaurante r = new Restaurante();
                r.setId(rs.getInt("id"));
                r.setNombre(rs.getString("nombre"));
                r.setCedulaJuridica(rs.getString("cedula_juridica"));
                r.setDireccion(rs.getString("direccion"));
                r.setTipoComida(rs.getString("tipo_comida"));
                lista.add(r);
            }
        }
        return lista;
    }
}