package pong;

import clasesCompartidas.ObjetoGrafico;

import java.awt.*;

import javax.swing.ImageIcon;

public class Cancha extends ObjetoGrafico {
    private Image imagenCancha;
    private String estilo = "Original";

    public Cancha() {
        // Posición 0,0 y tamaño igual a la ventana (o lo que quieras)
        this.posicionX = 0;
        this.posicionY = 0;
    }

    public void setEstilo(String estilo) {
        this.estilo = estilo;
        String ruta = "/pong/";

        switch (estilo) {
            case "Futbol":
                imagenCancha = new ImageIcon(getClass().getResource(ruta + "cancha-fondo.png")).getImage();
                break;
            case "Ciudad":
                imagenCancha = new ImageIcon(getClass().getResource(ruta + "ciudad-fondo.jpg")).getImage();
                break;
            case "Original":
            default:
                imagenCancha = null;
                break;
        }
    }
    public void mostrar(Graphics2D g2, int ancho, int alto)
    {
    if (imagenCancha != null) {
        g2.drawImage(imagenCancha, 0, 0, ancho, alto, null);
    } else {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, ancho, alto);
    }
    }

    @Override
    public void update(double delta) {}
}