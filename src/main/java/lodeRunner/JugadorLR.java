package lodeRunner;

import clasesCompartidas.Sonido;
import com.entropyinteractive.Keyboard;
import java.awt.event.KeyEvent;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class JugadorLR extends PersonajeLR {

    private int oroRecolectado = 0;
    private int puntos         = 0;
    private int vidas          = 5;

    // Sprites de las 3 poses de caminar
    private BufferedImage imgNeutral;
    private BufferedImage imgIzquierda;
    private BufferedImage imgDerecha;

    // Sprite de caída  (No es gancia con sprite)
    private BufferedImage imgCayendo;

    // Sprites subiendo escalera  (terminé usando el mismo)
    private BufferedImage imgEscaleraA;
    private BufferedImage imgEscaleraB;

    // Sprites en barra
    private BufferedImage imgBarraA;
    private BufferedImage imgBarraB;

    // Teclas configurables
    private int teclaIzq       = KeyEvent.VK_LEFT;
    private int teclaDer       = KeyEvent.VK_RIGHT;
    private int teclaArriba    = KeyEvent.VK_UP;
    private int teclaAbajo     = KeyEvent.VK_DOWN;
    private int teclaRomperIzq = KeyEvent.VK_SPACE;
    private int teclaRomperDer = KeyEvent.VK_SPACE;

    // Última dirección
    // Mantiene el último valor -1 o +1 aunque se suelten las teclas.
    private int ultimaDireccion = -1; // arranca mirando izquierda como el original

    // Quitar, inicialmente usaba X y X como en los juegos del ejemlo, pero en pdf pide que sea con el espacio
    private boolean teclaZAnterior = false;
    private boolean teclaXAnterior = false;

    // tiempo acumulado, frame actual y estado previo
    private double tiempoAnim   = 0.0;
    private int    frameAnim    = 0;
    private int    estadoAnim   = 0;   // 0=suelo 1=cayendo 2=escalera 3=barra
    private boolean moviendoVertical = false;
    private boolean moviendoHorizontal = false;

    private static final double FPS_ESCALERA = 5.0;
    private static final double FPS_BARRA    = 6.0;

    public JugadorLR(double x, double y) {
        super("/lodeRunner/skins/" + MenuConfigLR.skinPersonajeSeleccionado + "/jugador.png", x, y, 120.0);
        String base = "/lodeRunner/skins/" + MenuConfigLR.skinPersonajeSeleccionado + "/";
        imgNeutral   = imagen;
        imgIzquierda = cargarImagen(base + "jugador_left.png");
        imgDerecha   = cargarImagen(base + "jugador_right.png");
        imgCayendo   = cargarImagen(base + "jugador_cayendo.png");
        imgEscaleraA = cargarImagen(base + "jugador_escalera_a.png");
        imgEscaleraB = cargarImagen(base + "jugador_escalera_b.png");
        imgBarraA    = cargarImagen(base + "jugador_barra_a.png");
        imgBarraB    = cargarImagen(base + "jugador_barra_b.png");

        if (imgCayendo   == null) imgCayendo   = imgNeutral;
        if (imgEscaleraA == null) imgEscaleraA = imgNeutral;
        if (imgEscaleraB == null) imgEscaleraB = imgNeutral;
        if (imgBarraA    == null) imgBarraA    = imgNeutral;
        if (imgBarraB    == null) imgBarraB    = imgNeutral;
    }

    private BufferedImage cargarImagen(String ruta) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(ruta);
            return (is != null) ? ImageIO.read(is) : null;
        } catch (java.io.IOException e) { return null; }
    }

    // Entrada principal

    public void procesarEntrada(Keyboard teclado, double delta, double escala) {
        actualizarEstado();
        tiempoAnim += delta;

        boolean presIzq = teclado.isKeyPressed(teclaIzq);
        boolean presDer = teclado.isKeyPressed(teclaDer);

        // Actualizar última dirección para la pose y para cavar
        if (presIzq && !presDer) {
            ultimaDireccion = -1;
        } else if (presDer && !presIzq) {
            ultimaDireccion = +1;
        }
        // Si no hay tecla horizontal, ultimaDireccion mantiene el último valor no-cero

        // Movimiento (bloqueado durante caída libre y si está en hoyo)
        moviendoVertical = false;
        moviendoHorizontal = presIzq || presDer;
        if (!enHoyo) {
            if (presIzq) moverIzquierda(delta);
            if (presDer) moverDerecha(delta);
            if (teclado.isKeyPressed(teclaArriba)) { moverArriba(delta); moviendoVertical = true; }
            if (teclado.isKeyPressed(teclaAbajo))  { moverAbajo(delta);  moviendoVertical = true; }
        }

        // Excavación, UNA SOLA TECLA (espacio).
        // La dirección del agujero depende del último botón direccional apretado:
        // último fue RIGHT entonces agujero a la DERECHA
        // último fue LEFT entonces agujero a la IZQUIERDA
        boolean espacioActual = teclado.isKeyPressed(KeyEvent.VK_SPACE);
        if (espacioActual && !teclaZAnterior) romperLadrillo(ultimaDireccion);
        teclaZAnterior = espacioActual;

        aplicarGravedad(delta);
        recolectarOro();
        actualizarSprite();
    }

    // Excavación

    /*
    Cava el ladrillo del suelo en la dirección indicada.
    direccion = -1 entonces cava a la izquierda
    direccion = +1 entonces cava a la derecha
    */
    private void romperLadrillo(int direccion) {
        if (mapa == null) return;
        if (enBarra)      return;
        if (!enSuelo)     return;

        // Columna del centro del personaje
        int colCentro  = (int)(posicionX + getAncho() / 2.0) / TILE_SIZE;
        int colObjetivo = colCentro + direccion;

        // Fila del suelo
        int filaSuelo = (int)(posicionY + getAlto()) / TILE_SIZE;

        ElementoMapa tile = mapa.getTileEn(colObjetivo, filaSuelo);
        if (tile instanceof Ladrillo) {
            Ladrillo ladrillo = (Ladrillo) tile;
            if (!ladrillo.isRoto()) ladrillo.romper();
        }
    }



    // Recolección de oro

    // Texto al juntar oro
    private final java.util.List<float[]> textosPuntos = new java.util.ArrayList<>();
    // Cada entrada: {x, y, tiempoRestante} en píxeles del mapa

    private void recolectarOro() {
        if (mapa == null) return;
        int filaTorso = (int)(posicionY + getAlto() / 2.0) / TILE_SIZE;
        int filaPies  = (int)(posicionY + getAlto())       / TILE_SIZE;
        int colIzq    = (int)(posicionX)                   / TILE_SIZE;
        int colDer    = (int)(posicionX + getAncho() - 1)  / TILE_SIZE;

        for (int fila : new int[]{filaTorso, filaPies}) {
            for (int col = colIzq; col <= colDer; col++) {
                ElementoMapa tile = mapa.getTileEn(col, fila);
                if (tile instanceof Oro) {
                    Oro oro = (Oro) tile;
                    if (!oro.isRecolectado()) {
                        oro.recolectar();
                        oroRecolectado++;
                        puntos += 100;
                        if (MenuConfigLR.efectosActivados && MenuConfigLR.sonidoGeneralActivado) Sonido.reproducir("LR_monedillas.wav");;
                        textosPuntos.add(new float[]{
                            col * MapaLR.TILE_SIZE + MapaLR.TILE_SIZE / 2f,
                            fila * MapaLR.TILE_SIZE,
                            0.8f
                        });
                    }
                }
            }
        }
    }

    // Dibujar y actualizar los textos "+100". Llamar desde mostrar(). 
    public void dibujarTextosPuntos(java.awt.Graphics2D g2, double delta) {
        java.util.Iterator<float[]> it = textosPuntos.iterator();
        while (it.hasNext()) {
            float[] t = it.next();
            t[2] -= (float) delta;
            if (t[2] <= 0) { it.remove(); continue; }
            // Subir el texto a medida que pasa el tiempo
            float alpha  = Math.min(1f, t[2] / 0.4f);   
            float drawY  = t[1] - (0.8f - t[2]) * 24f; 
            java.awt.AlphaComposite ac = java.awt.AlphaComposite.getInstance(
                java.awt.AlphaComposite.SRC_OVER, alpha);
            java.awt.Composite anterior = g2.getComposite();
            g2.setComposite(ac);
            g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
            g2.setColor(java.awt.Color.YELLOW);
            g2.drawString("+100", t[0] - 10, drawY);
            g2.setComposite(anterior);
        }
    }

    // Sprite según estado con la animación
    private static final int ESTADO_SUELO    = 0;
    private static final int ESTADO_CAYENDO  = 1;
    private static final int ESTADO_ESCALERA = 2;
    private static final int ESTADO_BARRA    = 3;

    private void actualizarSprite() {
        // activar siempre que enEscalera=true, sin importar moviendoVertical.
        int nuevoEstado;
        if      (cayendo)    nuevoEstado = ESTADO_CAYENDO;
        else if (enBarra)    nuevoEstado = ESTADO_BARRA;
        else if (enEscalera) nuevoEstado = ESTADO_ESCALERA;
        else                 nuevoEstado = ESTADO_SUELO;

        // Al cambiar de estado resetear timer y frame para que la animación empiece limpia desde el principio, sin arrastrar tiempo acumulado.
        if (nuevoEstado != estadoAnim) {
            tiempoAnim = 0.0;
            frameAnim  = 0;
            estadoAnim = nuevoEstado;
        }

        switch (nuevoEstado) {

            case ESTADO_CAYENDO:
                imagen = imgCayendo;
                break;

            case ESTADO_ESCALERA: {
                // Avanzar la animación SOLO si el jugador se mueve verticalmente. Si está parado en la escalera congelar el frame.
                // Esto arregla un bug que hacia que titile o parpadee la imagen
                if (moviendoVertical) {
                    double periodo = 1.0 / FPS_ESCALERA;
                    tiempoAnim = tiempoAnim % periodo;
                    frameAnim  = (int)(tiempoAnim / (periodo / 2)) % 2;
                }
                imagen = (frameAnim == 0) ? imgEscaleraA : imgEscaleraB;
                break;
            }

            case ESTADO_BARRA:
                imagen = (ultimaDireccion < 0) ? imgBarraA : imgBarraB;
                break;

            default: // ESTADO_SUELO
                if (!moviendoHorizontal) {
                    imagen = imgNeutral;
                } else if (ultimaDireccion == -1 && imgIzquierda != null) {
                    imagen = imgIzquierda;
                } else if (ultimaDireccion == +1 && imgDerecha != null) {
                    imagen = imgDerecha;
                } else {
                    imagen = imgNeutral;
                }
                break;
        }
    }

    // Vidas
    public void perderVida() { vidas--; }
    public void ganarVida()  { vidas++; }
    public int  getVidas()   { return vidas; }

    // Puntos 
    public void sumarPuntos(int cantidad) { puntos += cantidad; }
    public int  getPuntos()               { return puntos; }

    // Getters
    public int getOroRecolectado() { return oroRecolectado; }
    public void resetOro() { oroRecolectado = 0; }

    public void recargarSprites() {
        String base = "/lodeRunner/skins/" + MenuConfigLR.skinPersonajeSeleccionado + "/";
        imagen       = cargarImagen(base + "jugador.png");
        imgNeutral   = imagen;
        imgIzquierda = cargarImagen(base + "jugador_left.png");
        imgDerecha   = cargarImagen(base + "jugador_right.png");
        imgCayendo   = cargarImagen(base + "jugador_cayendo.png");
        imgEscaleraA = cargarImagen(base + "jugador_escalera_a.png");
        imgEscaleraB = cargarImagen(base + "jugador_escalera_b.png");
        imgBarraA    = cargarImagen(base + "jugador_barra_a.png");
        imgBarraB    = cargarImagen(base + "jugador_barra_b.png");

        if (imgCayendo   == null) imgCayendo   = imgNeutral;
        if (imgEscaleraA == null) imgEscaleraA = imgNeutral;
        if (imgEscaleraB == null) imgEscaleraB = imgNeutral;
        if (imgBarraA    == null) imgBarraA    = imgNeutral;
        if (imgBarraB    == null) imgBarraB    = imgNeutral;
    }

    @Override
    public void update(double delta) { /* Mirar procesarEntrada() */ }
}
