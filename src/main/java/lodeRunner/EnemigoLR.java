package lodeRunner;

import java.util.Random;


public class EnemigoLR extends PersonajeLR {

    private JugadorLR objetivo;
    private static final Random RNG = new Random();

    // reaparecer
    private double tiempoAtrapado  = 0;
    private static final double TIEMPO_ESCAPAR = 8.0; // segundos

    public boolean isCaidoEnPozo() {
        return this.enHoyo;
    }

    // Comportamiento aleatorio
    private double tiempoDecision  = 0;
    private int dirRandomActual = 0;      // -1, 0, +1 (horizontal aleatoria cada tanto)
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

        // Atrapado en hoyo
        if (enHoyo) {
            //Fuerzo a que se caiga hasta el fondo del bloque porque se buggeaba
            int tamañoBloque = 32; 
            int filaActual = (int) ((posicionY + tamañoBloque / 2) / tamañoBloque);
            this.posicionY = filaActual * tamañoBloque;

            // Si llevaba oro lo suelta
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

        // Decisión aleatoria
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

        // Persecución o movimiento aleatorio
        if (objetivo != null) {
            double dx = objetivo.getX() - posicionX;
            double dy = objetivo.getY() - posicionY;

            int dirH;
            if (dirRandomActual != 0) {
                // Movimiento ilógico
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

        // Si lleva oro, mantiene el sprite del oro encima (Al final no implementé del todo esto, no me andaba bien)
        if (lleva_oro && oroTransportado != null) {
            oroTransportado.setPosicion(posicionX + getAncho() / 4.0, posicionY - 8);
        }
    }

    // Oro (Al final no implementé del todo esto, no me andaba bien)

    // Intenta juntar el oro del tile donde está parado. 
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
        // Devuelve el oro al mapa en la posición actual
        if (oroTransportado != null) {
            oroTransportado.devolver(posicionX, posicionY);
        }
        lleva_oro       = false;
        oroTransportado = null;
    }


    // Reaparición

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
        posicionX = TILE_SIZE;
        posicionY = TILE_SIZE;
        velocidadY = 0;
        enHoyo    = false;
    }

    public boolean llevaOro() { return lleva_oro; }
}
