package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Galaxia extends ObjetoGrafico {
    private String estilo = "Original";

    public Galaxia() {
        // Cargamos la imagen por defecto en la variable 'imagen' heredada de ObjetoGrafico
        super("/AssetsSpace/Galaxia.png");
        this.posicionX = 0;
        this.posicionY = 0;
    }

    public void setEstilo(String estilo) {
        this.estilo = estilo;
        String ruta = "/AssetsSpace/";

        /*try {
            switch (estilo) {
                case "Futbol":
                    // Reutilizamos setImagen() heredado de ObjetoGrafico
                    setImagen(ImageIO.read(getClass().getResourceAsStream(ruta + "cancha-fondo.png")));
                    break;
                case "Ciudad":
                    setImagen(ImageIO.read(getClass().getResourceAsStream(ruta + "ciudad-fondo.jpg")));
                    break;
                case "Original":
                default:
                    setImagen(ImageIO.read(getClass().getResourceAsStream(ruta + "Galaxia.png")));
                    break;
            }
        } catch (IOException | NullPointerException e) {
            System.out.println("No se pudo cargar el fondo de estilo: " + estilo + ". Se usará el fondo negro.");
            setImagen(null); // Si falla en encontrar la imagen de la carpeta, la deja en null
        }*/
    }

    // Cambiamos el comportamiento de mostrar para que use la variable 'imagen' del padre
    public void mostrar(Graphics2D g2, int ancho, int alto) {
        // getImagen() viene de ObjetoGrafico
        if (getImagen() != null) {
            // Dibuja estirando la imagen al tamaño total de la ventana
            g2.drawImage(getImagen(), 0, 0, ancho, alto, null);
        } else {
            // Resguardo por si falla la carga del archivo
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, ancho, alto);
        }
    }

    @Override
    public void update(double delta) {}
}