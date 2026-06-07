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

    private boolean disparoJugador; 

    private String tipoProyectil;

    public Proyectil(double x, double y, boolean esJugador) {
        this(x, y, esJugador, "Original");
    }

    public Proyectil(double x, double y, boolean esJugador, String tipoProyectil) {
        super(); 
        this.posicionX = x;
        this.posicionY = y;
        this.disparoJugador = esJugador;
        this.velocidadY = esJugador ? -400 : 400; 
        this.activo = true;
        this.tipoProyectil = tipoProyectil != null ? tipoProyectil : "Original";
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

        // Si elegiste imagen de proyectil (combo "Laser"), dibujamos la imagen.
        if ("Laser".equalsIgnoreCase(tipoProyectil)) {
            java.awt.Image img = null;
            try {
                java.net.URL url = getClass().getResource("/AssetsSpace/proyectil.png");
                if (url != null) img = new javax.swing.ImageIcon(url).getImage();
            } catch (Exception ignored) {
            }

            if (img != null) {
                int w = ANCHO * 2;
                int h = ALTO * 2;
                g2.drawImage(img, (int) posicionX, (int) posicionY, w, h, null);
                return;
            }
            // Si no se pudo cargar la imagen, cae al render por color.
        }

        // Render fallback por color
        if (disparoJugador) {
            g2.setColor(Color.YELLOW); // Bala del jugador: Amarilla
            g2.fillRect((int) posicionX, (int) posicionY, ANCHO, ALTO);
        } else {
            g2.setColor(Color.RED); // Bala del enemigo: Roja
            g2.fillRect((int) posicionX - 1, (int) posicionY, 6, 8);
        }
    }


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
     
    public boolean colisionaCon(ObjetoGrafico otro) {
    if (otro == null) return false;

    return posicionX < otro.getX() + otro.getAncho() &&
           posicionX + getAncho() > otro.getX() &&
           posicionY < otro.getY() + otro.getAlto() &&
           posicionY + getAlto() > otro.getY();
    }
}