package lodeRunner;

import clasesCompartidas.ObjetoGrafico;
 
public abstract class ElementoMapa extends ObjetoGrafico {

    public ElementoMapa(String rutaRecurso, double x, double y) {
        super(rutaRecurso);
        setPosicion(x, y);
    }
}

// Ladrillo (rompible)
class Ladrillo extends ElementoMapa {
    private boolean roto = false;
    private double tiempoRestauracion = 0;
    private static final double TIEMPO_RESTAURAR = 5.0;
    private static final double TIEMPO_CIERRE    = 2.0;

    private java.awt.image.BufferedImage imgRoto;
    private java.awt.image.BufferedImage imgNormal;

    public Ladrillo(double x, double y) {
        super("/lodeRunner/ladrillo.png", x, y);
        imgNormal = imagen;
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/lodeRunner/ladrillo_roto.png");
            if (is != null) imgRoto = javax.imageio.ImageIO.read(is);
        } catch (java.io.IOException e) { e.printStackTrace(); }
    }

    public boolean isRoto() { return roto; }

    public boolean seEstaSerrando() {
        return roto && tiempoRestauracion <= TIEMPO_CIERRE;
    }

    public void romper() {
        roto = true;
        tiempoRestauracion = TIEMPO_RESTAURAR;
        imagen = imgRoto;
    }

    @Override
    public void mostrar(java.awt.Graphics2D g2) {
        if (!roto) { super.mostrar(g2); return; }
        if (tiempoRestauracion > TIEMPO_CIERRE) {
            if (imgRoto != null)
                g2.drawImage(imgRoto, (int)posicionX, (int)posicionY, null);
            return;
        }
        boolean mostrarNormal = ((int)(tiempoRestauracion / 0.15)) % 2 == 0;
        java.awt.image.BufferedImage frame = mostrarNormal ? imgNormal : imgRoto;
        if (frame != null)
            g2.drawImage(frame, (int)posicionX, (int)posicionY, null);
    }

    @Override
    public void update(double delta) {
        if (roto) {
            tiempoRestauracion -= delta;
            if (tiempoRestauracion <= 0) {
                roto = false;
                imagen = imgNormal;
            }
        }
    }
}

// Piedra (no se rompe)
class Piedra extends ElementoMapa {
    public Piedra(double x, double y) {
        super("/lodeRunner/piedra.png", x, y);
    }
    @Override public void update(double delta) {}
}

// Escalera

class Escalera extends ElementoMapa {

    private static final int TILE_SIZE = MapaLR.TILE_SIZE;

    private boolean esTope = false;

    public Escalera(double x, double y) {
        super("/lodeRunner/escalera.png", x, y);
    }

    public void setEsTope(boolean v) { esTope = v; }
    public boolean isEsTope()        { return esTope; }

    @Override
    public void mostrar(java.awt.Graphics2D g2) {
        if (imagen == null) return;

        int dx   = (int) posicionX;
        int dy   = (int) posicionY;
        int sprH = imagen.getHeight();
        int yOffset = Math.max(0, sprH - TILE_SIZE);

        if (esTope && yOffset > 0) {
            // Si el sprite es más alto que la celda, dibujar el sobresaliendo.
            g2.drawImage(imagen, dx, dy - yOffset, null);
        } else if (yOffset > 0) {
            // Si el sprite es más alto que la celda, dibujar solamente la parte inferior de 32px.
            g2.drawImage(imagen,
                dx,             dy,
                dx + TILE_SIZE, dy + TILE_SIZE,
                0,              yOffset,
                TILE_SIZE,      sprH,
                null);
        } else {
            // Sprite de 32px o igual a la celda
            g2.drawImage(imagen, dx, dy, null);
        }
    }

    @Override
    public void update(double delta) {}
}

// Barra horizontal 
class Barra extends ElementoMapa {
    public Barra(double x, double y) {
        super("/lodeRunner/barra.png", x, y);
    }
    @Override public void update(double delta) {}
}

// Oro
class Oro extends ElementoMapa {
    private boolean recolectado = false;

    public Oro(double x, double y) {
        super("/lodeRunner/oro.png", x, y);
    }

    public boolean isRecolectado() { return recolectado; }

    public void recolectar() {
        recolectado = true;
        imagen = null;
    }

    public void devolver(double x, double y) {
        recolectado = false;
        setPosicion(x, y);
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/lodeRunner/oro.png");
            if (is != null) imagen = javax.imageio.ImageIO.read(is);
        } catch (java.io.IOException e) { e.printStackTrace(); }
    }

    @Override public void update(double delta) {}
}
