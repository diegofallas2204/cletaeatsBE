package cletaeats.repositories;

import cletaeats.config.Conexion;
import cletaeats.models.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {
    private final Conexion conexion = Conexion.getInstancia();

    public boolean crear(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (usuario_id, cedula, nombre, direccion, telefono, email, tarjeta, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cliente.getUsuarioId());
            stmt.setString(2, cliente.getCedula());
            stmt.setString(3, cliente.getNombre());
            stmt.setString(4, cliente.getDireccion());
            stmt.setString(5, cliente.getTelefono());
            stmt.setString(6, cliente.getEmail());
            stmt.setString(7, cliente.getTarjeta());
            stmt.setString(8, "activo");
            
            return stmt.executeUpdate() > 0;
        }
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        return ejecutarConsultaUnica(sql, id);
    }

    public Cliente buscarPorUsuarioId(int usuarioId) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE usuario_id = ?";
        return ejecutarConsultaUnica(sql, usuarioId);
    }

    public boolean existeCedula(String cedula) throws SQLException {
        String sql = "SELECT COUNT(*) FROM clientes WHERE cedula = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cedula);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearResultSetACliente(rs));
            }
        }
        return lista;
    }

    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre=?, direccion=?, telefono=?, email=?, tarjeta=?, estado=? WHERE id=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getDireccion());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getTarjeta());
            stmt.setString(6, cliente.getEstado());
            stmt.setInt(7, cliente.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Cliente> listarPorEstado(String estado) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE estado = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSetACliente(rs));
                }
            }
        }
        return lista;
    }

    private Cliente ejecutarConsultaUnica(String sql, int parametro) throws SQLException {
        try (Connection conn = conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, parametro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapearResultSetACliente(rs);
            }
        }
        return null;
    }

    private Cliente mapearResultSetACliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setUsuarioId(rs.getInt("usuario_id"));
        c.setCedula(rs.getString("cedula"));
        c.setNombre(rs.getString("nombre"));
        c.setDireccion(rs.getString("direccion"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setTarjeta(rs.getString("tarjeta"));
        c.setEstado(rs.getString("estado"));
        return c;
    }
}