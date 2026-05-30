package cletaeats.repositories;

import cletaeats.config.Conexion;
import cletaeats.models.Valoracion;

import java.sql.*;

public class ValoracionRepository {
    private final Conexion conexion = Conexion.getInstancia();

    public Valoracion guardar(Valoracion v) throws SQLException {
        String sql = "INSERT INTO valoraciones (pedido_id, cliente_id, rating, comentario) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, v.getPedidoId());
            stmt.setInt(2, v.getClienteId());
            stmt.setInt(3, v.getRating());
            stmt.setString(4, v.getComentario());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) v.setId(rs.getInt(1));
            }
        }
        return v;
    }

    public boolean existeParaPedido(int pedidoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM valoraciones WHERE pedido_id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pedidoId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
