package lodeRunner;

import clasesCompartidas.ObjetoGrafico;

public abstract class PersonajeLR extends ObjetoGrafico {
    protected double velocidad;

    public PersonajeLR(String rutaRecurso, double x, double y, double velocidad) {
        super(rutaRecurso);
        setPosicion(x, y);
        this.velocidad = velocidad;
    }

    public void moverIzquierda(double delta) { posicionX -= velocidad * delta; }
    public void moverDerecha(double delta) { posicionX += velocidad * delta; }
    public void moverArriba(double delta) { posicionY -= velocidad * delta; }
    public void moverAbajo(double delta) { posicionY += velocidad * delta; }
}