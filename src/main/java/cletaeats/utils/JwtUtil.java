package cletaeats.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static final SecretKey SECRET_KEY = buildSecretKey();

    private static SecretKey buildSecretKey() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            if ("true".equalsIgnoreCase(System.getenv("PRODUCTION"))) {
                throw new IllegalStateException("JWT_SECRET env var is required in production");
            }
            // Valor por defecto solo para desarrollo local
            secret = "ClaveSecretaMuyLargaYSeguraParaCletaEats123!";
        }
        if (secret.getBytes().length < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters");
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

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

    /**
     * Extrae el claim "rol" del token sin revalidar la firma (llamar solo tras validarToken).
     */
    public static String obtenerRol(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("rol", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
