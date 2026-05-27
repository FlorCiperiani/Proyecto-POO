package pong;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.*;

import javax.swing.ImageIcon;

import clasesCompartidas.ObjetoGrafico;

class Pelota extends ObjetoGrafico {
    private int tamanio;
    private double velocidadX;
    private double velocidadY;
    private String estilo = "Original";
    private Image imagenPelota;

    public Pelota(int tamanio, double xInicial, double yInicial, double velocidadX, double velocidadY) {
        super(); // No hay imagen, solo es un cuadrado blanco
        this.tamanio = tamanio;
        this.posicionX = xInicial;
        this.posicionY = yInicial;
        this.velocidadX = velocidadX;
        this.velocidadY = velocidadY;
    }

    @Override
    public void update(double delta) {
        posicionX += velocidadX * delta;
        posicionY += velocidadY * delta;
    }
    public void setEstilo(String estilo) {
        this.estilo = estilo;
        String ruta = "/pong/";

        switch (estilo) {
<<<<<<< HEAD
            case "Planeta":
                imagenPelota = new ImageIcon(getClass().getResource(ruta + "planeta-tierra.png")).getImage();
                break;
            case "Disco":
                imagenPelota = new ImageIcon(getClass().getResource(ruta + "bola-de-disco.png")).getImage();
=======
            case "Futbol":
                imagenPelota = new ImageIcon(getClass().getResource(ruta + "SoccerBall.png")).getImage();
                break;
            case "Basquet":
                imagenPelota = new ImageIcon(getClass().getResource(ruta + "Basketball.png")).getImage();
>>>>>>> Agregando-LodeRunner
                break;
            case "Original":
            default:
                imagenPelota = null;
                break;
        }
    }

    @Override
    public void mostrar(Graphics2D g2) {
        if (imagenPelota != null) {
            g2.drawImage(imagenPelota, (int) posicionX, (int) posicionY, tamanio, tamanio, null);
        } else {
            g2.setColor(Color.WHITE); // fallback
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.fillOval((int) posicionX, (int) posicionY, tamanio, tamanio);
        }
    }


    @Override
    public int getAncho() {
        return tamanio;
    }

    @Override
    public int getAlto() {
        return tamanio;
    }

    public void setTamanio(int tamanio){
        this.tamanio=tamanio;
    }
    public void setVelocidadY(double velocidadY){
        this.velocidadY=velocidadY;
    }

    public double getVelocidadY(){
        return this.velocidadY;
    }

    public void setVelocidadX(double velocidadX){
        this.velocidadX=velocidadX;
    }

    public double getVelocidadX(){
        return this.velocidadX;
    }

    boolean colisiona(Paleta b) {
        return getX() < b.getX() + b.getAncho() &&
                getX() + getAncho() > b.getX() &&
                getY() < b.getY() + b.getAlto() &&
                getY() + getAlto() > b.getY();
    }

    void invertirDireccionY() {
        // Multiplicamos por -1 para invertir dirección vertical
        setVelocidadY(-getVelocidadY());
    }

    public void reiniciarPelota() {
        this.tamanio = 10;
        posicionX = 400;  // Centro horizontal
        posicionY = 300;  // Centro vertical
    
        // Dirección aleatoria en X (izquierda o derecha)
        if (Math.random() < 0.5) {
            this.velocidadX = 250;  // Derecha
        } else {
         this.velocidadX = -250; // Izquierda
        }
    
        // Dirección aleatoria en Y (arriba o abajo)
        if (Math.random() < 0.5) {
            this.velocidadY = 250;  // Abajo
        } else {
            this.velocidadY = -250; // Arriba
        }
    }
}
