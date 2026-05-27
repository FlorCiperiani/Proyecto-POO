package clasesCompartidas;

import java.awt.event.KeyEvent;

public class conversorTecla {

    public static int convertirTecla(String valor) {
        if (valor == null || valor.isEmpty()) {
            throw new IllegalArgumentException("El valor de la tecla no puede ser nulo o vacío");
        }

        // Si el valor es una flecha representada como \u2191, \u2193, etc.
        switch (valor) {
            case "\u2191": return KeyEvent.VK_UP;
            case "\u2193": return KeyEvent.VK_DOWN;
        }
        // Si el valor es una letra o número
        valor = valor.toUpperCase();
        if (valor.length() == 1) {
            char c = valor.charAt(0);
            if (Character.isLetterOrDigit(c)) {
                return KeyEvent.getExtendedKeyCodeForChar(c);
            }
        }
        // Si es otro tipo de tecla (Escape, Enter, etc.)
        try {
            return KeyEvent.class.getField("VK_" + valor.toUpperCase()).getInt(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Tecla no reconocida: " + valor, e);
        }
    }
}