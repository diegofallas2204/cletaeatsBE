package cletaeats.services;

import cletaeats.models.ReporteRestaurante;
import cletaeats.repositories.PedidoRepository;
import java.sql.SQLException;
import java.util.List;

public class ReporteService {
    private final PedidoRepository pedidoRepo = new PedidoRepository();

    public double obtenerTotalVendidoGeneral() throws SQLException {
        // Lógica: Sumatoria de la columna 'total' de todos los pedidos 'entregados'
        return pedidoRepo.sumarVentasTotales();
    }

    public List<ReporteRestaurante> ventasPorRestaurante() throws SQLException {
        return pedidoRepo.obtenerVentasAgrupadasPorRestaurante();
    }

    // Este servicio llamará a métodos específicos del PedidoRepository
    // que ejecutan queries con GROUP BY y ORDER BY
}