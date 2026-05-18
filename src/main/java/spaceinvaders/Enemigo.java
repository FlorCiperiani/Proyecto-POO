package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;

public class Enemigo extends ObjetoGrafico {
    private double velocidadX = 100;

    public Enemigo(double x, double y) {
        this.posicionX = x;
        this.posicionY = y;
    }

    @Override
    public void update(double delta) {
        // Movimiento básico horizontal
        this.posicionX += velocidadX * delta;
    }

    public void invertirDireccionYBajar() {
        velocidadX = -velocidadX;
        this.posicionY += 20; // Baja un escalón al tocar un borde
    }
}