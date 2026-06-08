package pong;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Properties;
import com.entropyinteractive.JGame;
import com.entropyinteractive.Keyboard;
import clasesCompartidas.Ranking;
import clasesCompartidas.Sonido;
import clasesCompartidas.Musica;
import clasesCompartidas.conversorTecla;

public class Pong extends JGame {
    private Pelota pelota;
    private Paleta paletaIzquierda;
    private Paleta paletaDerecha;
    private Arco arcoIzquierdo;
    private Arco arcoDerecho;
    private boolean esperandoReinicio = false;
    private double tiempoEspera = 0;
    private int teclaArribaJ1;
    private int teclaAbajoJ1;
    private int teclaArribaJ2;
    private int teclaAbajoJ2;
    protected Properties appProperties;
    private final Properties appProperties2;
    private Cancha cancha;
    private final Keyboard key = this.getKeyboard();

    private static final double TIEMPO_ESPERA_MAXIMO = 2.0;
    private int estado;
    private final int ESTADO_MENU    = 0;
    private final int ESTADO_JUEGO   = 1;
    private final int ESTADO_GANADOR = 3;
    private final int ESTADO_PAUSA   = 4;
    private String ganador;
    private boolean contraBot = false;

    // ── Ranking ───────────────────────────────────────────────────────────────
    private RankingPong ranking;

    public Pong(String title, int width, int height) {
        super(title, width, height);
        appProperties  = new Properties();
        appProperties2 = new Properties();
    }

   
    // ── STARTUP ───────────────────────────────────────────────────────────────
  
    @Override
    public void gameStartup() {
        try {
            ranking = new RankingPong();
            estado  = ESTADO_MENU;
        } catch (Exception ex) {
            System.out.println("ERROR en gameStartup");
            ex.printStackTrace();
        }
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @Override
    public void gameUpdate(double delta) {

        // ── Pausa ─────────────────────────────────────────────────────────────
        if (estado == ESTADO_PAUSA) {
            for (KeyEvent event : key.getEvents()) {
                if (event.getID() == KeyEvent.KEY_PRESSED && key.isKeyPressed(KeyEvent.VK_P)) {
                    estado = ESTADO_JUEGO;
                }
            }
            return;
        }

        // ── Menú ──────────────────────────────────────────────────────────────
        if (estado == ESTADO_MENU) {
            if (key.isKeyPressed(KeyEvent.VK_1)) {
                iniciarJuego(true);
            } else if (key.isKeyPressed(KeyEvent.VK_2)) {
                iniciarJuego(false);
            }
        }

        if (estado != ESTADO_JUEGO) {
            if (getKeyboard().isKeyPressed(KeyEvent.VK_ESCAPE)) {
                estado = ESTADO_MENU;
                Musica.detenerMusicaFondo();
            }
            return;
        }

        pausar();

        if (esperandoReinicio) {
            tiempoEspera += delta;
            if (tiempoEspera >= TIEMPO_ESPERA_MAXIMO) {
                pelota.reiniciarPelota();
                esperandoReinicio = false;
                tiempoEspera = 0;
            }
            return;
        }

        paletaIzquierda.update(delta);
        paletaDerecha.update(delta);
        pelota.update(delta);

        if (pelota.colisiona(paletaIzquierda)) {
            Sonido.reproducir("colisionPelota.wav");
            pelota.setVelocidadX((Math.abs(pelota.getVelocidadX())) * 1.15);
        }
        if (pelota.colisiona(paletaDerecha)) {
            Sonido.reproducir("colisionPelota.wav");
            pelota.setVelocidadX((-Math.abs(pelota.getVelocidadX())) * 1.15);
        }
        if (pelota.getY() <= 37) {
            Sonido.reproducir("colisionPelota.wav");
            pelota.setY(37);
            pelota.invertirDireccionY();
        }
        if (pelota.getY() + pelota.getAlto() >= getHeight()) {
            Sonido.reproducir("colisionPelota.wav");
            pelota.setY(getHeight() - pelota.getAlto());
            pelota.invertirDireccionY();
        }

        if (arcoIzquierdo.detectaGol(pelota) || arcoDerecho.detectaGol(pelota)) {
            Sonido.reproducir("gameOver.wav");
            esperandoReinicio = true;
            pelota.setVelocidadX(0);
            pelota.setVelocidadY(0);

            if (arcoIzquierdo.getMarcador().getPuntaje() == 10
             || arcoDerecho.getMarcador().getPuntaje()   == 10) {
                estado = ESTADO_GANADOR;
                // ── Guardar en ranking al terminar la partida ─────────────────
                manejarGuardadoRanking();
                return;
            }
        }

        if (getKeyboard().isKeyPressed(KeyEvent.VK_ESCAPE)) {
            estado = ESTADO_MENU;
        }
    }

   
    // ── DRAW ──────────────────────────────────────────────────────────────
  
    @Override
    public void gameDraw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (estado == ESTADO_MENU) {
            // ── Título ────────────────────────────────────────────────────────
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 32));
            g.drawString("PONG", getWidth() / 2 - 40, 60);

            // ── Ranking en el menú ────────────────────────────────────────────
            dibujarTablaRanking(g, 100);

            // ── Opciones ──────────────────────────────────────────────────────
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            g.drawString("[ 1 ] Un jugador (vs Bot)", getWidth() / 2 - 130, getHeight() - 70);
            g.drawString("[ 2 ] Dos jugadores",       getWidth() / 2 - 130, getHeight() - 40);

        } else if (estado == ESTADO_JUEGO || estado == ESTADO_PAUSA) {
            if (cancha != null) cancha.mostrar(g, getWidth(), getHeight());
            pelota.mostrar(g);
            paletaIzquierda.mostrar(g);
            paletaDerecha.mostrar(g);
            arcoIzquierdo.getMarcador().mostrar(g);
            arcoDerecho.getMarcador().mostrar(g);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            g.drawString("Menu: Esq  |  Pausa: P", 12, getHeight() - 10);

            if (estado == ESTADO_PAUSA) {
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.YELLOW);
                g.setFont(new Font("SansSerif", Font.BOLD, 48));
                String pausaTxt = "PAUSA";
                g.drawString(pausaTxt, (getWidth() - g.getFontMetrics().stringWidth(pausaTxt)) / 2, getHeight() / 2);
                g.setFont(new Font("SansSerif", Font.PLAIN, 18));
                g.setColor(Color.WHITE);
                String reanTxt = "[ P ] para reanudar";
                g.drawString(reanTxt, (getWidth() - g.getFontMetrics().stringWidth(reanTxt)) / 2, getHeight() / 2 + 50);
            }

        } else if (estado == ESTADO_GANADOR) {
            g.setColor(new Color(0, 0, 0, 230));
            g.fillRect(0, 0, getWidth(), getHeight());

            // Determinar ganador
            if (arcoIzquierdo.getMarcador().getPuntaje() == 10) {
                ganador = contraBot ? "CPU" : "Jugador 2";
            } else {
                ganador = "Jugador 1";
            }

            g.setColor(Color.YELLOW);
            g.setFont(new Font("SansSerif", Font.BOLD, 36));
            String winTxt = "¡" + ganador + " ganó!";
            g.drawString(winTxt, (getWidth() - g.getFontMetrics().stringWidth(winTxt)) / 2, 60);

            // ── Ranking al finalizar ───────────────────────────────────────────
            dibujarTablaRanking(g, 110);

            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.setColor(Color.WHITE);
            String volver = "[ ESC ] para volver al menú";
            g.drawString(volver, (getWidth() - g.getFontMetrics().stringWidth(volver)) / 2, getHeight() - 20);
        }
    }

    
    // ── RANKING ──────────────────────────────────────────────────────────────
    

    private void manejarGuardadoRanking() {
        // Determinar ganador y su puntaje
        String nombreGanador;
        int puntajeGanador;

        if (arcoIzquierdo.getMarcador().getPuntaje() == 10) {
            nombreGanador  = contraBot ? "CPU" : "Jugador 2";
            puntajeGanador = arcoIzquierdo.getMarcador().getPuntaje();
        } else {
            nombreGanador  = "Jugador 1";
            puntajeGanador = arcoDerecho.getMarcador().getPuntaje();
        }

        // Solo pedimos nombre si no es la CPU
        if (!"CPU".equals(nombreGanador)) {
            String nombre = javax.swing.JOptionPane.showInputDialog(
                null,
                "¡" + nombreGanador + " ganó con " + puntajeGanador + " puntos!\n"
                    + "Ingresá tu nombre para el ranking:",
                "Guardar en Ranking",
                javax.swing.JOptionPane.PLAIN_MESSAGE
            );
            if (nombre != null && !nombre.trim().isEmpty()) {
                nombreGanador = nombre.trim();
            }
        }

        ranking.guardarPartida(nombreGanador, puntajeGanador);
    }

    private void dibujarTablaRanking(Graphics2D g, int inicioY) {
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.setColor(Color.YELLOW);
        String cab = "=== TOP 10 MEJORES PUNTAJES ===";
        g.drawString(cab, (getWidth() - g.getFontMetrics().stringWidth(cab)) / 2, inicioY);

        g.setFont(new Font("Monospaced", Font.PLAIN, 15));
        g.setColor(Color.CYAN);
        String cols = String.format("%-3s %-18s %-10s %-16s", "POS", "JUGADOR", "PUNTOS", "FECHA");
        int posX = (getWidth() - g.getFontMetrics().stringWidth(cols)) / 2;
        g.drawString(cols, posX, inicioY + 28);

        g.setColor(Color.WHITE);
        ArrayList<Ranking.Entrada> top10 = ranking.obtenerTop10();
        int y = inicioY + 52;
        int pos = 1;

        for (Ranking.Entrada e : top10) {
            String fila = String.format("%02d. %-18s %6d pts  %s",
                pos,
                e.nombre.length() > 17 ? e.nombre.substring(0, 17) : e.nombre,
                e.puntaje,
                e.fecha
            );
            g.drawString(fila, posX, y);
            y += 22;
            pos++;
        }

        if (top10.isEmpty()) {
            g.setColor(Color.GRAY);
            String vacio = "No hay registros aún. ¡Sé el primero!";
            g.drawString(vacio, (getWidth() - g.getFontMetrics().stringWidth(vacio)) / 2, y + 10);
        }
    }

    
    // ── UTILS ──────────────────────────────────────────────────────────────

    @Override
    public void gameShutdown() {
        if (ranking != null) ranking.cerrar();
    }

    private void iniciarJuego(boolean conBot) {
        contraBot = conBot;
        estado    = ESTADO_JUEGO;
        cancha    = new Cancha();
        pelota    = new Pelota(10, 400, 300, 250, 250);
        Keyboard teclado = getKeyboard();

        String rutaArchivo = "defaultPong.properties";
        MenuConfig.cargarEnArchivo(appProperties,  rutaArchivo);
        MenuConfig.cargarEnArchivo(appProperties2, "jgame.properties");

        try {
            String t1Arriba = appProperties.getProperty("movArriba1", "W");
            String t1Abajo  = appProperties.getProperty("movAbajo1",  "S");
            String t2Arriba = appProperties.getProperty("movArriba2", "\u2191");
            String t2Abajo  = appProperties.getProperty("movAbajo2",  "\u2193");

            cancha.setEstilo(appProperties.getProperty("cancha", "Original"));

            String pistaMusical    = appProperties.getProperty("pistaMusical", "pong_cancion.wav");
            boolean musicaActivada = Boolean.parseBoolean(appProperties.getProperty("musicaBox", "true"));
            Musica.detenerMusicaFondo();
            if (musicaActivada) Musica.iniciarMusica(pistaMusical);

            teclaArribaJ1 = conversorTecla.convertirTecla(t1Arriba);
            teclaAbajoJ1  = conversorTecla.convertirTecla(t1Abajo);
            teclaArribaJ2 = conversorTecla.convertirTecla(t2Arriba);
            teclaAbajoJ2  = conversorTecla.convertirTecla(t2Abajo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        paletaIzquierda = new Paleta(10, 90, 30, 270, teclado, teclaArribaJ1, teclaAbajoJ1);
        paletaDerecha   = conBot
            ? new BotPaleta(10, 90, 760, 270, pelota)
            : new Paleta(10, 90, 760, 270, teclado, teclaArribaJ2, teclaAbajoJ2);

        arcoIzquierdo = new Arco(0, true);
        arcoDerecho   = new Arco(getWidth(), false);

        try {
            pelota.setEstilo(appProperties.getProperty("pelota", "Original"));
            paletaIzquierda.setEstilo(appProperties.getProperty("paleta", "Original"));
            paletaDerecha.setEstilo(appProperties.getProperty("paleta", "Original"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pausar() {
        for (KeyEvent event : key.getEvents()) {
            if (event.getID() == KeyEvent.KEY_PRESSED && key.isKeyPressed(KeyEvent.VK_P)) {
                estado = ESTADO_PAUSA;
            }
        }
    }
}