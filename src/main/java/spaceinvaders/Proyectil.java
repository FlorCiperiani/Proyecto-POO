package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;

public class Proyectil extends ObjetoGrafico {
    private double velocidadY;
    private boolean activo;

    public Proyectil(double x, double y, boolean esJugador) {
        this.posicionX = x;
        this.posicionY = y;
        this.velocidadY = esJugador ? -400 : 400; // Sube si es del jugador, baja si es enemigo
        this.activo = true;
    }

    @Override
    public void update(double delta) {
        this.posicionY += velocidadY * delta;
    }

    public boolean isActivo() { return activo; }
    public void desactivar() { this.activo = false; }
}