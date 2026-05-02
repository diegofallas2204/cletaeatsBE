package cletaeats.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    // Clave secreta estática (En producción, debería venir de variables de entorno)
    // Debe ser de al menos 256 bits (32 caracteres)
    private static final String SECRET_STRING = "ClaveSecretaMuyLargaYSeguraParaCletaEats123!";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // Tiempo de expiración del token (ej. 24 horas)
    private static final long EXPIRATION_TIME = 86400000;

    /**
     * Genera un token JWT para un usuario
     */
    public static String generarToken(String username, String rol) {
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Valida el token y devuelve el nombre de usuario (subject) si es válido
     */
    public static String validarToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            return null; // Token inválido o expirado
        }
    }
}
