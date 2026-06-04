package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;

public class Tierra extends ObjetoGrafico {
    public Tierra(double x, double y) {
        this.posicionX = x;
        this.posicionY = y;
    }

    @Override
    public void update(double delta) {
    }
}