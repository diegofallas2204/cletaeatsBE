package cletaeats;

import cletaeats.config.Conexion;
import java.sql.*;

public class DbCheck {
    public static void main(String[] args) {
        try {
            Connection conn = Conexion.getInstancia().getConnection();
            String sql = "SELECT username, activo, rol FROM usuarios";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                System.out.println("Usuarios en la base de datos:");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println("- " + rs.getString("username") + " (Activo: " + rs.getInt("activo") + ", Rol: " + rs.getString("rol") + ")");
                }
                if (!found) {
                    System.out.println("No se encontraron usuarios.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
