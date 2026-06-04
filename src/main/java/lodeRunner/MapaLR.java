package lodeRunner;

import java.awt.Graphics2D;

/**
 * Representa el mapa del nivel como una grilla de ElementoMapa.
 *
 * Convención del array de diseño:
 *   0 = Aire (vacío)
 *   1 = Ladrillo (rompible)
 *   2 = Piedra   (indestructible)
 *   3 = Escalera
 *   4 = Barra horizontal
 *   5 = Oro
 *   6 = Escalera oculta (aparece al recoger todo el oro)
 */
public class MapaLR {

    public static final int TILE_SIZE = 32;

    private final ElementoMapa[][] grilla;
    private final int[][] disenoOriginal;
    private final int filas;
    private final int columnas;
    private int oroTotal  = 0;

    private int colEscaleraOculta  = -1;
    private int filaEscaleraOculta = -1;
    private boolean escaleraVisible = false;

    public MapaLR(int[][] disenoNivel) {
        this.disenoOriginal = disenoNivel;
        filas    = disenoNivel.length;
        columnas = disenoNivel[0].length;
        grilla   = new ElementoMapa[filas][columnas];
        construirGrilla(disenoNivel);
        marcarTopesEscalera();
    }

    // ── Construcción ────────────────────────────────────────────────────

    private void construirGrilla(int[][] diseño) {
        oroTotal = 0;
        escaleraVisible = false;
        colEscaleraOculta  = -1;
        filaEscaleraOculta = -1;

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                double x = c * TILE_SIZE;
                double y = f * TILE_SIZE;
                switch (diseño[f][c]) {
                    case 1: grilla[f][c] = new Ladrillo(x, y); break;
                    case 2: grilla[f][c] = new Piedra(x, y);   break;
                    case 3: grilla[f][c] = new Escalera(x, y); break;
                    case 4: grilla[f][c] = new Barra(x, y);    break;
                    case 5:
                        grilla[f][c] = new Oro(x, y);
                        oroTotal++;
                        break;
                    case 6:
                        colEscaleraOculta  = c;
                        filaEscaleraOculta = f;
                        grilla[f][c] = null;
                        break;
                    default: grilla[f][c] = null; break;
                }
            }
        }
    }

    /**
     * Recorre cada columna y marca como "tope" (esTope=true) al tile de Escalera
     * más alto de cada segmento continuo vertical.
     * Esto se usa en Escalera.mostrar() para dibujar el remate sobresaliendo
     * levemente por encima del ladrillo adyacente, igual que en el original.
     */
    private void marcarTopesEscalera() {
        for (int c = 0; c < columnas; c++) {
            for (int f = 0; f < filas; f++) {
                if (grilla[f][c] instanceof Escalera) {
                    // ¿El tile de arriba también es escalera?
                    boolean hayEscaleraArriba = (f > 0) && (grilla[f-1][c] instanceof Escalera);
                    if (!hayEscaleraArriba) {
                        // Este es el tope del segmento
                        ((Escalera) grilla[f][c]).setEsTope(true);
                    }
                }
            }
        }
    }

    // ── Actualización ───────────────────────────────────────────────────

    public void update(double delta) {
        for (int f = 0; f < filas; f++)
            for (int c = 0; c < columnas; c++)
                if (grilla[f][c] != null) grilla[f][c].update(delta);
    }

    public void dibujar(Graphics2D g2) {
        for (int f = 0; f < filas; f++)
            for (int c = 0; c < columnas; c++)
                if (grilla[f][c] != null) grilla[f][c].mostrar(g2);
    }

    // ── Escalera oculta ─────────────────────────────────────────────────

    public void revelarEscaleraOculta(double jugadorX) {
        if (escaleraVisible) return;
        escaleraVisible = true;

        int col, filaOculta;
        if (colEscaleraOculta >= 0) {
            col       = colEscaleraOculta;
            filaOculta = filaEscaleraOculta;
        } else {
            col       = (int)(jugadorX + TILE_SIZE / 2.0) / TILE_SIZE;
            col       = Math.max(1, Math.min(col, columnas - 2));
            filaOculta = 0;
        }

        // Buscar la fila más baja desde filaOculta hacia abajo que ya tenga
        // una escalera continua que llegue hasta una plataforma pisable,
        // para saber hasta dónde hay que completar la columna.
        // En la práctica el tile 6 está en fila 0 y fila 1 suele tener ya
        // una escalera visible.  Lo que falta es:
        //   1. Colocar escalera en todas las filas desde filaOculta hacia abajo
        //      hasta la primera fila que ya tenga escalera (exclusive), para
        //      cerrar el hueco.
        //   2. Actualizar el flag esTope: solo la fila superior debe ser tope.

        // Paso 1a: extender hacia ARRIBA desde filaOculta hasta fila 0,
        // rellenando celdas null con Escalera para que moverArriba no haga
        // snap y bloquee al jugador antes de que pueda escapar por el techo.
        for (int f = filaOculta - 1; f >= 0; f--) {
            if (grilla[f][col] == null) {
                grilla[f][col] = new Escalera(col * TILE_SIZE, f * TILE_SIZE);
            } else {
                break; // Piedra u otro tile: no continuar
            }
        }

        // Paso 1b: colocar escalera en filaOculta y extender hacia ABAJO
        // hasta la primera Escalera ya existente o tile sólido.
        int filaInicio = filaOculta;
        int filaFin    = filaOculta;

        for (int f = filaOculta + 1; f < filas; f++) {
            if (grilla[f][col] instanceof Escalera) {
                break;
            }
            if (grilla[f][col] == null) {
                filaFin = f;
            } else {
                break;
            }
        }

        for (int f = filaInicio; f <= filaFin; f++) {
            grilla[f][col] = new Escalera(col * TILE_SIZE, f * TILE_SIZE);
        }

        // Paso 2: recalcular esTope en toda la columna para que solo el tile
        // más alto sea tope (puede haber escaleras preexistentes debajo).
        for (int f = 0; f < filas; f++) {
            if (grilla[f][col] instanceof Escalera) {
                boolean hayEscaleraArriba = (f > 0) && (grilla[f - 1][col] instanceof Escalera);
                ((Escalera) grilla[f][col]).setEsTope(!hayEscaleraArriba);
            }
        }
    }

    /** True si el diseño del nivel tiene un tile tipo 6 (escalera oculta). */
    public boolean tieneEscaleraOculta() { return colEscaleraOculta >= 0; }

    public boolean jugadorEscapo(double jugadorY) {
        // El personaje escapa cuando su posicionY (borde superior) llega a fila=0.
        // Usamos < TILE_SIZE en vez de <= 0 porque el snap de moverArriba
        // detiene al personaje exactamente en y=0 (borde del mapa) y nunca
        // llega a valores negativos.
        return jugadorY < TILE_SIZE;
    }

    // ── Acceso a tiles ──────────────────────────────────────────────────

    public ElementoMapa getTileEn(int col, int fila) {
        if (fila >= 0 && fila < filas && col >= 0 && col < columnas)
            return grilla[fila][col];
        return null;
    }

    public void setTileEn(int col, int fila, ElementoMapa tile) {
        if (fila >= 0 && fila < filas && col >= 0 && col < columnas)
            grilla[fila][col] = tile;
    }

    public void romperLadrillo(int col, int fila) {
        ElementoMapa tile = getTileEn(col, fila);
        if (tile instanceof Ladrillo) ((Ladrillo) tile).romper();
    }

    public int pixelAColumna(double x) { return (int) x / TILE_SIZE; }
    public int pixelAFila(double y)    { return (int) y / TILE_SIZE; }

    public int getFilas()    { return filas;    }
    public int getColumnas() { return columnas; }
    public int getOroTotal() { return oroTotal; }
}
