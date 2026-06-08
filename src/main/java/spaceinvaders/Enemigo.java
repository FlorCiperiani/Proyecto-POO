package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;
import java.awt.Graphics2D;

public abstract class Enemigo extends ObjetoGrafico {
    protected double velocidadX;
    protected int puntos;
    
    // ── VARIABLES LOCALES PARA EL TAMAÑO RETRO ───────────────────────────
    protected int anchoPersonalizado;
    protected int altoPersonalizado;

    public Enemigo(double x, double y, String rutaImagen, int puntos, double velocidadInicial) {
        super(rutaImagen); 
        this.posicionX = x;
        this.posicionY = y;
        this.puntos = puntos;
        this.velocidadX = velocidadInicial;
        
        // Seteamos un tamaño estándar retro idéntico para todos los invasores
        this.anchoPersonalizado = 45; 
        this.altoPersonalizado = 35;  
    }

    @Override
    public void update(double delta) {
        this.posicionX += velocidadX * delta;
    }

    public void invertirDireccionYBajar() {
        velocidadX = -velocidadX;
        this.posicionY += 20; 
    }

    // ── REDEFINIMOS EL DIBUJO PARA FORZAR EL ESCALADO ────────────────────
    @Override
    public void mostrar(Graphics2D g2) {
        if (this.imagen != null) {
            // Se le pasan el ancho y el alto fijados para achicar cualquier foto gigante
            g2.drawImage(this.imagen, (int)this.posicionX, (int)this.posicionY, this.anchoPersonalizado, this.altoPersonalizado, null);
        }
    }

    // ── SOBREESCRIBIMOS LOS GETTERS DE LA CLASE MADRE ─────────────────────
    // Esto es vital para que SpaceInvaders calcule las colisiones con el tamaño real del sprite en pantalla
    @Override
    public int getAncho() {
        return this.anchoPersonalizado;
    }

    @Override
    public int getAlto() {
        return this.altoPersonalizado;
    }
    // ─────────────────────────────────────────────────────────────────────

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