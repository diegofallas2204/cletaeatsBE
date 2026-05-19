package cletaeats.repositories;

import cletaeats.config.Conexion;
import cletaeats.models.DetallePedido;
import cletaeats.models.Pedido;
import cletaeats.models.ReporteRestaurante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoRepository {
    private final Conexion conexion = Conexion.getInstancia();

    public int crearPedido(Pedido pedido) throws SQLException {
        String sqlPedido = "INSERT INTO pedidos (cliente_id, restaurante_id, repartidor_id, estado, subtotal, costo_envio, iva, total, distancia_km, fecha_pedido) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        String sqlDetalle = "INSERT INTO pedido_detalle (pedido_id, combo_id, cantidad, precio_unitario, agrandado, notas) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = conexion.getConnection();
            conn.setAutoCommit(false); 

            int pedidoId = -1;
            try (PreparedStatement psP = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                psP.setInt(1, pedido.getClienteId());
                psP.setInt(2, pedido.getRestauranteId());
                if (pedido.getRepartidorId() == 0) {
                    psP.setNull(3, java.sql.Types.INTEGER);
                } else {
                    psP.setInt(3, pedido.getRepartidorId());
                }
                psP.setString(4, pedido.getEstado());
                psP.setFloat(5, pedido.getSubtotal());
                psP.setFloat(6, pedido.getCostoEnvio());
                psP.setFloat(7, pedido.getIva());
                psP.setFloat(8, pedido.getTotal());
                psP.setFloat(9, pedido.getDistanciaKm());

                psP.executeUpdate();

                try (ResultSet rs = psP.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedidoId = rs.getInt(1);
                    }
                }
            }

            if (pedidoId == -1) {
                throw new SQLException("No se pudo obtener el ID del pedido generado.");
            }

            try (PreparedStatement psD = conn.prepareStatement(sqlDetalle)) {
                for (DetallePedido detalle : pedido.getDetalles()) {
                    psD.setInt(1, pedidoId);
                    psD.setInt(2, detalle.getComboId());
                    psD.setInt(3, detalle.getCantidad());
                    psD.setFloat(4, detalle.getPrecioUnitario());
                    psD.setBoolean(5, detalle.isAgrandado());
                    psD.setString(6, detalle.getNotas());
                    psD.addBatch();
                }
                psD.executeBatch();
            }

            conn.commit(); 
            return pedidoId;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException ex) {
                    System.err.println("Error en Rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); 
            }
        }
    }

    public double sumarVentasTotales() throws SQLException {
        String sql = "SELECT SUM(total) as gran_total FROM pedidos WHERE estado = 'entregado'";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("gran_total");
            }
        }
        return 0.0;
    }

    public List<ReporteRestaurante> obtenerVentasAgrupadasPorRestaurante() throws SQLException {
        List<ReporteRestaurante> lista = new ArrayList<>();
        String sql = "SELECT r.nombre, COUNT(p.id) as cantidad_pedidos, IFNULL(SUM(p.total), 0) as total_ventas " +
                "FROM restaurantes r " +
                "LEFT JOIN pedidos p ON r.id = p.restaurante_id AND p.estado = 'entregado' " +
                "GROUP BY r.id, r.nombre " +
                "ORDER BY total_ventas DESC";

        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ReporteRestaurante rep = new ReporteRestaurante();
                rep.setNombreRestaurante(rs.getString("nombre"));
                rep.setCantidadPedidos(rs.getInt("cantidad_pedidos"));
                rep.setTotalVendido(rs.getDouble("total_ventas"));
                lista.add(rep);
            }
        }
        return lista;
    }

    public List<Pedido> listarPorCliente(int clienteId) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE cliente_id = ? ORDER BY fecha_pedido DESC";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
        }
        return pedidos;
    }

    public List<Pedido> listarPorRepartidor(int repartidorId) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE repartidor_id = ? AND estado != 'entregado' AND estado != 'suspendido' ORDER BY fecha_pedido DESC";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, repartidorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
        }
        return pedidos;
    }

    public boolean actualizarEstadoPedido(int pedidoId, String nuevoEstado) throws SQLException {
        System.out.println("[PedidoRepository] actualizarEstadoPedido: pedidoId=" + pedidoId + ", nuevoEstado=" + nuevoEstado);
        String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, pedidoId);
            int rows = stmt.executeUpdate();
            System.out.println("[PedidoRepository] Filas afectadas: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[PedidoRepository] Error SQL en actualizarEstadoPedido: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getInt("id"));
        p.setClienteId(rs.getInt("cliente_id"));
        p.setRestauranteId(rs.getInt("restaurante_id"));
        p.setRepartidorId(rs.getInt("repartidor_id"));
        p.setEstado(rs.getString("estado"));
        p.setSubtotal(rs.getFloat("subtotal"));
        p.setCostoEnvio(rs.getFloat("costo_envio"));
        p.setIva(rs.getFloat("iva"));
        p.setTotal(rs.getFloat("total"));
        p.setDistanciaKm(rs.getFloat("distancia_km"));
        return p;
    }
}