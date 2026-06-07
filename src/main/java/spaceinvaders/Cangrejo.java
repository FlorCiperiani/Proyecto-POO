package spaceinvaders;

public class Cangrejo extends Enemigo {
    public Cangrejo(double x, double y, double velocidadInicial) {
        super(x, y, "/AssetsSpace/yellow.png", 20, velocidadInicial);
    }
    public Cangrejo(double x, double y, String rutaImagen, double velocidadInicial) {
        super(x, y, rutaImagen, 20, velocidadInicial);
    }
}