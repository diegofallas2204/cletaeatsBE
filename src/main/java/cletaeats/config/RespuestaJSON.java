package cletaeats.config;

/**
 * Clase genérica para estandarizar las respuestas JSON.
 * @param <T> El tipo de objeto que contiene el campo 'data'.
 */
public class RespuestaJSON<T> {
    private boolean success;
    private T data;
    private String error;

    public RespuestaJSON(boolean success, T data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    // Método estático genérico para respuestas exitosas
    public static <T> RespuestaJSON<T> exito(T data) {
        return new RespuestaJSON<>(true, data, null);
    }

    // Método estático genérico para respuestas fallidas
    public static <T> RespuestaJSON<T> fallar(String mensaje) {
        return new RespuestaJSON<>(false, null, mensaje);
    }

    // Getters (necesarios para que GSON pueda leer los campos privamos)
    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getError() { return error; }
}