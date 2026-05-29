package  cletaeats.controllers;

import com.google.gson.Gson;
import cletaeats.config.RespuestaJSON;
import cletaeats.models.Usuario;
import cletaeats.services.AuthService;
import cletaeats.repositories.UsuarioRepository;
import java.util.Map;

public class UsuarioController {
    private final AuthService authService = new AuthService();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final Gson gson = new Gson();

    /**
     * Recibe un JSON String del cuerpo del request, lo convierte a objeto,
     * procesa y devuelve un JSON String de respuesta.
     */
    public String login(String jsonInput) {
        try {
            // Deserialización: JSON -> Map
            Map<String, String> credenciales = gson.fromJson(jsonInput, Map.class);
            String user = credenciales.get("username");
            String pass = credenciales.get("password");

            Usuario usuario = authService.login(user, pass);

            // Serialización: Objeto -> JSON
            return gson.toJson(RespuestaJSON.exito(usuario));

        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar(e.getMessage()));
        }
    }

    public String registrar(String jsonInput) {
        try {
            Usuario nuevo = gson.fromJson(jsonInput, Usuario.class);
            int id = authService.registrarUsuarioBase(nuevo);

            nuevo.setId(id);
            nuevo.setActivo(true); // Reflejar el estado real en la base de datos
            nuevo.setPassword(null); // Ocultar la contraseña por seguridad en la respuesta

            return gson.toJson(RespuestaJSON.exito(nuevo));
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar(e.getMessage()));
        }
    }

    public String logout() {
        // En JWT, el logout es principalmente del lado del cliente (borrar el token).
        // Aquí devolvemos éxito para confirmar la petición.
        return gson.toJson(RespuestaJSON.exito("Sesión cerrada correctamente"));
    }

    public String getPerfil(String username) {
        try {
            if (username == null || username.isBlank()) {
                return gson.toJson(RespuestaJSON.fallar("Username no proporcionado"));
            }

            Usuario usuario = authService.obtenerPerfilCompleto(username);

            // Mapear a DTO con solo los campos necesarios
            Map<String, Object> perfilData = new java.util.HashMap<>();
            perfilData.put("username", usuario.getUsername());
            perfilData.put("nombre", usuario.getNombre());
            perfilData.put("cedula", usuario.getCedula());
            perfilData.put("direccion", usuario.getDireccion());
            perfilData.put("telefono", usuario.getTelefono());
            perfilData.put("email", usuario.getEmail());
            perfilData.put("rol", usuario.getRol());

            return gson.toJson(RespuestaJSON.exito(perfilData));
        } catch (Exception e) {
            return gson.toJson(RespuestaJSON.fallar("Error al obtener perfil: " + e.getMessage()));
        }
    }
}