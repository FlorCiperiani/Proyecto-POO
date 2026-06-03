package spaceinvaders;

public class NaveNodriza extends Enemigo {
    private boolean activa = false;

    public NaveNodriza(double velocidadInicial) {
        // Inicialmente aparece fuera de la pantalla (X de la derecha) y arriba de todo (Y = 20)
        super(900, 70, "/AssetsSpace/extra.png", 0, -Math.abs(velocidadInicial) * 1.5); 
        // Va de derecha a izquierda (velocidad negativa) y un poco más rápido
    }

    public void aparecer(int anchoPantalla) {
        this.posicionX = anchoPantalla; // Reaparece a la derecha
        this.activa = true;
        // AQUÍ REPRODUCÍS EL SONIDO REPETITIVO (UFO SOUND)
        // Ejemplo ficticio: ControladorSonido.playLoop("ufo.wav");
    }

    @Override
    public void update(double delta) {
        if (activa) {
            this.posicionX += velocidadX * delta;
            // Si cruza toda la pantalla y sale por la izquierda, se desactiva
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

    // Calcular puntaje especial en base a los disparos del jugador
    public int calcularPuntajeEspecial(int totalDisparosJugador) {
        if (totalDisparosJugador == 23 || (totalDisparosJugador > 23 && (totalDisparosJugador - 23) % 15 == 0)) {
            return 300;
        }
        // Si no cumple la regla de la consigna, podés dar un valor aleatorio entre 50 y 250
        return 50 + ((int)(Math.random() * 5)) * 50; 
    }
}