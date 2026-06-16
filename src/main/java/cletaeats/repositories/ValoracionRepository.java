package cletaeats.repositories;

import cletaeats.config.Conexion;
import cletaeats.models.Valoracion;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Lista todas las valoraciones (estrellas + comentario) con el nombre del
     * cliente y del restaurante asociados al pedido, para mostrarlas en el admin.
     */
    public List<Map<String, Object>> listarTodas() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT v.id, v.pedido_id, v.cliente_id, v.rating, v.comentario, v.fecha, " +
                     "IFNULL(c.nombre, '') AS cliente_nombre, " +
                     "IFNULL(r.nombre, '') AS restaurante_nombre " +
                     "FROM valoraciones v " +
                     "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                     "LEFT JOIN pedidos p ON v.pedido_id = p.id " +
                     "LEFT JOIN restaurantes r ON p.restaurante_id = r.id " +
                     "ORDER BY v.fecha DESC";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id",                rs.getInt("id"));
                row.put("pedidoId",          rs.getInt("pedido_id"));
                row.put("clienteId",         rs.getInt("cliente_id"));
                row.put("rating",            rs.getInt("rating"));
                row.put("comentario",        rs.getString("comentario"));
                row.put("fecha",             rs.getString("fecha"));
                row.put("clienteNombre",     rs.getString("cliente_nombre"));
                row.put("restauranteNombre", rs.getString("restaurante_nombre"));
                lista.add(row);
            }
        }
        return lista;
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
