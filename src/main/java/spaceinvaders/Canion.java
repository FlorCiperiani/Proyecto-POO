package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;
//import java.awt.Graphics2D;

public class Canion extends ObjetoGrafico {
    private double velocidad = 300; // Píxeles por segundo

    public Canion(double x, double y) {
        this(x, y, "/AssetsSpace/player.png");
    }

    public Canion(double x, double y, String rutaImagen) {
        super(rutaImagen);
        this.posicionX = x;
        this.posicionY = y;
       
    }

    public void moverIzquierda(double delta) {
        this.posicionX -= velocidad * delta;
        if (this.posicionX < 0) this.posicionX = 0; 
    }

    public void moverDerecha(double delta, int anchoPantalla) {
        this.posicionX += velocidad * delta;
        if (this.posicionX > anchoPantalla - 50) this.posicionX = anchoPantalla - 50; 
    }

    @Override
    public void update(double delta) {
       
    }
}