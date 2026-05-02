package cletaeats.services;

import cletaeats.models.Cliente;
import cletaeats.repositories.ClienteRepository;
import java.sql.SQLException;
import java.util.List;

public class ClienteService {
    private final ClienteRepository clienteRepository = new ClienteRepository();

    public boolean registrarCliente(Cliente cliente) throws Exception {
        if (clienteRepository.existeCedula(cliente.getCedula())) {
            throw new Exception("La cédula ya se encuentra registrada");
        }
        // Validaciones de datos obligatorios según Enunciado.txt
        if (cliente.getNombre() == null || cliente.getTarjeta() == null) {
            throw new Exception("Nombre y tarjeta son obligatorios");
        }
        return clienteRepository.crear(cliente);
    }

    public Cliente obtenerPerfil(int usuarioId) throws SQLException {
        return clienteRepository.buscarPorUsuarioId(usuarioId);
    }

    public List<Cliente> listarClientes(String estado) throws SQLException {
        return clienteRepository.listarPorEstado(estado);
    }
}