package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Galaxia extends ObjetoGrafico {

    private BufferedImage imagenActual;

    public Galaxia() {
        cargarImagen("/AssetsSpace/Galaxia.png");
    }

    public void setEstilo(String estilo) {
        switch (estilo) {
            case "Oceano":
                cargarImagen("/AssetsSpace/Oceano.png");
                break;
            case "Ciudad":
                cargarImagen("/AssetsSpace/Ciudad.png");
                break;
            default:
                cargarImagen("/AssetsSpace/Galaxia.png");
                break;
        }
    }

    private void cargarImagen(String ruta) {
        try {
            imagenActual = ImageIO.read(getClass().getResourceAsStream(ruta));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("No se pudo cargar el fondo: " + ruta);
            imagenActual = null;
        }
    }

    public void mostrar(Graphics2D g2, int ancho, int alto) {
        if (imagenActual != null) {
            g2.drawImage(imagenActual, 0, 0, ancho, alto, null);
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, ancho, alto);
        }
    }

    @Override
    public void update(double delta) {}
}