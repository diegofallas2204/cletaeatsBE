package cletaeats.utils;

import cletaeats.models.MetodoPago;

import java.util.Calendar;
import java.util.List;

/**
 * Utilidades de tarjetas compartidas entre el flujo de cliente y el de repartidor.
 *
 * Reglas de seguridad (PCI-DSS):
 *  - El CVV NUNCA debe persistirse ni devolverse al cliente.
 *  - El PAN completo no debe exponerse; se enmascara dejando solo el primer
 *    dígito (para detectar la marca) y los últimos 4.
 */
public final class TarjetaUtils {

    private TarjetaUtils() {}

    /**
     * Valida los datos de una tarjeta del lado del servidor (no se puede confiar
     * en la validación del cliente, que es bypasseable llamando la API directo).
     *
     * @return mensaje de error si algo es inválido, o {@code null} si es válida.
     */
    public static String validar(MetodoPago tarjeta) {
        if (tarjeta == null) return "Datos de tarjeta inválidos";

        String num = tarjeta.getNumeroTarjeta() == null ? "" : tarjeta.getNumeroTarjeta().trim();
        if (!num.matches("\\d{15,16}")) return "Número de tarjeta inválido (15 o 16 dígitos)";

        String exp = tarjeta.getFechaVencimiento() == null ? "" : tarjeta.getFechaVencimiento().trim();
        if (!exp.matches("\\d{2}/\\d{2}")) return "Fecha de vencimiento inválida (formato MM/AA)";

        int mes = Integer.parseInt(exp.substring(0, 2));
        int anio = Integer.parseInt(exp.substring(3, 5));
        if (mes < 1 || mes > 12) return "Mes de vencimiento inválido";

        Calendar c = Calendar.getInstance();
        int anioActual = c.get(Calendar.YEAR) % 100;
        int mesActual = c.get(Calendar.MONTH) + 1;
        if (anio < anioActual || (anio == anioActual && mes < mesActual)) {
            return "La tarjeta está vencida";
        }

        String cvv = tarjeta.getCvv() == null ? "" : tarjeta.getCvv().trim();
        if (!cvv.matches("\\d{3,4}")) return "CVV inválido (3 o 4 dígitos)";

        return null;
    }

    /** Elimina el CVV antes de devolver una tarjeta al cliente. */
    public static void ocultarCvv(MetodoPago tarjeta) {
        if (tarjeta != null) tarjeta.setCvv(null);
    }

    public static void ocultarCvv(List<MetodoPago> tarjetas) {
        if (tarjetas != null) tarjetas.forEach(TarjetaUtils::ocultarCvv);
    }

    /** Oculta el CVV y enmascara el PAN dejando primer dígito + últimos 4. */
    public static void enmascarar(MetodoPago tarjeta) {
        if (tarjeta == null) return;
        tarjeta.setCvv(null);
        String num = tarjeta.getNumeroTarjeta();
        if (num != null && num.length() > 5) {
            String last4 = num.substring(num.length() - 4);
            StringBuilder masked = new StringBuilder();
            masked.append(num.charAt(0)); // se conserva para detectar la marca
            for (int i = 1; i < num.length() - 4; i++) masked.append('*');
            masked.append(last4);
            tarjeta.setNumeroTarjeta(masked.toString());
        }
    }

    public static void enmascarar(List<MetodoPago> tarjetas) {
        if (tarjetas != null) tarjetas.forEach(TarjetaUtils::enmascarar);
    }
}
