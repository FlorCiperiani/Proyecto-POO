package lodeRunner;

import com.entropyinteractive.JGame;
import com.entropyinteractive.Keyboard;
import clasesCompartidas.Musica;
import clasesCompartidas.Sonido;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;


public class LodeRunner extends JGame {

    private static final String MUSICA_FONDO = "LR_musiquilla.wav";

    private static final NivelLR[] NIVELES = {
        NivelLR.NIVEL_1,
        NivelLR.NIVEL_2,
        NivelLR.NIVEL_3,
    };

    private MapaLR       mapa;
    private JugadorLR    jugador;
    public static JugadorLR jugadorActual;
    private ArrayList<EnemigoLR> enemigos;
    private NivelLR      nivelActual;
    private int          indiceNivel = 0;

    private double escala  = 1.0;
    private int    offsetX = 0;
    private int    offsetY = 0;
    private static final int HUD_ALTO = 50; //la barra de arriba que no anda :(

    // Estados en que pueda estar
    private boolean victoria     = false;
    private boolean derrota      = false;
    private boolean gameOver     = false;
    private boolean escaleraList = false;
    private boolean juegoCompletado = false;
    private boolean pausado        = false;
    private boolean teclaP_presionada = false;
    private boolean teclaQ_presionada = false;
    private boolean teclaW_presionada = false;

    private double tiempoNivel  = 0;
    private double ultimoDelta  = 0;   // para pasar a dibujarTextosPuntos
    private static final double TIEMPO_MAXIMO = 180.0;

    private int puntajeTotal = 0;
    private int vidasGuardadas = 5;

    public LodeRunner(String titulo, int ancho, int alto) {
    super(titulo, ancho, alto);
    getFrame().addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
            Musica.detenerMusicaFondo();
        }
    });
    }

    //Ciclo JGame
    @Override
    public void gameStartup() {
        indiceNivel = 0;
        puntajeTotal = 0;
        inicializarNivel();
    }

    @Override
    public void gameUpdate(double delta) {
        Keyboard teclado = this.getKeyboard();
        if (teclado.isKeyPressed(KeyEvent.VK_ESCAPE)) { stop(); return; }

        // Pausa
        if (teclado.isKeyPressed(KeyEvent.VK_P) && !teclaP_presionada) {
            teclaP_presionada = true;
            if (!pausado) {
                pausado = true;
                SwingUtilities.invokeLater(() -> new MenuConfigLR());
            } else {
                pausado = false;
            }
        } else if (!teclado.isKeyPressed(KeyEvent.VK_P)) {
            teclaP_presionada = false;
        }
        if (pausado) {
            if (teclado.isKeyPressed(KeyEvent.VK_ENTER)) pausado = false;
            return;
        }

        // Teclas para el sonido
        if (teclado.isKeyPressed(KeyEvent.VK_Q) && !teclaQ_presionada) {
            teclaQ_presionada = true;
            MenuConfigLR.efectosActivados = !MenuConfigLR.efectosActivados;
        } else if (!teclado.isKeyPressed(KeyEvent.VK_Q)) {
            teclaQ_presionada = false;
        }

        if (teclado.isKeyPressed(KeyEvent.VK_W) && !teclaW_presionada) {
            teclaW_presionada = true;
            MenuConfigLR.musicaActivada = !MenuConfigLR.musicaActivada;
            if (MenuConfigLR.musicaActivada && MenuConfigLR.sonidoGeneralActivado) {
                Musica.iniciarMusica(MenuConfigLR.pistaMusicalSeleccionada);
            } else {
                Musica.detenerMusicaFondo();
            }
        } else if (!teclado.isKeyPressed(KeyEvent.VK_W)) {
            teclaW_presionada = false;
        }


        // Atajo N para pasar al siguiente nivel, lo implementé para que sea mas facil trabajar los niveles
        if (teclado.isKeyPressed(KeyEvent.VK_N)) {
            vidasGuardadas = jugador != null ? jugador.getVidas() : 5;
            puntajeTotal  += jugador != null ? jugador.getPuntos() : 0;
            indiceNivel    = (indiceNivel + 1) % NIVELES.length;
            inicializarNivel();
            return;
        }


        if (juegoCompletado) {
            if (teclado.isKeyPressed(KeyEvent.VK_ENTER)) {
                indiceNivel  = 0;
                puntajeTotal = 0;
                juegoCompletado = false;
                inicializarNivel();
            }
            return;
        }

        // Game Over
        if (gameOver) {
            if (teclado.isKeyPressed(KeyEvent.VK_ENTER)) {
                indiceNivel  = 0;
                puntajeTotal = 0;
                inicializarNivel();
            }
            return;
        }

        // Victoria o Derrota 
        if (victoria || derrota) {
            if (teclado.isKeyPressed(KeyEvent.VK_ENTER)) {
                if (victoria) {
                    // Pasar al siguiente nivel
                    puntajeTotal  += jugador.getPuntos();
                    vidasGuardadas = jugador.getVidas();
                    indiceNivel++;
                    if (indiceNivel >= NIVELES.length) {
                        juegoCompletado = true;
                    } else {
                        inicializarNivel();
                    }
                } else {
                    // Reintentar mismo nivel con las vidas actuales
                    vidasGuardadas = jugador.getVidas();
                    inicializarNivel();
                }
            }
            return;
        }

        // Update normal 
        tiempoNivel += delta;
        ultimoDelta  = delta;

        jugador.procesarEntrada(teclado, delta, escala);
        mapa.update(delta);

        for (EnemigoLR e : enemigos) {
            e.update(delta);
        }

        for (EnemigoLR e : enemigos) {
            if (jugador.colisionaCon(e) && !jugador.estaPisandoCabeza(e) && !e.isEnHoyo()) {
                jugador.perderVida();
                if (jugador.getVidas() <= 0) { 
                    if (MenuConfigLR.efectosActivados && MenuConfigLR.sonidoGeneralActivado) Sonido.reproducir("gameOver.wav");
                    gameOver = true;
                } else { 
                    if (MenuConfigLR.efectosActivados && MenuConfigLR.sonidoGeneralActivado) Sonido.reproducir("LR_me_mataron.wav");
                    derrota = true;
                }
                return;
            }
            if (e.isEnHoyo()) jugador.sumarPuntos(200);
        }

        if (jugador.isEnHoyo()) {
            jugador.perderVida();
            if (jugador.getVidas() <= 0) { Sonido.reproducir("gameOver.wav"); gameOver = true; }
            else                          { Sonido.reproducir("LR_me_mataron.wav");     derrota  = true; }
            return;
        }

        if (!escaleraList && jugador.getOroRecolectado() >= mapa.getOroTotal()) {
            escaleraList = true;
            mapa.revelarEscaleraOculta(jugador.getX());
        }

        // Victoria:
        // necesita escaleraList=true (haber juntado todo el oro) para que la escalera no esté oculta.
        boolean condicionEscape = mapa.tieneEscaleraOculta()
                ? (escaleraList && mapa.jugadorEscapo(jugador.getY()))
                : mapa.jugadorEscapo(jugador.getY());

        if (condicionEscape) {
            int tiempoSobrante = (int) Math.max(0, TIEMPO_MAXIMO - tiempoNivel);
            jugador.sumarPuntos(500 + tiempoSobrante * 10);
            jugador.ganarVida();
            victoria = true;
        }
    }

    @Override
    public void gameDraw(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D gMapa = (Graphics2D) g2.create();
        gMapa.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                               RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        gMapa.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_OFF);
        gMapa.translate(offsetX, offsetY + HUD_ALTO);
        gMapa.scale(escala, escala);

        if (mapa     != null) mapa.dibujar(gMapa);
        if (jugador  != null) jugador.mostrar(gMapa);
        if (enemigos != null) for (EnemigoLR e : enemigos) e.mostrar(gMapa);
        if (jugador  != null) jugador.dibujarTextosPuntos(gMapa, ultimoDelta);

        gMapa.dispose();

        dibujarHUD(g2);

        if (pausado)
            dibujarMensajeCentral(g2, "— PAUSA —", new Color(100, 200, 255));
        else if (juegoCompletado)
            dibujarMensajeCentral(g2, "¡JUEGO COMPLETADO! — ENTER para reiniciar", new Color(255, 215, 0));
        else if (gameOver)
            dibujarMensajeCentral(g2, "GAME OVER — ENTER para reiniciar", Color.RED);
        else if (victoria)
            dibujarMensajeCentral(g2, "¡NIVEL SUPERADO! — ENTER para continuar", Color.GREEN);
        else if (derrota)
            dibujarMensajeCentral(g2, "¡ATRAPADO! Vidas: " + jugador.getVidas() + " — ENTER para reintentar", Color.ORANGE);
    }

    @Override
    public void gameShutdown() {
        Musica.detenerMusicaFondo();
    }

    //Inicialización de nivel

    private void inicializarNivel() {
        victoria     = false;
        derrota      = false;
        escaleraList = false;
        tiempoNivel  = 0;
        pausado      = false;
        Musica.detenerMusicaFondo();
        if (MenuConfigLR.musicaActivada && MenuConfigLR.sonidoGeneralActivado) {
            Musica.iniciarMusica(MenuConfigLR.pistaMusicalSeleccionada);
        }

        nivelActual  = NIVELES[indiceNivel];
        int[][] diseño = nivelActual.getDiseño();

        mapa = new MapaLR(diseño);

        int[] spawnJ = nivelActual.getSpawnJugador();
        jugador = new JugadorLR(
            MapaLR.TILE_SIZE * spawnJ[0],
            MapaLR.TILE_SIZE * spawnJ[1]
        );
        jugadorActual = jugador;
        jugador.setMapa(mapa);

        // Restaurar vidas del nivel anterior (se pierden al morir, no al cambiar nivel)
        while (jugador.getVidas() > vidasGuardadas) jugador.perderVida();
        while (jugador.getVidas() < vidasGuardadas) jugador.ganarVida();

        enemigos = new ArrayList<>();
        for (int[] s : nivelActual.getSpawnEnemigos()) {
            EnemigoLR e = new EnemigoLR(MapaLR.TILE_SIZE * s[0], MapaLR.TILE_SIZE * s[1]);
            e.setMapa(mapa);
            e.setObjetivo(jugador);
            enemigos.add(e);
        }
        jugador.setEnemigos(enemigos);
        
        calcularEscala(diseño);
    }

    private void calcularEscala(int[][] diseño) {
        int mapaW = diseño[0].length * MapaLR.TILE_SIZE;
        int mapaH = diseño.length    * MapaLR.TILE_SIZE;
        int venW  = getWidth();
        int venH  = getHeight() - HUD_ALTO;

        escala  = Math.min((double) venW / mapaW, (double) venH / mapaH);
        offsetX = (int)((venW - mapaW * escala) / 2);
        offsetY = (int)((venH - mapaH * escala) / 2);
    }

    // HUD, la barra de arriba que no anda :(

    private void dibujarHUD(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 20, 220));
        int venH  = getHeight() - (int)(HUD_ALTO * 1.5);

        g2.setFont(new Font("Arial", Font.BOLD, 14));

        // Vidas
        g2.setColor(Color.RED);
        g2.drawString("♥ x" + (jugador != null ? jugador.getVidas() : 0), 8, 20);

        // Número de nivel
        g2.setColor(new Color(100, 200, 255));
        String lvl = "NIVEL " + (nivelActual != null ? nivelActual.getNumero() : 1)
                   + " / " + NIVELES.length;
        g2.drawString(lvl, 70, 20);

        // Oro
        if (mapa != null && jugador != null) {
            g2.setColor(Color.YELLOW);
            String oro = "ORO: " + jugador.getOroRecolectado() + "/" + mapa.getOroTotal();
            int wOro = g2.getFontMetrics().stringWidth(oro);
            g2.drawString(oro, (getWidth() - wOro) / 2, 20);
        }

        // Tiempo
        int tRestante = (int) Math.max(0, TIEMPO_MAXIMO - tiempoNivel);
        g2.setColor(tRestante < 30 ? Color.RED : Color.LIGHT_GRAY);
        g2.drawString(String.format("%02d:%02d", tRestante / 60, tRestante % 60),
                      getWidth() - 160, 20);

        // Puntos
        g2.setColor(Color.WHITE);
        int pts = puntajeTotal + (jugador != null ? jugador.getPuntos() : 0);
        g2.drawString("PTS: " + pts, getWidth() - 90, 20);

        // Indicador de escape
        if (escaleraList && !victoria) {
            g2.setColor(Color.GREEN);
            int wS = g2.getFontMetrics().stringWidth("▲ ¡SUBE!");
            g2.drawString("▲ ¡SUBE!", (getWidth() - wS) / 2 + 80, 20);
        }
    }

    private void dibujarMensajeCentral(Graphics2D g2, String mensaje, Color color) {
        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, getHeight() / 2 - 45, getWidth(), 90);

        g2.setColor(color);
        g2.setFont(new Font("Arial", Font.BOLD, 26));
        int w = g2.getFontMetrics().stringWidth(mensaje);
        g2.drawString(mensaje, (getWidth() - w) / 2, getHeight() / 2 + 5);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        String sub = "ENTER para continuar  |  ESC para salir";
        int ws = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, (getWidth() - ws) / 2, getHeight() / 2 + 30);
    }
}
