package lodeRunner;

import clasesCompartidas.ObjetoGrafico;

<<<<<<< HEAD
public abstract class PersonajeLR extends ObjetoGrafico {
    protected double velocidad;

=======
/**
 * Clase base para todos los personajes del juego (jugador y enemigos).
 * Gestiona movimiento, gravedad y consultas de colisión al mapa.
 */
public abstract class PersonajeLR extends ObjetoGrafico {

    protected double velocidad;

    protected boolean enEscalera = false;
    protected boolean enBarra    = false;
    protected boolean enSuelo    = false;
    protected boolean cayendo    = false;
    protected boolean enHoyo     = false;

    protected static final double GRAVEDAD  = 300.0; // px/s²
    protected static final int    TILE_SIZE = MapaLR.TILE_SIZE;
    protected double velocidadY = 0;

    protected MapaLR mapa;

>>>>>>> Agregando-LodeRunner
    public PersonajeLR(String rutaRecurso, double x, double y, double velocidad) {
        super(rutaRecurso);
        setPosicion(x, y);
        this.velocidad = velocidad;
    }

<<<<<<< HEAD
    public void moverIzquierda(double delta) { posicionX -= velocidad * delta; }
    public void moverDerecha(double delta) { posicionX += velocidad * delta; }
    public void moverArriba(double delta) { posicionY -= velocidad * delta; }
    public void moverAbajo(double delta) { posicionY += velocidad * delta; }
}
=======
    public void setMapa(MapaLR mapa) { this.mapa = mapa; }

    // ── Movimientos básicos ──────────────────────────────────────────────

    public void moverIzquierda(double delta) {
        if (cayendo) return;
        double nuevaX = posicionX - velocidad * delta;
        if (!colisionaHorizontal(nuevaX, posicionY)) posicionX = nuevaX;
    }

    public void moverDerecha(double delta) {
        if (cayendo) return;
        double nuevaX = posicionX + velocidad * delta;
        if (!colisionaHorizontal(nuevaX, posicionY)) posicionX = nuevaX;
    }

    public void moverArriba(double delta) {
        if (!enEscalera) return;
        double nuevaY = posicionY - velocidad * delta;

        int col     = (int)(posicionX + getAncho() / 2.0) / TILE_SIZE;
        int filaCab = (int)(nuevaY) / TILE_SIZE;
        ElementoMapa tileArriba = (filaCab >= 0) ? mapa.getTileEn(col, filaCab) : null;

        if (esSolido(tileArriba)) {
            // Sólido arriba: bloquear, snap al borde superior del tile actual
            int filaActual = (int)(posicionY) / TILE_SIZE;
            double tope    = filaActual * (double) TILE_SIZE;
            if (nuevaY < tope) nuevaY = tope;
        } else {
            // Verificar si el TORSO salió del tile de escalera en la nueva posición.
            // Si es así, snap exacto: pies alineados con el borde superior del tope.
            // Esto evita que el personaje quede flotando más arriba de la plataforma.
            int filaCuerpoNueva = (int)(nuevaY + getAlto() / 2.0) / TILE_SIZE;
            boolean torsoEnEscalera = mapa.getTileEn(col, filaCuerpoNueva) instanceof Escalera;
            if (!torsoEnEscalera) {
                // El torso salió del tile de escalera: buscar el tope y hacer snap
                // posicionY snap = fila_tope * TILE - alto
                // El tope es la fila donde está el tile de escalera más alto que el personaje
                int filaTope = filaCuerpoNueva + 1; // la fila inmediatamente debajo del torso
                double snapY = filaTope * (double) TILE_SIZE - getAlto();
                if (nuevaY < snapY) nuevaY = snapY;
            }
        }

        posicionY = nuevaY;
    }

    public void moverAbajo(double delta) {
        if (!enEscalera && !enBarra) return;
        double nuevaY = posicionY + velocidad * delta;

        // Snap al suelo cuando los pies llegan a un tile sólido o salen de la escalera
        int col      = (int)(posicionX + getAncho() / 2.0) / TILE_SIZE;
        int filaPies = (int)(nuevaY + getAlto()) / TILE_SIZE;

        ElementoMapa debajo = mapa != null ? mapa.getTileEn(col, filaPies) : null;

        if (esSolido(debajo)) {
            // Snap: pies exactamente en el borde superior del tile sólido
            nuevaY   = filaPies * (double)TILE_SIZE - getAlto();
            enSuelo  = true;
            cayendo  = false;
            velocidadY = 0;
        }

        posicionY = nuevaY;
    }

    // ── Física: gravedad ────────────────────────────────────────────────

    public void aplicarGravedad(double delta) {
        if (enHoyo)                return;
        if (enEscalera || enBarra) { velocidadY = 0; cayendo = false; return; }

        if (!enSuelo) {
            cayendo     = true;
            velocidadY += GRAVEDAD * delta;
            double nuevaY = posicionY + velocidadY * delta;

            if (colisionaVertical(posicionX, nuevaY)) {
                // Snap preciso: pies en el borde exacto del tile sólido.
                int filaSolida = (int)(nuevaY + getAlto()) / TILE_SIZE;
                posicionY  = filaSolida * (double)TILE_SIZE - getAlto();
                velocidadY = 0;
                enSuelo    = true;
                cayendo    = false;
            } else {
                posicionY = nuevaY;
                enSuelo   = false;
            }
        } else {
            // Re-verificar que el suelo sigue siendo sólido DESPUÉS del movimiento
            // horizontal de este frame. Si el personaje se movió sobre un hueco o
            // un ladrillo recién roto, enSuelo era true al inicio pero ya no aplica.
            double piesPx   = posicionY + getAlto();
            int    filaBajo = (int) piesPx / TILE_SIZE;
            int    colIzqP  = (int) posicionX              / TILE_SIZE;
            int    colCen   = (int)(posicionX + getAncho() / 2.0) / TILE_SIZE;
            int    colDerP  = (int)(posicionX + getAncho() - 1)   / TILE_SIZE;

            boolean sueloReal = mapa != null && (
                    esSolido(mapa.getTileEn(colIzqP, filaBajo))
                 || esSolido(mapa.getTileEn(colCen,  filaBajo))
                 || esSolido(mapa.getTileEn(colDerP, filaBajo)));

            if (sueloReal) {
                // Snap exacto para eliminar residuos de punto flotante
                posicionY  = filaBajo * (double)TILE_SIZE - getAlto();
                cayendo    = false;
                velocidadY = 0;
            } else {
                // El suelo desapareció (ladrillo roto o borde de plataforma):
                // caer inmediatamente sin esperar al siguiente frame.
                enSuelo    = false;
                cayendo    = true;
                velocidadY = 0;
            }
        }
    }

    // ── Detección de estado según el mapa ───────────────────────────────

    public void actualizarEstado() {
        if (mapa == null) return;

        int col        = (int)(posicionX + getAncho() / 2.0) / TILE_SIZE;
        int filaCuerpo = (int)(posicionY + getAlto() / 2.0)  / TILE_SIZE;
        int filaCabeza = (int)(posicionY)                    / TILE_SIZE;

        // ── Detección de suelo estricta ──────────────────────────────────
        // Se comprueba si los pies están exactamente en el borde superior
        // de un tile sólido. El margen es de 1 px para absorber errores de
        // punto flotante sin dar falsos positivos.
        //
        // piesPx = posicionY + alto. Si piesPx == fila*TILE el personaje está
        // apoyado. Si piesPx está 1 px por encima también se considera suelo
        // para que la gravedad lo alinee correctamente en el mismo frame.
        // No usamos +2 px porque eso causaba que el personaje "flotara" sobre
        // huecos recién abiertos (ladrillo roto) durante varios frames.
        double piesPx   = posicionY + getAlto();
        int    filaBajo = (int) piesPx / TILE_SIZE;          // fila que contiene los pies
        double bordeInf = filaBajo * (double) TILE_SIZE;     // borde superior de ese tile

        // Los pies están "en contacto" si están dentro del primer pixel del tile
        boolean contacto = (piesPx - bordeInf) <= 1.0;

        // También verificar la columna izquierda y derecha del personaje
        int colIzqP = (int) posicionX             / TILE_SIZE;
        int colDerP = (int)(posicionX + getAncho() - 1) / TILE_SIZE;

        enSuelo = contacto && (esSolido(mapa.getTileEn(colIzqP, filaBajo))
                            || esSolido(mapa.getTileEn(col,      filaBajo))
                            || esSolido(mapa.getTileEn(colDerP,  filaBajo)));

        int filaPies = filaBajo;

        // En escalera: el TORSO del personaje está dentro de un tile de Escalera.
        // Usar solo filaCuerpo (centro del sprite) evita dos problemas:
        // 1. filaPies apuntaba al tile-tope cuando el personaje estaba parado
        //    encima de él → enEscalera=true al caminar por la plataforma → parpadeo.
        // 2. &&!enSuelo bloqueaba la entrada a la escalera desde la plataforma.
        // Con solo filaCuerpo: false cuando está en plataforma (torso en aire),
        // true cuando el torso cruza dentro del tile de escalera.
        enEscalera = (mapa.getTileEn(col, filaCuerpo) instanceof Escalera);

        // En barra: la CABEZA o el CUERPO del personaje están en un tile de Barra.
        // Al detectarla, snap inmediato: posicionY = fila_barra * TILE_SIZE.
        // Esto coloca las manos del sprite exactamente sobre la barra (rows 12-16
        // del sprite de barra coinciden con rows 11-15 del sprite del personaje).
        // El snap solo se aplica si el personaje acaba de entrar a la barra
        // (evita reposicionar cada frame mientras se mueve horizontalmente).
        boolean enBarraAnterior = enBarra;
        int filaConBarra = -1;
        if (mapa.getTileEn(col, filaCabeza) instanceof Barra) filaConBarra = filaCabeza;
        else if (mapa.getTileEn(col, filaCuerpo) instanceof Barra) filaConBarra = filaCuerpo;
        enBarra = (filaConBarra >= 0);

        if (enBarra && filaConBarra >= 0) {
            double snapY = filaConBarra * (double) TILE_SIZE;
            // Solo aplicar snap si la posición actual difiere del snap (entrada a barra
            // o posición incorrecta). Tolerancia de 1px para no interrumpir movimiento.
            if (Math.abs(posicionY - snapY) > 1.0) {
                posicionY  = snapY;
                velocidadY = 0;
            }
        }

        // Atrapado en hoyo
        enHoyo = estaEncerradoEnHoyo(col, filaCuerpo);
    }

    protected boolean estaEncerradoEnHoyo(int col, int fila) {
        ElementoMapa izq    = mapa.getTileEn(col - 1, fila);
        ElementoMapa der    = mapa.getTileEn(col + 1, fila);
        ElementoMapa debajo = mapa.getTileEn(col,     fila + 1);
        ElementoMapa actual = mapa.getTileEn(col,     fila);

        boolean lados = esSolido(izq) && esSolido(der);
        boolean suelo = esSolido(debajo);
        boolean hueco = !(actual instanceof Piedra);

        return lados && suelo && hueco;
    }

    // ── Colisiones ──────────────────────────────────────────────────────

    protected boolean colisionaHorizontal(double nx, double y) {
        if (mapa == null) return false;
        int colIzq  = (int) nx                   / TILE_SIZE;
        int colDer  = (int)(nx + getAncho() - 1)  / TILE_SIZE;
        int filaCab = (int) y                     / TILE_SIZE;
        int filaPie = (int)(y + getAlto() - 1)    / TILE_SIZE;

        for (int f = filaCab; f <= filaPie; f++) {
            if (esSolido(mapa.getTileEn(colIzq, f))) return true;
            if (esSolido(mapa.getTileEn(colDer, f))) return true;
        }
        return false;
    }

    protected boolean colisionaVertical(double x, double ny) {
        if (mapa == null) return false;
        int filaPie = (int)(ny + getAlto() - 1) / TILE_SIZE;
        int colIzq  = (int) x                   / TILE_SIZE;
        int colDer  = (int)(x + getAncho() - 1)  / TILE_SIZE;

        for (int c = colIzq; c <= colDer; c++) {
            if (esSolido(mapa.getTileEn(c, filaPie))) return true;
        }
        return false;
    }

    protected boolean esSolido(ElementoMapa t) {
        if (t instanceof Ladrillo) return !((Ladrillo) t).isRoto();
        if (t instanceof Piedra)   return true;
        return false;
    }

    // ── Colisión entre personajes (AABB) ─────────────────────────────────

    public boolean colisionaCon(PersonajeLR otro) {
        return posicionX < otro.posicionX + otro.getAncho()
            && posicionX + getAncho()  > otro.posicionX
            && posicionY < otro.posicionY + otro.getAlto()
            && posicionY + getAlto()   > otro.posicionY;
    }

    public boolean estaPisandoCabeza(PersonajeLR otro) {
        boolean solapaX = posicionX < otro.posicionX + otro.getAncho()
                       && posicionX + getAncho() > otro.posicionX;
        double piesPropios  = posicionY + getAlto();
        double cabezaOtro   = otro.posicionY;
        double tercioCuerpo = otro.posicionY + otro.getAlto() / 3.0;
        return solapaX && piesPropios >= cabezaOtro && piesPropios <= tercioCuerpo;
    }

    public boolean colisionaLateralOAbajo(PersonajeLR otro) {
        return colisionaCon(otro) && !estaPisandoCabeza(otro);
    }

    // ── Getters de estado ───────────────────────────────────────────────

    public boolean isEnHoyo()    { return enHoyo;     }
    public boolean isCayendo()   { return cayendo;    }
    public boolean isEnSuelo()   { return enSuelo;    }
    public boolean isEnEscalera(){ return enEscalera; }
    public double  getX()        { return posicionX;  }
    public double  getY()        { return posicionY;  }
}
>>>>>>> Agregando-LodeRunner
