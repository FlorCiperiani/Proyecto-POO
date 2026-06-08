package clasesCompartidas;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Musica {
    private static Clip musicaFondo;

    public static void iniciarMusica(String archivo) {
        try {
            URL sonidoURL = Musica.class.getClassLoader().getResource("sonidos/" + archivo);

            // FIX: si el archivo no existe, no crashea — solo avisa en consola
            if (sonidoURL == null) {
                System.out.println("Musica: no se encontró 'sonidos/" + archivo + "'. El juego continúa sin música.");
                return;
            }

            // Si había música anterior, la detenemos antes de abrir una nueva
            detenerMusicaFondo();

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(sonidoURL);
            musicaFondo = AudioSystem.getClip();
            musicaFondo.open(audioIn);
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
            musicaFondo.start(); //  sin esto la música no suena
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Musica: error al reproducir '" + archivo + "': " + e.getMessage());
        }
    }

    public static void detenerMusicaFondo() {
        if (musicaFondo != null && musicaFondo.isRunning()) {
            musicaFondo.stop();
            musicaFondo.close();
            musicaFondo = null;
        }
    }
}