package clasesCompartidas;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Musica {
    private static Clip musicaFondo;

    public static void iniciarMusica(String archivo) {
        try {
            URL sonidoURL = Musica.class.getClassLoader().getResource("sonidos/" + archivo);

            if (sonidoURL == null) {
                System.out.println("Aviso: No se encontró el archivo de música: sonidos/" + archivo);
                return; 
            }
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(sonidoURL);
            musicaFondo = AudioSystem.getClip();
            musicaFondo.open(audioIn);
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY); // Repetir indefinidamente
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void detenerMusicaFondo() {
        if (musicaFondo != null && musicaFondo.isRunning()) {
            musicaFondo.stop();
            musicaFondo.close();
        }
    }
}
