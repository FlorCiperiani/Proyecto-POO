package pong;
import java.awt.*;

import clasesCompartidas.ObjetoGrafico;
import com.entropyinteractive.Keyboard;
import javax.swing.ImageIcon;

public class Paleta extends ObjetoGrafico{
    private int ancho;
    private int alto;
    private double velocidad=200;
    private Keyboard keyboard;
    private int teclaArriba;
    private int teclaAbajo;
    private Image imagenPaleta;
    private String estilo = "Original";


    public Paleta(int ancho, int alto, double xInicial, double yInicial,Keyboard keyboard, int teclaArriba, int teclaAbajo) {
        super(); // No usamos imagen
        this.ancho = ancho;
        this.alto = alto;
        this.posicionX = xInicial;
        this.posicionY = yInicial;
        this.velocidad =0; //inicia quieta
        this.keyboard=keyboard;
        //Seteo los movimientos de las paletas
        this.teclaArriba=teclaArriba;
        this.teclaAbajo=teclaAbajo;
    }


    public void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }

    @Override
    public void update(double delta) {
        // Controles para jugador izquierdo (W/S)

        // Limitar movimiento de la paleta dentro de la ventana
        if (posicionY < 37) {
            posicionY = 37;
        }
        if (posicionY + alto > 600) {
            posicionY = 600 - alto;
        }

        if (posicionX < 400) {
            if (keyboard.isKeyPressed(teclaArriba)) {
                setVelocidad(-400);
            } else if (keyboard.isKeyPressed(teclaAbajo)) {
                setVelocidad(400);
            } else {
                setVelocidad(0);
            }

        }

        // Controles para jugador derecho (Arriba/Abajo)
        if (posicionX > 400) {
            if (keyboard.isKeyPressed(teclaArriba)) {
                setVelocidad(-400);
            } else if (keyboard.isKeyPressed(teclaAbajo)) {
                setVelocidad(400);
            } else {
                setVelocidad(0);
            }
        }

        posicionY += velocidad * delta;
    }
    public void setEstilo(String estilo) {
        this.estilo = estilo;
        String ruta = "/pong/";

        switch (estilo) {
            case "Paleta azul":
                imagenPaleta = new ImageIcon(getClass().getResource(ruta + "01-Breakout-Tiles.png")).getImage();
                break;
            case "Paleta roja":
                imagenPaleta = new ImageIcon(getClass().getResource(ruta + "07-Breakout-Tiles.png")).getImage();
                break;
            case "Original":
            default:
                imagenPaleta = null;
                break;
        }
    }

     @Override
    public void mostrar(Graphics2D g2) {
        if (imagenPaleta != null) {
            g2.drawImage(imagenPaleta, (int) posicionX, (int) posicionY, ancho, alto, null);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRect((int) posicionX, (int) posicionY, ancho, alto);
        }
    }


    @Override
    public int getAncho() {
        return ancho;
    }

    @Override
    public int getAlto() {
        return alto;
    }

}
