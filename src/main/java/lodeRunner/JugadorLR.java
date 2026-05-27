package lodeRunner;

import com.entropyinteractive.Keyboard;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Jugador controlado por teclado.
 */
public class JugadorLR extends PersonajeLR {

    // ── Estado del jugador ───────────────────────────────────────────────
    private int oroRecolectado = 0;
    private int puntos         = 0;
    private int vidas          = 5;

    // ── Sprites ──────────────────────────────────────────────────────────
    private BufferedImage imgNeutral;
    private BufferedImage imgIzquierda;
    private BufferedImage imgDerecha;
    private BufferedImage imgCayendo;
    private BufferedImage imgEscaleraA;
    private BufferedImage imgEscaleraB;
    private BufferedImage imgBarraA;
    private BufferedImage imgBarraB;

    // ── Teclas ───────────────────────────────────────────────────────────
    private final int teclaIzq       = KeyEvent.VK_LEFT;
    private final int teclaDer       = KeyEvent.VK_RIGHT;
    private final int teclaArriba    = KeyEvent.VK_UP;
    private final int teclaAbajo     = KeyEvent.VK_DOWN;
    private final int teclaRomperIzq = KeyEvent.VK_Z;
    private final int teclaRomperDer = KeyEvent.VK_X;

    // ── Dirección / animación ────────────────────────────────────────────
    private int ultimaDireccion      = 0;   // -1 izq, 0 neutro, +1 der
    private int ultimaDireccionCavar = -1;

    private boolean teclaZAnterior = false;
    private boolean teclaXAnterior = false;

    private double tiempoAnim = 0;
    private int frameAnim     = 0;
    private int estadoAnim    = 0;
    private boolean moviendoVertical = false;

    private static final int ESTADO_SUELO    = 0;
    private static final int ESTADO_CAYENDO  = 1;
    private static final int ESTADO_ESCALERA = 2;
    private static final int ESTADO_BARRA    = 3;

    private static final double FPS_ESCALERA = 5.0;

    // ── Constructor ──────────────────────────────────────────────────────
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

        if (imgCayendo   == null) imgCayendo   = imgNeutral;
        if (imgEscaleraA == null) imgEscaleraA = imgNeutral;
        if (imgEscaleraB == null) imgEscaleraB = imgNeutral;
        if (imgBarraA    == null) imgBarraA    = imgNeutral;
        if (imgBarraB    == null) imgBarraB    = imgNeutral;
    }

    private BufferedImage cargarImagen(String ruta) {
        try {
            var is = getClass().getResourceAsStream(ruta);
            return (is != null) ? ImageIO.read(is) : null;
        } catch (Exception e) {
            return null;
        }
    }

    // ── Entrada principal ────────────────────────────────────────────────
    public void procesarEntrada(Keyboard teclado, double delta, double escala) {
        actualizarEstado();
        tiempoAnim += delta;

        boolean presIzq = teclado.isKeyPressed(teclaIzq);
        boolean presDer = teclado.isKeyPressed(teclaDer);

        if (presIzq && !presDer) {
            ultimaDireccion = -1;
            ultimaDireccionCavar = -1;
        } else if (presDer && !presIzq) {
            ultimaDireccion = 1;
            ultimaDireccionCavar = 1;
        } else {
            ultimaDireccion = 0;
        }

        moviendoVertical = false;

        if (!enHoyo) {
            if (presIzq) moverIzquierda(delta);
            if (presDer) moverDerecha(delta);
            if (teclado.isKeyPressed(teclaArriba)) { moverArriba(delta); moviendoVertical = true; }
            if (teclado.isKeyPressed(teclaAbajo))  { moverAbajo(delta);  moviendoVertical = true; }
        }

        boolean zActual = teclado.isKeyPressed(teclaRomperIzq);
        if (zActual && !teclaZAnterior) romperLadrillo(ultimaDireccionCavar);
        teclaZAnterior = zActual;

        boolean xActual = teclado.isKeyPressed(teclaRomperDer);
        if (xActual && !teclaXAnterior) romperLadrillo(-ultimaDireccionCavar);
        teclaXAnterior = xActual;

        aplicarGravedad(delta);
        recolectarOro();
        actualizarSprite();
    }

    // ── Lógica ───────────────────────────────────────────────────────────
    private void romperLadrillo(int direccion) {
        if (mapa == null || enBarra || !enSuelo) return;

        int colCentro   = (int)(posicionX + getAncho() / 2) / TILE_SIZE;
        int colObjetivo = colCentro + direccion;
        int filaSuelo   = (int)(posicionY + getAlto()) / TILE_SIZE;

        ElementoMapa tile = mapa.getTileEn(colObjetivo, filaSuelo);
        if (tile instanceof Ladrillo ladrillo && !ladrillo.isRoto()) {
            ladrillo.romper();
        }
    }

    private void recolectarOro() {
        if (mapa == null) return;

        int col  = (int)(posicionX + getAncho() / 2) / TILE_SIZE;
        int fila = (int)(posicionY + getAlto()  / 2) / TILE_SIZE;

        ElementoMapa tile = mapa.getTileEn(col, fila);
        if (tile instanceof Oro oro && !oro.isRecolectado()) {
            oro.recolectar();
            oroRecolectado++;
            puntos += 100;
        }
    }

    private void actualizarSprite() {
        int nuevoEstado;
        if      (cayendo)    nuevoEstado = ESTADO_CAYENDO;
        else if (enBarra)    nuevoEstado = ESTADO_BARRA;
        else if (enEscalera) nuevoEstado = ESTADO_ESCALERA;
        else                 nuevoEstado = ESTADO_SUELO;

        if (nuevoEstado != estadoAnim) {
            tiempoAnim = 0;
            frameAnim = 0;
            estadoAnim = nuevoEstado;
        }

        switch (nuevoEstado) {
            case ESTADO_CAYENDO -> imagen = imgCayendo;
            case ESTADO_ESCALERA -> {
                if (moviendoVertical) {
                    frameAnim = (int)(tiempoAnim * FPS_ESCALERA) % 2;
                }
                imagen = (frameAnim == 0) ? imgEscaleraA : imgEscaleraB;
            }
            case ESTADO_BARRA -> imagen = (ultimaDireccionCavar < 0) ? imgBarraA : imgBarraB;
            default -> {
                if (ultimaDireccion == -1) imagen = imgIzquierda;
                else if (ultimaDireccion == 1) imagen = imgDerecha;
                else imagen = imgNeutral;
            }
        }
    }

    // ── Getters ──────────────────────────────────────────────────────────
    public int getOroRecolectado() { return oroRecolectado; }
    public int getPuntos()         { return puntos; }
    public int getVidas()          { return vidas; }

    public void perderVida() { vidas--; }
    public void ganarVida()  { vidas++; }

    @Override
    public void update(double delta) {
        // gestionado desde procesarEntrada()
    }
}