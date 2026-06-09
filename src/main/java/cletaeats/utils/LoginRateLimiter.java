package cletaeats.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginRateLimiter {
    private static final int MAX_ATTEMPTS = 10;
    private static final long WINDOW_MS = 10 * 60 * 1000L; // 10 minutos

    private record Bucket(AtomicInteger count, long windowStart) {}

    private static final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private LoginRateLimiter() {}

    /**
     * Registra un intento de login para la IP dada.
     * @return true si el intento está permitido, false si debe bloquearse.
     */
    public static boolean isAllowed(String ip) {
        long now = System.currentTimeMillis();
        Bucket bucket = buckets.compute(ip, (key, existing) -> {
            if (existing == null || now - existing.windowStart() >= WINDOW_MS) {
                return new Bucket(new AtomicInteger(0), now);
            }
            return existing;
        });
        return bucket.count().incrementAndGet() <= MAX_ATTEMPTS;
    }
}
