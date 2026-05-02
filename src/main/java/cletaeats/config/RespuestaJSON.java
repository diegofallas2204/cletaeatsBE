package cletaeats.config;

public class RespuestaJSON<T> {
    private boolean success;
    private T data;
    private String error;

    public RespuestaJSON(boolean success, T data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> RespuestaJSON<T> exito(T data) {
        return new RespuestaJSON<>(true, data, null);
    }

    public static <T> RespuestaJSON<T> fallar(String mensaje) {
        return new RespuestaJSON<>(false, null, mensaje);
    }

    // Getters necesarios para que Gson pueda leer los campos
    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getError() { return error; }
}