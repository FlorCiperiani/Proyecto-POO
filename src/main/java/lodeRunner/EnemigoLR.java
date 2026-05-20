package lodeRunner;

public class EnemigoLR extends PersonajeLR {
    public EnemigoLR(double x, double y) {
        super("loderunner/enemigo.png", x, y, 100.0); // Un poco más lento que el player
    }

    @Override
    public void update(double delta) {
        // Lógica de persecución artificial
    }
}