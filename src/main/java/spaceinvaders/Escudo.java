package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;

public class Escudo extends ObjetoGrafico {
    private int salud = 3; // Los escudos suelen romperse tras un par de impactos

    public Escudo(double x, double y) {
        this.posicionX = x;
        this.posicionY = y;
    }

    @Override
    public void update(double delta) {
        // Estático, no requiere actualización de posición
    }

    public void recibirDanio() {
        this.salud--;
    }

    public boolean estaDestruido() {
        return this.salud <= 0;
    }
}