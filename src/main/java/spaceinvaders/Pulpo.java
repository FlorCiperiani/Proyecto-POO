package spaceinvaders;

public class Pulpo extends Enemigo {
    public Pulpo(double x, double y, double velocidadInicial) {
        this(x, y, "/AssetsSpace/red.png", velocidadInicial);
    }

    public Pulpo(double x, double y, String rutaImagen, double velocidadInicial) {
        super(x, y, rutaImagen, 30, velocidadInicial);
    }
    public Pulpo(double x, double y, String rutaImagen, double velocidadInicial) {
        super(x, y, rutaImagen, 30, velocidadInicial);
    }
}