package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;
import java.awt.Color;
import java.awt.Graphics2D;

public class Proyectil extends ObjetoGrafico {

    private double velocidadY;
    private boolean activo;

    // Tamaño del proyectil
    private static final int ANCHO = 4;
    private static final int ALTO = 10;

    private boolean disparoJugador; // Reemplaza o añade esta variable

    public Proyectil(double x, double y, boolean esJugador) {
        super(); 
        this.posicionX = x;
        this.posicionY = y;
        this.disparoJugador = esJugador;
        // Si es jugador va hacia arriba (-400), si es enemigo va hacia abajo (400)
        this.velocidadY = esJugador ? -400 : 400; 
        this.activo = true;
    }

    public boolean isDisparoJugador() {
    return disparoJugador;
    }

    @Override
    public void update(double delta) {
        posicionY += velocidadY * delta;
    }

   @Override
    public void mostrar(Graphics2D g2) {
        if (!activo) return;

        if (disparoJugador) {
            g2.setColor(Color.YELLOW); // Bala del jugador: Amarilla
            g2.fillRect((int) posicionX, (int) posicionY, ANCHO, ALTO);
        } else {
            g2.setColor(Color.RED); // Bala del enemigo: Roja
            // La hacemos un poquito diferente (ej: tipo misil cuadrado de 6x8)
            g2.fillRect((int) posicionX - 1, (int) posicionY, 6, 8); 
        }
    }

    // 👉 Importante para colisiones y límites
    @Override
    public int getAncho() {
        return ANCHO;
    }

    @Override
    public int getAlto() {
        return ALTO;
    }

    public boolean isActivo() {
        return activo;
    }

    public void desactivar() {
        activo = false;
    }
     
    // Método puente para recibir cualquier objeto gráfico (Enemigo, NaveNodriza, etc)
    public boolean colisionaCon(ObjetoGrafico otro) {
    if (otro == null) return false;

    return posicionX < otro.getX() + otro.getAncho() &&
           posicionX + getAncho() > otro.getX() &&
           posicionY < otro.getY() + otro.getAlto() &&
           posicionY + getAlto() > otro.getY();
}
}