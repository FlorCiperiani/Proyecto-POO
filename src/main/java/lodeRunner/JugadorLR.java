package lodeRunner;

import com.entropyinteractive.Keyboard;
import java.awt.event.KeyEvent;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Jugador controlado por teclado.
 *
 * Controles:
 *   ← →  : moverse (también cambia la pose del sprite)
 *   ↑ ↓  : trepar escalera / colgarse de barra
 *   Z    : romper ladrillo a la izquierda
 *   X    : romper ladrillo a la derecha
 *
 * Poses del jugador:
 *   - jugador_left.png    : presionando ←
 *   - jugador.png         : sin dirección (neutral / parado)
 *   - jugador_right.png   : presionando →
 *
 * Mecánica de excavación:
 *   - Solo se puede cavar a los lados (no directamente debajo).
 *   - Se cava el ladrillo del suelo adyacente.
 *   - Solo se puede cavar estando parado en el suelo.
 */
public class JugadorLR extends PersonajeLR {

    private int oroRecolectado = 0;
    private int puntos         = 0;
    private int vidas          = 5;

    // Sprites de las 3 poses de caminar
    private BufferedImage imgNeutral;
    private BufferedImage imgIzquierda;
    private BufferedImage imgDerecha;

    // Sprites de animación: caída
    private BufferedImage imgCayendo;

    // Sprites de animación: escalera (2 frames alternados)
    private BufferedImage imgEscaleraA;
    private BufferedImage imgEscaleraB;

    // Sprites de animación: barra (2 frames alternados)
    private BufferedImage imgBarraA;
    private BufferedImage imgBarraB;

    // Teclas configurables
    private int teclaIzq       = KeyEvent.VK_LEFT;
    private int teclaDer       = KeyEvent.VK_RIGHT;
    private int teclaArriba    = KeyEvent.VK_UP;
    private int teclaAbajo     = KeyEvent.VK_DOWN;
    private int teclaRomperIzq = KeyEvent.VK_Z;
    private int teclaRomperDer = KeyEvent.VK_X;

    // Última dirección no-cero para saber hacia dónde cavar
    private int ultimaDireccionCavar = -1; // arranca mirando a la izquierda como el original

    // Anti-repetición de teclas de excavación
    private boolean teclaZAnterior = false;
    private boolean teclaXAnterior = false;

    // Última dirección horizontal para mantener la pose cuando se suelta la tecla
    private int ultimaDireccion = 0; // -1 izq, 0 neutral, +1 der

    // Animación: tiempo acumulado, frame actual y estado previo
    private double tiempoAnim   = 0.0;
    private int    frameAnim    = 0;
    private int    estadoAnim   = 0;   // 0=suelo 1=cayendo 2=escalera 3=barra
    private boolean moviendoVertical = false;

    private static final double FPS_ESCALERA = 5.0;
    private static final double FPS_BARRA    = 6.0;

    public JugadorLR(double x, double y) {
        super("/lodeRunner/jugador.png", x, y, 120.0);
        imgNeutral   = imagen;
        imgIzquierda = cargarImagen("/lodeRunner/jugador_left.png");
        imgDerecha   = cargarImagen("/lodeRunner/jugador_right.png");
        imgCayendo   = cargarImagen("/lodeRunner/jugador_cayendo.png");
        imgEscaleraA = cargarImagen("/lodeRunner/jugador_escalera_a.png");
        imgEscaleraB = cargarImagen("/lodeRunner/jugador_escalera_b.png");
        imgBarraA    = cargarImagen("/lodeRunner/jugador_barra_a.png");
        imgBarraB    = cargarImagen("/lodeRunner/jugador_barra_b.png");

        // Fallbacks: si algún sprite no carga, usar neutral
        if (imgCayendo   == null) imgCayendo   = imgNeutral;
        if (imgEscaleraA == null) imgEscaleraA = imgNeutral;
        if (imgEscaleraB == null) imgEscaleraB = imgNeutral;
        if (imgBarraA    == null) imgBarraA    = imgNeutral;
        if (imgBarraB    == null) imgBarraB    = imgNeutral;
    }

    private BufferedImage cargarImagen(String ruta) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(ruta);
            return (is != null) ? ImageIO.read(is) : null;
        } catch (java.io.IOException e) { return null; }
    }

    // ── Entrada principal ────────────────────────────────────────────────

    public void procesarEntrada(Keyboard teclado, double delta, double escala) {
        actualizarEstado();
        tiempoAnim += delta;

        boolean presIzq = teclado.isKeyPressed(teclaIzq);
        boolean presDer = teclado.isKeyPressed(teclaDer);

        // Actualizar última dirección para la pose y para cavar
        if (presIzq && !presDer) {
            ultimaDireccion      = -1;
            ultimaDireccionCavar = -1;
        } else if (presDer && !presIzq) {
            ultimaDireccion      = +1;
            ultimaDireccionCavar = +1;
        } else if (!presIzq && !presDer) {
            ultimaDireccion = 0;
            // ultimaDireccionCavar mantiene el último valor no-cero
        }

        // Movimiento (bloqueado durante caída libre y si está en hoyo)
        moviendoVertical = false;
        if (!enHoyo) {
            if (presIzq) moverIzquierda(delta);
            if (presDer) moverDerecha(delta);
            if (teclado.isKeyPressed(teclaArriba)) { moverArriba(delta); moviendoVertical = true; }
            if (teclado.isKeyPressed(teclaAbajo))  { moverAbajo(delta);  moviendoVertical = true; }
        }

        // Excavación: UNA SOLA TECLA (Z).
        // La dirección del agujero depende del último botón direccional presionado:
        //   → último fue RIGHT  => agujero a la DERECHA
        //   → último fue LEFT   => agujero a la IZQUIERDA
        boolean zActual = teclado.isKeyPressed(teclaRomperIzq);
        if (zActual && !teclaZAnterior) romperLadrillo(ultimaDireccionCavar);
        teclaZAnterior = zActual;

        // X también cava pero en la dirección CONTRARIA (opcional, compatible NES)
        boolean xActual = teclado.isKeyPressed(teclaRomperDer);
        if (xActual && !teclaXAnterior) romperLadrillo(-ultimaDireccionCavar);
        teclaXAnterior = xActual;

        aplicarGravedad(delta);
        recolectarOro();
        actualizarSprite();
    }

    // ── Excavación ───────────────────────────────────────────────────────

    /**
     * Cava el ladrillo del suelo en la dirección indicada.
     *   direccion = -1 → cava a la izquierda
     *   direccion = +1 → cava a la derecha
     *
     * La columna objetivo es la columna DEL CENTRO del personaje ±1,
     * lo que siempre apunta al tile directamente adyacente.
     */
    private void romperLadrillo(int direccion) {
        if (mapa == null) return;
        if (enBarra)      return;
        if (!enSuelo)     return;

        // Columna del centro del personaje
        int colCentro  = (int)(posicionX + getAncho() / 2.0) / TILE_SIZE;
        int colObjetivo = colCentro + direccion;

        // Fila del suelo: donde están los pies
        int filaSuelo = (int)(posicionY + getAlto()) / TILE_SIZE;

        ElementoMapa tile = mapa.getTileEn(colObjetivo, filaSuelo);
        if (tile instanceof Ladrillo) {
            Ladrillo ladrillo = (Ladrillo) tile;
            if (!ladrillo.isRoto()) ladrillo.romper();
        }
    }

    // ── Recolección de oro ───────────────────────────────────────────────

    private void recolectarOro() {
        if (mapa == null) return;
        int col  = (int)(posicionX + getAncho() / 2.0) / TILE_SIZE;
        int fila = (int)(posicionY + getAlto() / 2.0)  / TILE_SIZE;

        ElementoMapa tile = mapa.getTileEn(col, fila);
        if (tile instanceof Oro) {
            Oro oro = (Oro) tile;
            if (!oro.isRecolectado()) {
                oro.recolectar();
                oroRecolectado++;
                puntos += 100;
            }
        }
    }

    // ── Sprite según estado con animación ────────────────────────────────

    private static final int ESTADO_SUELO    = 0;
    private static final int ESTADO_CAYENDO  = 1;
    private static final int ESTADO_ESCALERA = 2;
    private static final int ESTADO_BARRA    = 3;

    private void actualizarSprite() {
        // Determinar el estado actual con prioridad clara.
        // ESCALERA: activar siempre que enEscalera=true, sin importar moviendoVertical.
        // El avance de frames se controla por separado dentro del case.
        int nuevoEstado;
        if      (cayendo)    nuevoEstado = ESTADO_CAYENDO;
        else if (enBarra)    nuevoEstado = ESTADO_BARRA;
        else if (enEscalera) nuevoEstado = ESTADO_ESCALERA;
        else                 nuevoEstado = ESTADO_SUELO;

        // Al cambiar de estado: resetear timer y frame para que la animación
        // empiece limpia desde el principio, sin arrastrar tiempo acumulado.
        if (nuevoEstado != estadoAnim) {
            tiempoAnim = 0.0;
            frameAnim  = 0;
            estadoAnim = nuevoEstado;
        }

        switch (nuevoEstado) {

            case ESTADO_CAYENDO:
                imagen = imgCayendo;
                break;

            case ESTADO_ESCALERA: {
                // Avanzar la animación SOLO si el jugador se mueve verticalmente.
                // Si está parado en la escalera (sin presionar ↑↓), congelar el frame.
                // Esto evita el reseteo continuo de tiempoAnim que causaba el parpadeo.
                if (moviendoVertical) {
                    double periodo = 1.0 / FPS_ESCALERA;
                    tiempoAnim = tiempoAnim % periodo;
                    frameAnim  = (int)(tiempoAnim / (periodo / 2)) % 2;
                }
                imagen = (frameAnim == 0) ? imgEscaleraA : imgEscaleraB;
                break;
            }

            case ESTADO_BARRA:
                // A = mirando izquierda (último botón fue ←)
                // B = mirando derecha   (último botón fue →)
                imagen = (ultimaDireccionCavar < 0) ? imgBarraA : imgBarraB;
                break;

            default: // ESTADO_SUELO
                // Sin animación: pose estática según dirección
                if (ultimaDireccion == -1 && imgIzquierda != null) {
                    imagen = imgIzquierda;
                } else if (ultimaDireccion == +1 && imgDerecha != null) {
                    imagen = imgDerecha;
                } else {
                    imagen = imgNeutral;
                }
                break;
        }
    }

    // ── Vidas ────────────────────────────────────────────────────────────

    public void perderVida() { vidas--; }
    public void ganarVida()  { vidas++; }
    public int  getVidas()   { return vidas; }

    // ── Puntos ───────────────────────────────────────────────────────────

    public void sumarPuntos(int cantidad) { puntos += cantidad; }
    public int  getPuntos()               { return puntos; }

    // ── Getters ──────────────────────────────────────────────────────────

    public int getOroRecolectado() { return oroRecolectado; }
    public void resetOro() { oroRecolectado = 0; }

    @Override
    public void update(double delta) { /* gestionado por procesarEntrada() */ }
}
