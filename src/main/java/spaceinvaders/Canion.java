package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;
import java.awt.Graphics2D; // <--- Descomentamos esta línea para poder dibujar

public class Canion extends ObjetoGrafico {
    private double velocidad = 300; // Píxeles por segundo
    
    // ── VARIABLES LOCALES PARA EL TAMAÑO RETRO DE LA NAVE ────────────────
    private int anchoPersonalizado;
    private int altoPersonalizado;

    public Canion(double x, double y) {
        this(x, y, "/AssetsSpace/player.png");
    }

    public Canion(double x, double y, String rutaImagen) {
        super(rutaImagen);
        this.posicionX = x;
        this.posicionY = y;
        
        // Asignamos un tamaño retro ideal para el jugador (por ejemplo, 50x40 píxeles)
        this.anchoPersonalizado = 50;
        this.altoPersonalizado = 40;
    }

    public void moverIzquierda(double delta) {
        this.posicionX -= velocidad * delta;
        if (this.posicionX < 0) this.posicionX = 0; 
    }

    public void moverDerecha(double delta, int anchoPantalla) {
        this.posicionX += velocidad * delta;
        // Reemplazamos el "50" fijo por getAncho() para que use el tamaño real de la nave
        if (this.posicionX > anchoPantalla - getAncho()) {
            this.posicionX = anchoPantalla - getAncho(); 
        }
    }

    @Override
    public void update(double delta) {
       
    }

    // ── REDEFINIMOS EL DIBUJO PARA FORZAR EL ESCALADO DEL JUGADOR ──────────
    @Override
    public void mostrar(Graphics2D g2) {
        if (this.imagen != null) {
            // Forzamos a que dibuje la nave con el ancho y alto que especificamos
            g2.drawImage(this.imagen, (int)this.posicionX, (int)this.posicionY, this.anchoPersonalizado, this.altoPersonalizado, null);
        }
    }

    // ── SOBREESCRIBIMOS LOS GETTERS DE LA CLASE MADRE ─────────────────────
    // Vital para que el límite derecho de la pantalla y los proyectiles enemigos funcionen perfecto
    @Override
    public int getAncho() {
        return this.anchoPersonalizado;
    }

    @Override
    public int getAlto() {
        return this.altoPersonalizado;
    }
}