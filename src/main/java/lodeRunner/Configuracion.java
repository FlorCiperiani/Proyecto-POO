package lodeRunner;


public class Configuracion {
    // Valores por defecto según tu consigna
    public static boolean pantallaCompleta = false;
    public static boolean sonidoGeneralAtrapado = true; // Master Switch
    public static boolean musicaActivada = true;
    public static boolean efectosActivados = true;
    public static boolean sonidoGeneralActivado = true; //Agrego flor

    public static String pistaMusicalSeleccionada = "tema_original.wav";
    public static String skinPersonajeSeleccionado = "original";
    
    // Método para restablecer (Botón Reset de tu UI)
    public static void reset() {
        pantallaCompleta = false;
        sonidoGeneralAtrapado = true;
        musicaActivada = true;
        efectosActivados = true;
        sonidoGeneralActivado = true; //Agrego flor
        pistaMusicalSeleccionada = "tema_original.wav";
        skinPersonajeSeleccionado = "original";
    }
    
}