package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;

public abstract class Enemigo extends ObjetoGrafico {
    protected double velocidadX;
    protected int puntos;

    public Enemigo(double x, double y, String rutaImagen, int puntos, double velocidadInicial) {
        // Llama al constructor de ObjetoGrafico(String filename) que nos mostraste antes
        super(rutaImagen); 
        this.posicionX = x;
        this.posicionY = y;
        this.puntos = puntos;
        this.velocidadX = velocidadInicial;
    }

    @Override
    public void update(double delta) {
        // Movimiento básico horizontal
        this.posicionX += velocidadX * delta;
    }

    public void invertirDireccionYBajar() {
        velocidadX = -velocidadX;
        this.posicionY += 20; 
    }

    public int getPuntos() {
        return this.puntos;
    }
    
    public void setVelocidadX(double nuevaVelocidad) {
        this.velocidadX = nuevaVelocidad;
    }
    
    public double getVelocidadX() {
        return this.velocidadX;
    }
}