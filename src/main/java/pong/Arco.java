package pong;

import clasesCompartidas.ObjetoGrafico;

class Arco extends ObjetoGrafico {
    private final int posicionX;
    private final Marcador MarcadorAsociado;

    public Arco(int posicionX, boolean esIzquierdo) {

        this.posicionX = posicionX;

        // Determina la posición del marcador según la posición del arco
        int marcadorX = esIzquierdo ? 550 : 250;
        this.MarcadorAsociado = new Marcador(marcadorX, 80);
    }

    public boolean detectaGol(Pelota pelota) {
        boolean gol= false;
        // Arco izquierdo (X=0)
        if (this.posicionX == 0 && pelota.getX() <= 0) {
            gol = true;
        }

        // Arco derecho (X=anchoPantalla)
        else if (this.posicionX > 0 && pelota.getX() + pelota.getAncho() >= this.posicionX) {
            gol = true;
        }

        if (gol) {
            MarcadorAsociado.incrementarPuntaje();
        }

        return gol;
    }

    public Marcador getMarcador() {
        return this.MarcadorAsociado;
    }

    @Override
    public void update(double delta){}
    @Override
    public double getX() {
        return this.posicionX;
    }
    @Override
    public double getY() {
        return this.posicionY;
    }

}
