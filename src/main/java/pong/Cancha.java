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

        String est = (estilo == null) ? "" : estilo.trim();
        est = est.toLowerCase();

        switch (est) {
            case "cancha 1":
            case "cancha1":
                imagenCancha = new ImageIcon(getClass().getResource(ruta + "cancha1.png")).getImage();
                break;
            case "cancha 2":
            case "cancha2":
                imagenCancha = new ImageIcon(getClass().getResource(ruta + "cancha2.png")).getImage();
                break;
            case "original":
                imagenCancha = new ImageIcon(getClass().getResource(ruta + "original.png")).getImage();
                break;
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