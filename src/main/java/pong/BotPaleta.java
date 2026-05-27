package pong;

public class BotPaleta extends Paleta {
    private final Pelota pelota;
    private static final double VELOCIDAD_BOT = 280;
    private static final double MARGEN_DEADZONE = 10;

    public BotPaleta(int ancho, int alto, double xInicial, double yInicial, Pelota pelota) {
        super(ancho, alto, xInicial, yInicial, null, 0, 0);
        this.pelota = pelota;
    }

    @Override
    public void update(double delta) {
        if (pelota != null) {
            double centroPaleta = getY() + getAlto() / 2.0;
            double centroPelota = pelota.getY() + pelota.getAlto() / 2.0;
            if (centroPaleta < centroPelota - MARGEN_DEADZONE) {
                setVelocidad(VELOCIDAD_BOT);
            } else if (centroPaleta > centroPelota + MARGEN_DEADZONE) {
                setVelocidad(-VELOCIDAD_BOT);
            } else {
                setVelocidad(0);
            }
        }
        super.update(delta);
    }
}
