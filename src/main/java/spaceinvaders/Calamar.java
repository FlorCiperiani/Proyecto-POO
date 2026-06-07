package spaceinvaders;

public class Calamar extends Enemigo {
    public Calamar(double x, double y, double velocidadInicial) {
        this(x, y, "/AssetsSpace/green.png", velocidadInicial);
    }

    public Calamar(double x, double y, String rutaImagen, double velocidadInicial) {
        super(x, y, rutaImagen, 10, velocidadInicial);
    }
}