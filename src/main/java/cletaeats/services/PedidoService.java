package cletaeats.services;

import cletaeats.models.*;
import cletaeats.repositories.*;
import java.sql.SQLException;
import java.util.List;

public class PedidoService {
    private final PedidoRepository pedidoRepo = new PedidoRepository();
    private final ClienteRepository clienteRepo = new ClienteRepository();
    private final RepartidorRepository repartidorRepo = new RepartidorRepository();
    private final UsuarioRepository usuarioRepo = new UsuarioRepository();
    private final ComboRepository comboRepo = new ComboRepository();
    private final MetodoPagoRepository metodoPagoRepo = new MetodoPagoRepository();

    public int procesarNuevoPedido(Pedido pedido, boolean esFeriado, String username) throws Exception {
        if (username == null || username.isBlank()) {
            throw new Exception("Usuario no autenticado");
        }

        Usuario usuario = usuarioRepo.findByUsername(username);
        if (usuario == null) {
            throw new Exception("Usuario autenticado no encontrado");
        }

        Cliente cliente = clienteRepo.buscarPorUsuarioId(usuario.getId());
        if (cliente == null) throw new Exception("Cliente no existe");
        if (!"activo".equals(cliente.getEstado())) {
            throw new Exception("El cliente está suspendido y no puede realizar pedidos");
        }

        String numeroTarjeta = pedido.getNumeroTarjeta();
        if (numeroTarjeta == null || numeroTarjeta.isBlank() || !metodoPagoRepo.existeTarjetaCliente(cliente.getId(), numeroTarjeta)) {
            throw new Exception("Método de pago no válido");
        }

        // Ignorar el clienteId enviado desde el payload y usar siempre el cliente del token.
        pedido.setClienteId(cliente.getId());

        // 2. Asignar Repartidor Automáticamente (Requerimiento 8.4)
        Repartidor disponible = repartidorRepo.obtenerDisponibleParaAsignacion();
        if (disponible == null) {
            // No hay repartidor disponible ahora; el pedido se crea igual y queda en preparación.
            pedido.setRepartidorId(0);
        } else {
            pedido.setRepartidorId(disponible.getId());
        }

        // 3. Cálculos Financieros
        float subtotal = 0;
        for (DetallePedido det : pedido.getDetalles()) {
            if (det == null) {
                throw new Exception("Detalle del pedido inválido");
            }
            Integer comboId = det.getComboId();
            if (comboId == null || comboId == 0) {
                throw new Exception("comboId inválido en detalle de pedido: " + comboId);
            }
            var combo = comboRepo.buscarPorId(comboId);
            if (combo == null) {
                throw new Exception("Combo no encontrado para comboId: " + comboId);
            }

            float precioCombo = combo.getPrecio();
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

        // 6. Guardar y cambiar estado del repartidor a ocupado solo si se asignó uno.
        int idPedido = pedidoRepo.crearPedido(pedido);
        if (disponible != null) {
            repartidorRepo.actualizarEstado(disponible.getId(), "ocupado");
        }

        return idPedido;
    }

    public List<Pedido> obtenerHistorialCliente(int clienteId) throws SQLException {
        return pedidoRepo.listarPorCliente(clienteId);
    }
}
