package lodeRunner;


public class Configuracion {
    // Valores por defecto según tu consigna
    public static boolean pantallaCompleta = false;
    public static boolean sonidoGeneralAtrapado = true; // Master Switch
    public static boolean musicaActivada = true;
    public static boolean efectosActivados = true;
    public static boolean sonidoGeneralActivado = true; //Agrego flor

    public static String pistaMusicalSeleccionada = "LR_musiquilla.wav";
    public static String skinPersonajeSeleccionado = "original";

    public static float volumenMusica  = 0.3f;
    public static float volumenEfectos = 0.3f;
    
    // Método para restablecer (Botón Reset de tu UI)
    public static void reset() {
        pantallaCompleta = false;
        sonidoGeneralAtrapado = true;
        musicaActivada = true;
        efectosActivados = true;
        sonidoGeneralActivado = true; //Agrego flor
        pistaMusicalSeleccionada = "LR_musiquilla.wav";
        skinPersonajeSeleccionado = "original";
        volumenMusica  = 0.3f;
        volumenEfectos = 0.3f;
    }
    
}