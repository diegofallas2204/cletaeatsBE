package cletaeats.repositories;

import cletaeats.config.Conexion;
import cletaeats.models.MetodoPago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla tarjetas_repartidor. Espejo de MetodoPagoRepository pero
 * asociando las tarjetas a un repartidor en lugar de a un cliente.
 */
public class MetodoPagoRepartidorRepository {
    private final Conexion conexion = Conexion.getInstancia();

    public List<MetodoPago> listarPorRepartidor(int repartidorId) throws SQLException {
        List<MetodoPago> tarjetas = new ArrayList<>();
        String sql = "SELECT * FROM tarjetas_repartidor WHERE repartidor_id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, repartidorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MetodoPago t = new MetodoPago();
                    t.setId(rs.getInt("id"));
                    t.setNumeroTarjeta(rs.getString("numero_tarjeta"));
                    t.setFechaVencimiento(rs.getString("fecha_vencimiento"));
                    t.setCvv(rs.getString("cvv"));
                    tarjetas.add(t);
                }
            }
        }
        return tarjetas;
    }

    public int guardar(int repartidorId, MetodoPago tarjeta) throws SQLException {
        String sql = "INSERT INTO tarjetas_repartidor (repartidor_id, numero_tarjeta, fecha_vencimiento, cvv) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, repartidorId);
            stmt.setString(2, tarjeta.getNumeroTarjeta());
            stmt.setString(3, tarjeta.getFechaVencimiento());
            stmt.setString(4, tarjeta.getCvv());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean existeTarjetaRepartidor(int repartidorId, String numeroTarjeta) throws SQLException {
        String sql = "SELECT 1 FROM tarjetas_repartidor WHERE repartidor_id = ? AND numero_tarjeta = ? LIMIT 1";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, repartidorId);
            stmt.setString(2, numeroTarjeta);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean eliminarTarjeta(int repartidorId, int tarjetaId) throws SQLException {
        String sql = "DELETE FROM tarjetas_repartidor WHERE id = ? AND repartidor_id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tarjetaId);
            stmt.setInt(2, repartidorId);
            return stmt.executeUpdate() > 0;
        }
    }
}
