package spaceinvaders;


public class NaveNodriza extends Enemigo {
    private boolean activa = false;

    public NaveNodriza(double velocidadInicial) {
        super(900, 70, "/AssetsSpace/extra.png", 0, -Math.abs(velocidadInicial) * 1.5);
    }

    public void aparecer(int anchoPantalla) {
        this.posicionX = anchoPantalla;
        this.activa = true;
       // Sonido.reproducir("nave-nodriza.wav"); 
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
        // Disparo 23 y luego cada 15 (38, 53, 68...) = 300 puntos máximo
        if (totalDisparosJugador == 23 ||
           (totalDisparosJugador > 23 && (totalDisparosJugador - 23) % 15 == 0)) {
            return 300;
        }
        // Resto: valor aleatorio entre 50 y 250 en múltiplos de 50
        return 50 + ((int)(Math.random() * 5)) * 50;
    }
}