package cletaeats.services;

import cletaeats.models.Usuario;
import cletaeats.repositories.UsuarioRepository;
import cletaeats.utils.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

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

    public int registrarUsuarioBase(String username, String password, String rol) throws Exception {
        if (usuarioRepository.existeUsername(username)) {
            throw new Exception("El nombre de usuario ya existe");
        }

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        Usuario nuevo = new Usuario();
        nuevo.setUsername(username);
        nuevo.setPassword(hash);
        nuevo.setRol(rol);
        nuevo.setActivo(true);

        return usuarioRepository.crear(nuevo);
    }
}