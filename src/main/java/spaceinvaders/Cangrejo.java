package spaceinvaders;

public class Cangrejo extends Enemigo {
    public Cangrejo(double x, double y, double velocidadInicial) {
        this(x, y, "/AssetsSpace/yellow.png", velocidadInicial);
    }

    public Cangrejo(double x, double y, String rutaImagen, double velocidadInicial) {
        super(x, y, rutaImagen, 20, velocidadInicial);
    }

}