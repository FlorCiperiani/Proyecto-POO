package lodeRunner;

/*
 Clase base abstracta para todos los niveles de Lode Runner.
 
 Cada nivel concreto (NivelLR1, NivelLR2, NivelLR3...) extiende esta clase
 y define su propio diseño de mapa, spawns y número de nivel.
 
 */
public abstract class NivelLR {

    /*
     Diseño del mapa: grilla de enteros.
     0=Aire  1=Ladrillo  2=Piedra  3=Escalera  4=Barra  5=Oro  6=EscaleraOculta
     */
    public abstract int[][] getDiseño();

    
     //Posición de spawn del jugador: {columna, fila}.
    
    public abstract int[] getSpawnJugador();

     // Posiciones de spawn de los enemigos: array de {columna, fila}.

    public abstract int[][] getSpawnEnemigos();

    
     // Número de nivel (1-based), se muestra en el HUD.
     
    public abstract int getNumero();
}
