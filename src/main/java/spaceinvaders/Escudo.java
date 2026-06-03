package spaceinvaders;

import clasesCompartidas.ObjetoGrafico;
import java.awt.Color;
import java.awt.Graphics2D;

public class Escudo extends ObjetoGrafico {
    // Dimensiones del escudo en "bloques"
    private static final int FILAS = 30;
    private static final int COLUMNAS = 40;
    private static final int TAMANIO_PIXEL = 2; // Cada bloque mide 2x2 píxeles reales

    // Matriz que representa los píxeles del escudo (true = sólido, false = destruido)
    private boolean[][] matriz;

    public Escudo(double x, double y) {
        super(); // No carga ninguna imagen de la clase padre
        this.posicionX = x;
        this.posicionY = y;
        this.matriz = new boolean[FILAS][COLUMNAS];
        inicializarFormaBunker();
    }

    // Rellena la matriz dándole la forma clásica de herradura/bunker
    private void inicializarFormaBunker() {
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                // Por defecto todo es sólido
                matriz[f][c] = true;

                // 1. Cortar las esquinas superiores para hacerlo redondeado
                if (f < 6) {
                    if (c < 6 - f || c >= COLUMNAS - (6 - f)) {
                        matriz[f][c] = false;
                    }
                }

                // 2. Cavar el túnel/arco de la parte inferior central
                if (f >= 18) {
                    if (c >= 12 && c < COLUMNAS - 12) {
                        matriz[f][c] = false;
                    }
                }
            }
        }
    }

    @Override
    public void mostrar(Graphics2D g2) {
        g2.setColor(Color.GREEN); // Color clásico de los escudos

        // Recorremos la matriz para dibujar solo los píxeles que siguen vivos
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                if (matriz[f][c]) {
                    int px = (int) this.posicionX + (c * TAMANIO_PIXEL);
                    int py = (int) this.posicionY + (f * TAMANIO_PIXEL);
                    g2.fillRect(px, py, TAMANIO_PIXEL, TAMANIO_PIXEL);
                }
            }
        }
    }

    /**
     * Verifica si un proyectil impactó en algún píxel activo.
     * Si impacta, destruye un área pequeña alrededor del choque y devuelve true.
     */
    public boolean verificarImpactoYDegradar(ObjetoGrafico proyectil) {
        // Obtenemos el área rectangular del proyectil
        int pX = (int) proyectil.getX();
        int pY = (int) proyectil.getY();
        int pAncho = proyectil.getAncho();
        int pAlto = proyectil.getAlto();

        // Calculamos los límites del escudo en píxeles reales de la pantalla
        int escX = (int) this.posicionX;
        int escY = (int) this.posicionY;
        int escAncho = COLUMNAS * TAMANIO_PIXEL;
        int escAlto = FILAS * TAMANIO_PIXEL;

        // Primero, una comprobación rápida de cajas (AABB) para ver si la bala toca el escudo general
        if (pX < escX + escAncho && pX + pAncho > escX && pY < escY + escAlto && pY + pAlto > escY) {
            
            // Si tocó el escudo, buscamos qué píxeles específicos de la matriz se solapan con la bala
            for (int f = 0; f < FILAS; f++) {
                for (int c = 0; c < COLUMNAS; c++) {
                    if (matriz[f][c]) {
                        // Coordenadas absolutas de este píxel individual en la pantalla
                        int pixelX = escX + (c * TAMANIO_PIXEL);
                        int pixelY = escY + (f * TAMANIO_PIXEL);

                        // Si la bala intersecta este píxel individual... ¡Hay colisión real!
                        if (pX < pixelX + TAMANIO_PIXEL && pX + pAncho > pixelX &&
                            pY < pixelY + TAMANIO_PIXEL && pY + pAlto > pixelY) {
                            
                            // Degradar: Borramos el píxel del impacto y un pequeño radio alrededor (cráter de explosión)
                            crearCrater(f, c);
                            return true; // El proyectil colisionó con éxito
                        }
                    }
                }
            }
        }
        return false;
    }

    // Borra píxeles vecinos para simular un hueco de explosión circular realista
    private void crearCrater(int filaImpacto, int colImpacto) {
        int radio = 3; // Podés agrandar o achicar el tamaño del agujero que deja la bala
        for (int f = filaImpacto - radio; f <= filaImpacto + radio; f++) {
            for (int c = colImpacto - radio; c <= colImpacto + radio; c++) {
                // Validamos no salirnos de los límites de la matriz
                if (f >= 0 && f < FILAS && c >= 0 && c < COLUMNAS) {
                    // Ecuación de distancia para que el cráter sea redondo
                    if (Math.hypot(f - filaImpacto, c - colImpacto) <= radio) {
                        matriz[f][c] = false; // El píxel se desintegra
                    }
                }
            }
        }
    }

    // Si no quedan píxeles en true, el escudo está completamente destruido
    public boolean estaVacio() {
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                if (matriz[f][c]) return false;
            }
        }
        return true;
    }

    // Desactivamos los getters heredados ya que no usamos imágenes directas
    @Override
    public int getAncho() { return COLUMNAS * TAMANIO_PIXEL; }
    @Override
    public int getAlto() { return FILAS * TAMANIO_PIXEL; }
    @Override
    public void update(double delta) {}
}