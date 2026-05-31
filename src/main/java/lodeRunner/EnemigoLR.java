package lodeRunner;

import java.util.Random;

/**
 * Enemigo controlado por IA.
 *
 * Comportamiento (según consigna):
 *  - Persigue al jugador pero NO siempre toma el camino más corto.
 *  - A veces se mueve de forma aparentemente ilógica o se aleja.
 *  - Puede recoger lingotes de oro (uno a la vez); si cae en hoyo con oro, lo suelta.
 *  - Si queda atrapado y no escapa antes de que el hoyo se cierre, desaparece
 *    y reaparece en la parte superior en posición aleatoria.
 */
public class EnemigoLR extends PersonajeLR {

    private JugadorLR objetivo;
    private static final Random RNG = new Random();

    // Estado de hoyo/reaparecer
    private double tiempoAtrapado  = 0;
    private static final double TIEMPO_ESCAPAR = 8.0; // segundos

    // Comportamiento impredecible
    private double tiempoDecision  = 0;
    private int    dirRandomActual = 0;      // -1, 0, +1 (horizontal aleatoria ocasional)
    private static final double INTERVALO_DECISION = 2.0; // cada cuántos segundos recalcula

    // Oro transportado
    private boolean lleva_oro   = false;
    private Oro     oroTransportado = null;

    public EnemigoLR(double x, double y) {
        super("/lodeRunner/enemigo.png", x, y, 70.0);
    }

    public void setObjetivo(JugadorLR jugador) { this.objetivo = jugador; }

    @Override
    public void update(double delta) {
        actualizarEstado();

        // ── Atrapado en hoyo ─────────────────────────────────────────────
        if (enHoyo) {
            // Si llevaba oro, lo suelta en la celda actual
            if (lleva_oro && oroTransportado != null) {
                soltarOro();
            }
            tiempoAtrapado += delta;
            if (tiempoAtrapado >= TIEMPO_ESCAPAR) {
                tiempoAtrapado = 0;
                reaparecerArriba();
            }
            return;
        }

        tiempoAtrapado = 0;

        // ── Decisión aleatoria ocasional ─────────────────────────────────
        tiempoDecision += delta;
        if (tiempoDecision >= INTERVALO_DECISION) {
            tiempoDecision = 0;
            // 20% de probabilidad de moverse "de forma ilógica" por un rato
            int r = RNG.nextInt(10);
            if (r < 2) {
                dirRandomActual = RNG.nextBoolean() ? -1 : 1;  // dirección contraria
            } else {
                dirRandomActual = 0;  // modo normal: perseguir
            }
        }

        // ── Persecución o movimiento aleatorio ───────────────────────────
        if (objetivo != null) {
            double dx = objetivo.getX() - posicionX;
            double dy = objetivo.getY() - posicionY;

            int dirH;
            if (dirRandomActual != 0) {
                // Movimiento aparentemente ilógico
                dirH = dirRandomActual;
            } else {
                dirH = (dx < 0) ? -1 : (dx > 0 ? 1 : 0);
            }

            if (dirH < 0) moverIzquierda(delta);
            else if (dirH > 0) moverDerecha(delta);

            // Escalar hacia el jugador
            if (enEscalera && Math.abs(dy) > 4) {
                if (dy < 0) moverArriba(delta);
                else        moverAbajo(delta);
            }
        }

        aplicarGravedad(delta);

        // Si lleva oro, mantiene el sprite del oro encima (lógica simplificada)
        if (lleva_oro && oroTransportado != null) {
            oroTransportado.setPosicion(posicionX + getAncho() / 4.0, posicionY - 8);
        }
    }

    // ── Oro ──────────────────────────────────────────────────────────────

    /** Intenta recoger oro del tile donde está parado. */
    public void intentarRecogerOro(MapaLR mapa) {
        if (lleva_oro || mapa == null) return;
        int col  = (int)(posicionX + getAncho() / 2.0) / TILE_SIZE;
        int fila = (int)(posicionY + getAlto() / 2.0)  / TILE_SIZE;
        ElementoMapa tile = mapa.getTileEn(col, fila);
        if (tile instanceof Oro) {
            Oro oro = (Oro) tile;
            if (!oro.isRecolectado()) {
                oro.recolectar();   // lo saca del mapa
                lleva_oro        = true;
                oroTransportado  = oro;
            }
        }
    }

    private void soltarOro() {
        // Devuelve el oro al mapa en su posición actual (simplificado: lo deja visible)
        if (oroTransportado != null) {
            oroTransportado.devolver(posicionX, posicionY);
        }
        lleva_oro       = false;
        oroTransportado = null;
    }

    // ── Reaparición ──────────────────────────────────────────────────────

    private void reaparecerArriba() {
        if (mapa == null) return;
        // Busca una columna aleatoria libre en la fila 1 (parte superior)
        int intentos = 0;
        while (intentos < 50) {
            int col = RNG.nextInt(mapa.getColumnas() - 2) + 1;
            ElementoMapa tile = mapa.getTileEn(col, 1);
            if (tile == null) {  // aire libre
                posicionX  = col * TILE_SIZE;
                posicionY  = TILE_SIZE;          // fila 1
                velocidadY = 0;
                enHoyo     = false;
                return;
            }
            intentos++;
        }
        // Fallback: esquina superior izquierda libre
        posicionX = TILE_SIZE;
        posicionY = TILE_SIZE;
        velocidadY = 0;
        enHoyo    = false;
    }

    public boolean llevaOro() { return lleva_oro; }
}
