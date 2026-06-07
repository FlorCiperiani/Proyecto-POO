package pong;

import clasesCompartidas.Ranking;

/**
 * Ranking específico del Pong.
 * Tabla: ranking_pong
 * Campos heredados: nombre, nivel (usado como "ronda", siempre 1), puntaje, fecha.
 */
public class RankingPong extends Ranking {

    public RankingPong() {
        super("db/ranking.db", "ranking_pong");
    }

    /**
     * Guarda el resultado de una partida.
     * @param nombreGanador  Nombre ingresado por el jugador ganador
     * @param puntajeGanador Puntaje del ganador (ej: 10)
     */
    public void guardarPartida(String nombreGanador, int puntajeGanador) {
        // En Pong no hay "niveles", usamos 1 como valor fijo
        guardar(nombreGanador, 1, puntajeGanador);
    }
}