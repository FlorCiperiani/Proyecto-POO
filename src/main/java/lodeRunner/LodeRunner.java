package lodeRunner;

import com.entropyinteractive.JGame;
import com.entropyinteractive.Keyboard;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Clase principal del juego Lode Runner.
 *
 * Gestiona el ciclo de juego, el HUD y la progresión entre niveles.
 * El diseño de cada nivel está en su propia clase (NivelLR1, NivelLR2, NivelLR3...).
 * Para agregar un nivel: crear NivelLRN extends NivelLR y añadirlo al array NIVELES.
 */
public class LodeRunner extends JGame {

    // ── Niveles disponibles (agregar aquí para expandir) ─────────────────
    private static final NivelLR[] NIVELES = {
        new NivelLR1(),
        new NivelLR2(),
        new NivelLR3(),
    };

    // ── Componentes ──────────────────────────────────────────────────────
    private MapaLR       mapa;
    private JugadorLR    jugador;
    private ArrayList<EnemigoLR> enemigos;
    private NivelLR      nivelActual;
    private int          indiceNivel = 0;

    // ── Escala y offset ──────────────────────────────────────────────────
    private double escala  = 1.0;
    private int    offsetX = 0;
    private int    offsetY = 0;
    private static final int HUD_ALTO = 30;

    // ── Estado de juego ──────────────────────────────────────────────────
    private boolean victoria     = false;
    private boolean derrota      = false;
    private boolean gameOver     = false;
    private boolean escaleraList = false;
    private boolean juegoCompletado = false;

    private double tiempoNivel  = 0;
    private double ultimoDelta  = 0;   // para pasar a dibujarTextosPuntos
    private static final double TIEMPO_MAXIMO = 180.0;

    private int puntajeTotal = 0;
    private int vidasGuardadas = 5;   // vidas que se llevan al siguiente nivel

    public LodeRunner(String titulo, int ancho, int alto) {
        super(titulo, ancho, alto);
    }

    // ── Ciclo JGame ──────────────────────────────────────────────────────

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

        // ── Atajo de desarrollo: N → siguiente nivel ──────────────────────
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

        // ── Game Over ─────────────────────────────────────────────────────
        if (gameOver) {
            if (teclado.isKeyPressed(KeyEvent.VK_ENTER)) {
                indiceNivel  = 0;
                puntajeTotal = 0;
                inicializarNivel();
            }
            return;
        }

        // ── Victoria / Derrota ────────────────────────────────────────────
        if (victoria || derrota) {
            if (teclado.isKeyPressed(KeyEvent.VK_ENTER)) {
                if (victoria) {
                    // Avanzar al siguiente nivel
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

        // ── Update normal ─────────────────────────────────────────────────
        tiempoNivel += delta;
        ultimoDelta  = delta;

        jugador.procesarEntrada(teclado, delta, escala);
        mapa.update(delta);

        for (EnemigoLR e : enemigos) {
            e.update(delta);
            e.intentarRecogerOro(mapa);
        }

        for (EnemigoLR e : enemigos) {
            if (jugador.colisionaCon(e) && !jugador.estaPisandoCabeza(e)) {
                jugador.perderVida();
                if (jugador.getVidas() <= 0) gameOver = true;
                else                          derrota  = true;
                return;
            }
            if (e.isEnHoyo()) jugador.sumarPuntos(200);
        }

        if (jugador.isEnHoyo()) {
            jugador.perderVida();
            if (jugador.getVidas() <= 0) gameOver = true;
            else                          derrota  = true;
            return;
        }

        if (!escaleraList && jugador.getOroRecolectado() >= mapa.getOroTotal()) {
            escaleraList = true;
            mapa.revelarEscaleraOculta(jugador.getX());
        }

        // Victoria:
        // - Si el nivel tiene escalera oculta (tile 6): necesita escaleraList=true
        //   (haber recogido todo el oro) para que la escalera esté revelada.
        // - Si no tiene escalera oculta (escalera visible desde el inicio):
        //   basta con llegar al extremo superior (posicionY < TILE_SIZE).
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

        if (juegoCompletado)
            dibujarMensajeCentral(g2, "¡JUEGO COMPLETADO! — ENTER para reiniciar", new Color(255, 215, 0));
        else if (gameOver)
            dibujarMensajeCentral(g2, "GAME OVER — ENTER para reiniciar", Color.RED);
        else if (victoria)
            dibujarMensajeCentral(g2, "¡NIVEL SUPERADO! — ENTER para continuar", Color.GREEN);
        else if (derrota)
            dibujarMensajeCentral(g2, "¡ATRAPADO! Vidas: " + jugador.getVidas() + " — ENTER para reintentar", Color.ORANGE);
    }

    @Override
    public void gameShutdown() {}

    // ── Inicialización de nivel ───────────────────────────────────────────

    private void inicializarNivel() {
        victoria     = false;
        derrota      = false;
        escaleraList = false;
        tiempoNivel  = 0;

        nivelActual  = NIVELES[indiceNivel];
        int[][] diseño = nivelActual.getDiseño();

        mapa = new MapaLR(diseño);

        int[] spawnJ = nivelActual.getSpawnJugador();
        jugador = new JugadorLR(
            MapaLR.TILE_SIZE * spawnJ[0],
            MapaLR.TILE_SIZE * spawnJ[1]
        );
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

    // ── HUD ──────────────────────────────────────────────────────────────

    private void dibujarHUD(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 20, 220));
        g2.fillRect(0, 0, getWidth(), HUD_ALTO);

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
