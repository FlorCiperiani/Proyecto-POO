package spaceinvaders;

import clasesCompartidas.Ranking;

/**
 * Implementación específica del sistema de ranking para Space Invaders.
 * Extiende la clase abstracta compartida Ranking y define su propia tabla en la base de datos.
 */
public class RankingSpace extends Ranking {

    // Nombre del archivo de la base de datos local
    private static final String RUTA_DB_SPACE = "spaceinvaders.db";
    
    // Nombre de la tabla específica para este videojuego
    private static final String TABLA_SPACE = "ranking_space";

    /**
     * Constructor por defecto. 
     * Llama al constructor de la clase madre pasando la configuración propia de Space Invaders.
     */
    public RankingSpace() {
        super(RUTA_DB_SPACE, TABLA_SPACE);
    }
}