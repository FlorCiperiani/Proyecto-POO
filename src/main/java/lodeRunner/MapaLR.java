package lodeRunner;

import java.awt.Graphics2D;

public class MapaLR {
    private ElementoMapa[][] grilla;
    public static final int TILE_SIZE = 32; // Se ajusta al tamaño en píxeles de sus imágenes

    public MapaLR(int[][] diseñoNivel) {
        int filas = diseñoNivel.length;
        int columnas = diseñoNivel[0].length;
        grilla = new ElementoMapa[filas][columnas];

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                int x = c * TILE_SIZE;
                int y = f * TILE_SIZE;
                
                switch (diseñoNivel[f][c]) {
                    case 1: grilla[f][c] = new Ladrillo(x, y); break;
                    case 2: grilla[f][c] = new Piedra(x, y); break;
                    case 3: grilla[f][c] = new Escalera(x, y); break;
                    case 4: grilla[f][c] = new Barra(x, y); break;
                    case 5: grilla[f][c] = new Oro(x, y); break;
                    default: grilla[f][c] = null; break; // Espacio libre (aire)
                }
            }
        }
    }

    public void dibujar(Graphics2D g2) {
        for (int f = 0; f < grilla.length; f++) {
            for (int c = 0; c < grilla[f].length; c++) {
                if (grilla[f][c] != null) {
                    grilla[f][c].mostrar(g2); // Llama al método heredado de ObjetoGrafico
                }
            }
        }
    }

    public ElementoMapa getTileEn(int col, int fila) {
        if (fila >= 0 && fila < grilla.length && col >= 0 && col < grilla[0].length) {
            return grilla[fila][col];
        }
        return null;
    }

    public void romperLadrillo(int col, int fila) {
        if (getTileEn(col, fila) instanceof Ladrillo) {
            grilla[fila][col] = null; 
        }
    }
}