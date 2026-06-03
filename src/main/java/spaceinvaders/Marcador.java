package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;
import java.awt.*;

class Marcador extends ObjetoGrafico {
    private int puntaje;

    public Marcador(double posicionX, double posicionY) {
        puntaje = 0;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
    }

    // MODIFICADO: Ahora recibe los puntos específicos del enemigo destruido
    public void incrementarPuntaje(int puntos) {
        this.puntaje += puntos;
    }

    public void mostrar(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.drawString(String.valueOf(puntaje), (int) posicionX, (int) posicionY);
    }

    public int getPuntaje() {
        return puntaje;
    }

    @Override
    public void update(double delta) {
    }
}