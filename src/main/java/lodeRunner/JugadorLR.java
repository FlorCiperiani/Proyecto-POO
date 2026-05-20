package lodeRunner;

public class JugadorLR extends PersonajeLR {
    public JugadorLR(double x, double y) {
        super("loderunner/jugador.png", x, y, 160.0); // Ruta al recurso del player
    }

    @Override
    public void update(double delta) {
        // Lógica de frames o estados temporales del jugador si la requieren
    }
}