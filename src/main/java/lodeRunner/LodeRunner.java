package lodeRunner;

import com.entropyinteractive.JGame;
import com.entropyinteractive.Keyboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Juego Lode Runner – versión base funcional
 */
public class LodeRunner extends JGame {

    // ================== NIVEL ==================
    private static final int[][] NIVEL_1 = {
        {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
        {2,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
        {2,0,5,0,0,0,0,0,0,0,0,0,5,3,0,0,0,0,0,0,0,0,0,0,5,0,0,2},
        {2,1,1,1,1,3,0,0,0,0,1,1,1,3,3,1,0,0,0,1,1,1,1,1,1,3,0,2},
        {2,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,2},
        {2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2}
    };

    // ================== COMPONENTES ==================
    private MapaLR mapa;
    private JugadorLR jugador;
    private ArrayList<EnemigoLR> enemigos;

    // ================== ESTADO ==================
    private boolean gameOver = false;

    // ================== CONSTRUCTOR ==================
    public LodeRunner(String titulo, int ancho, int alto) {
        super(titulo, ancho, alto);
    }

    // ================== CICLO JGAME ==================
    @Override
    public void gameStartup() {
        inicializarNivel();
    }

    @Override
    public void gameUpdate(double delta) {
        Keyboard teclado = getKeyboard();

        if (teclado.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            stop();
            return;
        }

        if (gameOver) return;

        jugador.procesarEntrada(teclado, delta);
        jugador.update(delta);

        for (EnemigoLR e : enemigos) {
            e.update(delta);
        }
    }

    @Override
    public void gameDraw(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (mapa != null) mapa.dibujar(g2);
        if (jugador != null) jugador.mostrar(g2);
        if (enemigos != null) {
            for (EnemigoLR e : enemigos) {
                e.mostrar(g2);
            }
        }
    }

    @Override
    public void gameShutdown() {
        // limpieza si hiciera falta
    }

    // ================== INICIALIZACIÓN ==================
    private void inicializarNivel() {
        mapa = new MapaLR(NIVEL_1);
        jugador = new JugadorLR(64, 64);
        jugador.setMapa(mapa);

        enemigos = new ArrayList<>();
        EnemigoLR enemigo = new EnemigoLR(256, 64);
        enemigo.setMapa(mapa);
        enemigo.setObjetivo(jugador);
        enemigos.add(enemigo);

        gameOver = false;
    }
}


/*public class LodeRunner extends JGame {

    // ── Diseño del único nivel activo ────────────────────────────────────
    // Grilla 28 columnas × 16 filas, igual que el Lode Runner original
    // 0=Aire  1=Ladrillo  2=Piedra  3=Escalera  4=Barra  5=Oro  6=EscaleraOculta

    private static final int[][] NIVEL_1 = {
    //   0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27
        {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 6, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, // 0  piedra + esc.oculta col13
        {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2}, // 1  escalera de escape col13
        {2, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 2}, // 2  escalera + oro
        {2, 1, 1, 1, 1, 3, 0, 0, 0, 0, 1, 1, 1, 3, 3, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 3, 0, 2}, // 3  plataformas; col5,13,14,25=tope escalera
        {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 2}, // 4
        {2, 0, 0, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0, 0, 3, 0, 4, 4, 4, 4, 4, 4, 4, 0, 0, 3, 0, 2}, // 5  barras
        {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 2}, // 6
        {2, 0, 1, 1, 1, 1, 1, 3, 0, 0, 1, 1, 1, 1, 3, 0, 0, 3, 1, 1, 1, 1, 1, 0, 0, 3, 0, 2}, // 7  col7,14,17=tope escalera
        {2, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3, 0, 0, 0, 0, 5, 0, 0, 3, 0, 2}, // 8
        {2, 5, 0, 0, 4, 4, 4, 3, 4, 4, 4, 0, 0, 0, 3, 4, 4, 3, 4, 4, 0, 0, 1, 1, 0, 3, 5, 2}, // 9  barras + escaleras
        {2, 1, 1, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 3, 1, 2}, // 10
        {2, 0, 0, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, 3, 0, 0, 3, 0, 0, 5, 0, 0, 0, 0, 1, 1, 2}, // 11
        {2, 0, 1, 1, 1, 3, 0, 3, 1, 1, 1, 1, 1, 1, 3, 1, 0, 3, 1, 1, 1, 3, 0, 0, 0, 0, 0, 2}, // 12 col5,7,14,17,21=tope escalera
        {2, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 2}, // 13
        {2, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 5, 3, 0, 0, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 2}, // 14
        {2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2}, // 15 suelo
    };

    // Spawn del jugador y enemigos para el nivel 1
    private static final int[] SPAWN_JUGADOR   = {2, 14};   // col, fila
    private static final int[][] SPAWN_ENEMIGOS = {
        {22, 2}, {8, 4}
    };

    // ── Componentes ──────────────────────────────────────────────────────

    private MapaLR mapa;
    private JugadorLR jugador;
    private ArrayList<EnemigoLR> enemigos;


    // El constructor que llama tu LanzadorJuego

    // ── Escala y offset para ajustar el mapa a la ventana ────────────────
    private double escala  = 1.0;
    private int    offsetX = 0;
    private int    offsetY = 0;
    private static final int HUD_ALTO = 30;

    // ── Estado ───────────────────────────────────────────────────────────
    private boolean victoria     = false;
    private boolean derrota      = false;
    private boolean gameOver     = false;
    private boolean escaleraList = false;

    private double tiempoNivel  = 0;
    private static final double TIEMPO_MAXIMO = 180.0;

    private int puntajeTotal = 0;


    public LodeRunner(String titulo, int ancho, int alto) {
        super(titulo, ancho, alto);
    }

    /* 
    @Override
    public void gameStartup() {
        // Matriz de prueba para el Nivel 1: 
        // 0=Vacío, 1=Ladrillo, 2=Piedra, 3=Escalera, 4=Barra, 5=Oro
        int[][] nivelInicial = {
            {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
            {2,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,5,2},
            {2,0,0,5,0,0,0,3,0,0,0,5,0,0,0,0,3,0,0,0,0,0,1,1,2},
            {2,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,0,0,0,0,2},
            {2,0,0,0,0,0,0,3,0,0,0,0,0,0,3,0,0,0,0,1,1,0,0,0,2},
            {2,0,5,0,0,0,0,3,4,4,4,4,4,4,3,0,0,5,0,0,1,1,0,0,2},
            {2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2}
        };

        // Inicializamos los componentes usando los recursos que ya tenés
        mapa = new MapaLR(nivelInicial);
        
        // Posicionamos al jugador y a un enemigo de prueba (en píxeles, ej: x=64, y=64)
        jugador = new JugadorLR(64, 64);
        enemigos = new ArrayList<>();
        enemigos.add(new EnemigoLR(250, 64));
        */
    // ── Ciclo JGame ──────────────────────────────────────────────────────

   /*  @Override
    public void gameStartup() {
        inicializarNivel();

    }

    @Override
    public void gameUpdate(double delta) {
        Keyboard teclado = this.getKeyboard();

        /* 
        // Procesar las entradas del teclado para el jugador
        jugador.procesarEntrada(teclado, delta);
        jugador.update(delta);

        // Actualizar la lógica de los enemigos
        for (EnemigoLR enemigo : enemigos) {
            enemigo.update(delta);
        }

        // Si presionan ESCAPE, se cierra la ventana del juego y vuelve al lanzador
        if (teclado.isPressed(KeyEvent.VK_ESCAPE)) {
            stop();
        }*/

/* 
        if (teclado.isKeyPressed(KeyEvent.VK_ESCAPE)) { stop(); return; }

        if (gameOver) {
            if (teclado.isKeyPressed(KeyEvent.VK_ENTER)) {
                puntajeTotal = 0;
                inicializarNivel();
            }
            return;
        }

        if (victoria || derrota) {
            if (teclado.isKeyPressed(KeyEvent.VK_ENTER)) {
                if (victoria) {
                    // Por ahora solo hay 1 nivel: reiniciar
                    puntajeTotal += jugador.getPuntos();
                    inicializarNivel();
                } else {
                    int vidasRestantes = jugador.getVidas();
                    inicializarNivel();
                    while (jugador.getVidas() > vidasRestantes) jugador.perderVida();
                    while (jugador.getVidas() < vidasRestantes) jugador.ganarVida();
                }
            }
            return;
        }

        tiempoNivel += delta;

        jugador.procesarEntrada(teclado, delta, escala);
        mapa.update(delta);

        for (EnemigoLR e : enemigos) {
            e.update(delta);
            e.intentarRecogerOro(mapa);
        }

        for (EnemigoLR e : enemigos) {
            if (jugador.colisionaCon(e)) {
                if (!jugador.estaPisandoCabeza(e)) {
                    jugador.perderVida();
                    if (jugador.getVidas() <= 0) gameOver = true;
                    else                          derrota  = true;
                    return;
                }
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

        if (escaleraList && mapa.jugadorEscapo(jugador.getY())) {
            int tiempoSobrante = (int) Math.max(0, TIEMPO_MAXIMO - tiempoNivel);
            jugador.sumarPuntos(500 + tiempoSobrante * 10);
            jugador.ganarVida();
            victoria = true;
        }

    }

    @Override
    public void gameDraw(Graphics2D g2) {

        // Pintamos el fondo de negro para que resalten los bloques
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Dibujamos el mapa estático (Ladrillos, escaleras, oro...)
        if (mapa != null) {
            mapa.dibujar(g2);
        }

        // Dibujamos al personaje principal (usa el mostrar de ObjetoGrafico)
        if (jugador != null) {
            jugador.mostrar(g2);
        }

        // Dibujamos la lista de enemigos
        if (enemigos != null) {
            for (EnemigoLR enemigo : enemigos) {
                enemigo.mostrar(g2);
            }
        }
    }

    @Override
    public void gameShutdown() {
        // Código de limpieza si llegan a usar recursos del sistema pesados
    }


        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D gMapa = (Graphics2D) g2.create();
        gMapa.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                               RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        gMapa.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_OFF);
        gMapa.translate(offsetX, offsetY + HUD_ALTO);
        gMapa.scale(escala, escala);

        if (mapa    != null) mapa.dibujar(gMapa);
        if (jugador != null) jugador.mostrar(gMapa);
        if (enemigos != null) for (EnemigoLR e : enemigos) e.mostrar(gMapa);

        gMapa.dispose();

        dibujarHUD(g2);

        if (gameOver)      dibujarMensajeCentral(g2, "GAME OVER — ENTER para reiniciar", Color.RED);
        else if (victoria) dibujarMensajeCentral(g2, "¡NIVEL SUPERADO! ENTER para reiniciar", Color.GREEN);
        else if (derrota)  dibujarMensajeCentral(g2, "¡ATRAPADO!  Vidas: " + jugador.getVidas() + "  ENTER para reintentar", Color.ORANGE);
    }

    @Override
    public void gameShutdown() {}

    // ── Inicialización ───────────────────────────────────────────────────

    private void inicializarNivel() {
        victoria     = false;
        derrota      = false;
        escaleraList = false;
        tiempoNivel  = 0;

        mapa    = new MapaLR(NIVEL_1);
        jugador = new JugadorLR(
            MapaLR.TILE_SIZE * SPAWN_JUGADOR[0],
            MapaLR.TILE_SIZE * SPAWN_JUGADOR[1]
        );
        jugador.setMapa(mapa);

        enemigos = new ArrayList<>();
        for (int[] s : SPAWN_ENEMIGOS) {
            EnemigoLR e = new EnemigoLR(MapaLR.TILE_SIZE * s[0], MapaLR.TILE_SIZE * s[1]);
            e.setMapa(mapa);
            e.setObjetivo(jugador);
            enemigos.add(e);
        }

        calcularEscala(NIVEL_1);
    }

    private void calcularEscala(int[][] diseno) {
        int mapaPixelesAncho = diseno[0].length * MapaLR.TILE_SIZE;
        int mapaPixelesAlto  = diseno.length    * MapaLR.TILE_SIZE;
        int ventanaAncho     = getWidth();
        int ventanaAlto      = getHeight() - HUD_ALTO;

        double escalaX = (double) ventanaAncho / mapaPixelesAncho;
        double escalaY = (double) ventanaAlto  / mapaPixelesAlto;
        escala = Math.min(escalaX, escalaY);

        offsetX = (int)((ventanaAncho - mapaPixelesAncho * escala) / 2);
        offsetY = (int)((ventanaAlto  - mapaPixelesAlto  * escala) / 2);
    }

    // ── HUD ──────────────────────────────────────────────────────────────

    private void dibujarHUD(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 20, 220));
        g2.fillRect(0, 0, getWidth(), HUD_ALTO);

        g2.setFont(new Font("Arial", Font.BOLD, 14));

        g2.setColor(Color.RED);
        g2.drawString("♥ x" + jugador.getVidas(), 8, 20);

        g2.setColor(new Color(100, 200, 255));
        g2.drawString("NIVEL 1", 80, 20);

        g2.setColor(Color.YELLOW);
        String oro = "ORO: " + jugador.getOroRecolectado() + "/" + mapa.getOroTotal();
        int wOro = g2.getFontMetrics().stringWidth(oro);
        g2.drawString(oro, (getWidth() - wOro) / 2, 20);

        int tRestante = (int) Math.max(0, TIEMPO_MAXIMO - tiempoNivel);
        g2.setColor(tRestante < 30 ? Color.RED : Color.LIGHT_GRAY);
        g2.drawString(String.format("%02d:%02d", tRestante / 60, tRestante % 60),
                      getWidth() - 160, 20);

        g2.setColor(Color.WHITE);
        g2.drawString("PTS: " + (puntajeTotal + jugador.getPuntos()), getWidth() - 90, 20);

        if (escaleraList) {
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
 */