package spaceinvaders;

import clasesCompartidas.Ranking;

public class RankingSpace extends Ranking {

    // Nombre del archivo de la base de datos local
    private static final String RUTA_DB_SPACE = "spaceinvaders.db";
    
    // Nombre de la tabla específica para este videojuego
    private static final String TABLA_SPACE = "ranking_space";

    public RankingSpace() {
        super(RUTA_DB_SPACE, TABLA_SPACE);
    }
}