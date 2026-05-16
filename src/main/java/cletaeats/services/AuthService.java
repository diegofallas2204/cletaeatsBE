package cletaeats.services;

import cletaeats.config.Conexion;
import cletaeats.models.Cliente;
import cletaeats.models.Repartidor;
import cletaeats.models.Usuario;
import cletaeats.repositories.ClienteRepository;
import cletaeats.repositories.RepartidorRepository;
import cletaeats.repositories.UsuarioRepository;
import cletaeats.utils.JwtUtil;
import java.sql.Connection;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private final Conexion conexion = Conexion.getInstancia();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final ClienteRepository clienteRepository = new ClienteRepository();
    private final RepartidorRepository repartidorRepository = new RepartidorRepository();

    public Usuario login(String username, String password) throws Exception {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new Exception("Usuario y contraseña son obligatorios");
        }

        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            throw new Exception("Usuario no encontrado");
        }

        if (!usuario.isActivo()) {
            throw new Exception("El usuario está inactivo");
        }

        if (!BCrypt.checkpw(password, usuario.getPassword())) {
            throw new Exception("Contraseña incorrecta");
        }

        String token = JwtUtil.generarToken(usuario.getUsername(), usuario.getRol());
        usuario.setToken(token);
        // Ocultar hash por seguridad
        usuario.setPassword(null);

        return usuario;
    }

    public int registrarUsuarioBase(Usuario usuario) throws Exception {
        if (usuario == null) {
            throw new Exception("Datos de usuario inválidos");
        }
        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            throw new Exception("El nombre de usuario es obligatorio");
        }
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new Exception("La contraseña es obligatoria");
        }
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            throw new Exception("El rol es obligatorio");
        }
        if (usuarioRepository.existeUsername(usuario.getUsername())) {
            throw new Exception("El nombre de usuario ya existe");
        }

        String hash = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());

        Connection conn = null;
        try {
            conn = conexion.getConnection();
            conn.setAutoCommit(false);

            Usuario nuevo = new Usuario();
            nuevo.setUsername(usuario.getUsername());
            nuevo.setPassword(hash);
            nuevo.setRol(usuario.getRol());
            nuevo.setActivo(true);

            int usuarioId = usuarioRepository.crear(nuevo, conn);

            if ("cliente".equalsIgnoreCase(usuario.getRol())) {
                validarDatosPerfil(usuario);
                Cliente cliente = new Cliente();
                cliente.setUsuarioId(usuarioId);
                cliente.setCedula(usuario.getCedula());
                cliente.setNombre(usuario.getNombre());
                cliente.setDireccion(usuario.getDireccion());
                cliente.setTelefono(usuario.getTelefono());
                cliente.setEmail(usuario.getEmail());

                if (!clienteRepository.crear(cliente, conn)) {
                    throw new Exception("Error al crear el perfil de cliente");
                }
            } else if ("repartidor".equalsIgnoreCase(usuario.getRol())) {
                validarDatosPerfil(usuario);
                Repartidor repartidor = new Repartidor();
                repartidor.setUsuarioId(usuarioId);
                repartidor.setCedula(usuario.getCedula());
                repartidor.setNombre(usuario.getNombre());
                repartidor.setDireccion(usuario.getDireccion());
                repartidor.setTelefono(usuario.getTelefono());
                repartidor.setEmail(usuario.getEmail());

                if (!repartidorRepository.crear(repartidor, conn)) {
                    throw new Exception("Error al crear el perfil de repartidor");
                }
            }

            conn.commit();
            return usuarioId;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new Exception("Error al registrar usuario: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    private void validarDatosPerfil(Usuario usuario) throws Exception {
        if (usuario.getCedula() == null || usuario.getCedula().isBlank()) {
            throw new Exception("La cédula es obligatoria para el perfil");
        }
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new Exception("El nombre es obligatorio para el perfil");
        }
        if (usuario.getDireccion() == null || usuario.getDireccion().isBlank()) {
            throw new Exception("La dirección es obligatoria para el perfil");
        }
        if (usuario.getTelefono() == null || usuario.getTelefono().isBlank()) {
            throw new Exception("El teléfono es obligatorio para el perfil");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new Exception("El email es obligatorio para el perfil");
        }
    }
}