package cletaeats.repositories;

import cletaeats.config.Conexion;
import cletaeats.models.MetodoPago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MetodoPagoRepository {
    private final Conexion conexion = Conexion.getInstancia();

    public List<MetodoPago> listarPorCliente(int clienteId) throws SQLException {
        List<MetodoPago> tarjetas = new ArrayList<>();
        String sql = "SELECT * FROM tarjetas_cliente WHERE cliente_id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MetodoPago t = new MetodoPago();
                    t.setId(rs.getInt("id"));
                    t.setClienteId(rs.getInt("cliente_id"));
                    t.setNumeroTarjeta(rs.getString("numero_tarjeta"));
                    t.setFechaVencimiento(rs.getString("fecha_vencimiento"));
                    t.setCvv(rs.getString("cvv"));
                    tarjetas.add(t);
                }
            }
        }
        return tarjetas;
    }

    public int guardar(MetodoPago tarjeta) throws SQLException {
        String sql = "INSERT INTO tarjetas_cliente (cliente_id, numero_tarjeta, fecha_vencimiento, cvv) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, tarjeta.getClienteId());
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

    public boolean existeTarjetaCliente(int clienteId, String numeroTarjeta) throws SQLException {
        String sql = "SELECT 1 FROM tarjetas_cliente WHERE cliente_id = ? AND numero_tarjeta = ? LIMIT 1";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            stmt.setString(2, numeroTarjeta);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
