package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;

public class Tierra extends ObjetoGrafico {
    public Tierra(double x, double y) {
        this.posicionX = x;
        this.posicionY = y;
        // Más adelante pueden cargar una imagen de la superficie terrestre:
        // cargarImagen("SpaceInvaders/tierra.png");
    }

    @Override
    public void update(double delta) {
        // La tierra usualmente es estática, no necesita lógica de movimiento
    }
}