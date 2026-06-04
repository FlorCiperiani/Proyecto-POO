package spaceinvaders;

public class NaveNodriza extends Enemigo {
    private boolean activa = false;

    public NaveNodriza(double velocidadInicial) {
        super(900, 70, "/AssetsSpace/extra.png", 0, -Math.abs(velocidadInicial) * 1.5); 
    }

    public void aparecer(int anchoPantalla) {
        this.posicionX = anchoPantalla; 
        this.activa = true;
    }

    @Override
    public void update(double delta) {
        if (activa) {
            this.posicionX += velocidadX * delta;
            if (this.posicionX < -getAncho()) {
                desactivar();
            }
        }
    }

    public void desactivar() {
        this.activa = false;
       
    }

    public boolean isActiva() {
        return activa;
    }

    public int calcularPuntajeEspecial(int totalDisparosJugador) {
        if (totalDisparosJugador == 23 || (totalDisparosJugador > 23 && (totalDisparosJugador - 23) % 15 == 0)) {
            return 300;
        }
        return 50 + ((int)(Math.random() * 5)) * 50; 
    }
}