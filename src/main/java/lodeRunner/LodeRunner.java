package lodeRunner;

import com.entropyinteractive.JGame;
import com.entropyinteractive.Keyboard;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class LodeRunner extends JGame {
    private MapaLR mapa;
    private JugadorLR jugador;
    private ArrayList<EnemigoLR> enemigos;

    // El constructor que llama tu LanzadorJuego
    public LodeRunner(String titulo, int ancho, int alto) {
        super(titulo, ancho, alto);
    }

    @Override
    public void gameStartup() {
        // Matriz de prueba para el Nivel 1: 
        // 0=Vacío, 1=Ladrillo, 2=Piedra, 3=Escalera, 4=Barra, 5=Oro
        int[][] nivelInicial = {
            {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
            {2,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,5,2},
            {2,0,0,5,0,0,0,3,0,0,0,5,0,0,0,0,3,0,0,0,0,0,1,1,2},
            {2,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,0,0,0,0,2},
            {2,0,0,0,0,0,0,3,0,0,0,0,0,0,3,0,0,0,0,1,1,0,0,0,2},
            {2,0,5,0,0,0,0,3,4,4,4,4,4,4,3,0,0,5,0,0,1,1,0,0,2},
            {2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2}
        };

        // Inicializamos los componentes usando los recursos que ya tenés
        mapa = new MapaLR(nivelInicial);
        
        // Posicionamos al jugador y a un enemigo de prueba (en píxeles, ej: x=64, y=64)
        jugador = new JugadorLR(64, 64);
        enemigos = new ArrayList<>();
        enemigos.add(new EnemigoLR(250, 64));
    }

    @Override
    public void gameUpdate(double delta) {
        Keyboard teclado = this.getKeyboard();

        // Procesar las entradas del teclado para el jugador
        jugador.procesarEntrada(teclado, delta);
        jugador.update(delta);

        // Actualizar la lógica de los enemigos
        for (EnemigoLR enemigo : enemigos) {
            enemigo.update(delta);
        }

        // Si presionan ESCAPE, se cierra la ventana del juego y vuelve al lanzador
        if (teclado.isPressed(KeyEvent.VK_ESCAPE)) {
            stop();
        }
    }

    @Override
    public void gameDraw(Graphics2D g2) {
        // Pintamos el fondo de negro para que resalten los bloques
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Dibujamos el mapa estático (Ladrillos, escaleras, oro...)
        if (mapa != null) {
            mapa.dibujar(g2);
        }

        // Dibujamos al personaje principal (usa el mostrar de ObjetoGrafico)
        if (jugador != null) {
            jugador.mostrar(g2);
        }

        // Dibujamos la lista de enemigos
        if (enemigos != null) {
            for (EnemigoLR enemigo : enemigos) {
                enemigo.mostrar(g2);
            }
        }
    }

    @Override
    public void gameShutdown() {
        // Código de limpieza si llegan a usar recursos del sistema pesados
    }
}