package cletaeats.services;

import cletaeats.models.*;
import cletaeats.repositories.*;
import java.sql.SQLException;
import java.util.List;

public class PedidoService {
    private final PedidoRepository pedidoRepo = new PedidoRepository();
    private final ClienteRepository clienteRepo = new ClienteRepository();
    private final RepartidorRepository repartidorRepo = new RepartidorRepository();

    public int procesarNuevoPedido(Pedido pedido, boolean esFeriado) throws Exception {
        // 1. Validar Cliente
        Cliente cliente = clienteRepo.buscarPorId(pedido.getClienteId());
        if (cliente == null) throw new Exception("Cliente no existe");
        if (!"activo".equals(cliente.getEstado())) {
            throw new Exception("El cliente está suspendido y no puede realizar pedidos");
        }

        // 2. Asignar Repartidor Automáticamente (Requerimiento 8.4)
        Repartidor disponible = repartidorRepo.obtenerDisponibleParaAsignacion();
        if (disponible == null) {
            throw new Exception("No hay repartidores disponibles en este momento");
        }
        pedido.setRepartidorId(disponible.getId());

        // 3. Cálculos Financieros
        float subtotal = 0;
        for (DetallePedido det : pedido.getDetalles()) {
            // Según Enunciado: Combo 1 = 4000, Combo 2 = 5000...
            // Lógica: (numero_combo * 1000) + 3000
            float precioCombo = (det.getComboId() * 1000) + 3000;
            det.setPrecioUnitario(precioCombo);
            subtotal += (precioCombo * det.getCantidad());
        }

        // 4. Costo Envío (Hábiles 1000/km, Feriados 1500/km)
        float tarifaKm = esFeriado ? 1500f : 1000f;
        float costoEnvio = pedido.getDistanciaKm() * tarifaKm;

        // 5. Impuestos y Totales
        float iva = subtotal * 0.13f;
        float total = subtotal + iva + costoEnvio;

        pedido.setSubtotal(subtotal);
        pedido.setCostoEnvio(costoEnvio);
        pedido.setIva(iva);
        pedido.setTotal(total);
        pedido.setEstado("preparacion");

        // 6. Guardar y Cambiar estado del repartidor a ocupado
        int idPedido = pedidoRepo.crearPedido(pedido);
        repartidorRepo.actualizarEstado(disponible.getId(), "ocupado");

        return idPedido;
    }

    public List<Pedido> obtenerHistorialCliente(int clienteId) throws SQLException {
        return pedidoRepo.listarPorCliente(clienteId);
    }
}
