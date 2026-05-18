package spaceinvaders;

import com.entropyinteractive.JGame;
import com.entropyinteractive.Keyboard;
import clasesCompartidas.conversorTecla;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class SpaceInvaders extends JGame {

    // Componentes del juego corregidos
    private Canion canion;
    private ArrayList<Enemigo> enemigos;
    private ArrayList<Proyectil> proyectiles;
    private Galaxia galaxia;
    private Marcador marcador;

    public SpaceInvaders(String titulo, int ancho, int alto) {
        super(titulo, ancho, alto);
    }

@Override
public void gameStartup() {
    // === 1. CARGAR CONFIGURACIÓN DESDE EL ARCHIVO ===
    Properties config = new Properties();
    try (InputStream input = new FileInputStream("spaceinvaders.properties")) {
        config.load(input);
    } catch (IOException e) {
        System.out.println("No se encontró archivo de configuración, usando valores por defecto.");
    }

    // === 2. INICIALIZAR FONDO Y HUD (Usando los datos cargados) ===
    galaxia = new Galaxia();
    // Le pasamos el fondo guardado; si no hay ninguno, usa "Original"
    galaxia.setEstilo(config.getProperty("fondoGalaxia", "Original")); 
    
    marcador = new Marcador(20, 40);

    // === 3. INICIALIZAR JUGADOR (CAÑÓN) ===
    canion = new Canion(getWidth() / 2.0, getHeight() - 80);
    
    // Si necesitas guardar los códigos de las teclas para usarlos en gameUpdate:
    String tIzq = config.getProperty("teclaIzquierda", "LEFT");
    String tDer = config.getProperty("teclaDerecha", "RIGHT");
    // Suponiendo que tenés estas variables enteras declaradas arriba en tu clase:
    // this.teclaIzquierdaCodigo = clasesCompartidas.conversorTecla.convertirTecla(tIzq);
    // this.teclaDerechaCodigo = clasesCompartidas.conversorTecla.convertirTecla(tDer);

    // === 4. INICIALIZAR LISTAS Y ENEMIGOS ===
    proyectiles = new ArrayList<>();
    enemigos = new ArrayList<>();

    // Crear la flota ordenada de invasores
    for (int i = 0; i < 6; i++) {
        for (int j = 0; j < 3; j++) {
            enemigos.add(new Enemigo(100 + i * 60, 50 + j * 40));
        }
    }
}

    @Override
    public void gameUpdate(double delta) {
        Keyboard keyboard = this.getKeyboard();

        // Controles del Cañón (Jugador)
        if (keyboard.isKeyPressed(KeyEvent.VK_LEFT)) {
            canion.moverIzquierda(delta);
        }
        if (keyboard.isKeyPressed(KeyEvent.VK_RIGHT)) {
            canion.moverDerecha(delta, getWidth());
        }

        // Disparar con Espacio
        if (keyboard.isKeyPressed(KeyEvent.VK_SPACE)) {
            // Un pequeño truco para evitar ráfagas infinitas: limitar por tiempo o cantidad
            if (proyectiles.isEmpty()) { 
                proyectiles.add(new Proyectil(canion.getX() + 20, canion.getY(), true));
            }
        }

        // Actualizar proyectiles
        for (int i = proyectiles.size() - 1; i >= 0; i--) {
            Proyectil p = proyectiles.get(i);
            p.update(delta);
            // Eliminar si sale de la pantalla
            if (p.getY() < 0 || p.getY() > getHeight()) {
                proyectiles.remove(i);
            }
        }

        // Lógica de movimiento de enemigos con rebote en bordes
        boolean cambiarDireccion = false;
        for (Enemigo e : enemigos) {
            e.update(delta);
            if (e.getX() < 0 || e.getX() > getWidth() - 40) {
                cambiarDireccion = true;
            }
        }

        if (cambiarDireccion) {
            for (Enemigo e : enemigos) {
                e.invertirDireccionYBajar();
            }
        }
    }

    @Override
    public void gameDraw(Graphics2D g2) {
        // 1. Dibujar Fondo (Se le pasan las dimensiones de la pantalla)
        if (galaxia != null) {
            galaxia.mostrar(g2, getWidth(), getHeight());
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Dibujar elementos de juego
        if (canion != null) {
            // Si no tiene imagen asignada aún, dibujamos un rectángulo verde temporal
            g2.setColor(Color.GREEN);
            g2.fillRect((int)canion.getX(), (int)canion.getY(), 50, 20);
        }

        g2.setColor(Color.RED);
        for (Enemigo e : enemigos) {
            g2.fillRect((int)e.getX(), (int)e.getY(), 30, 20);
        }

        g2.setColor(Color.YELLOW);
        for (Proyectil p : proyectiles) {
            g2.fillRect((int)p.getX(), (int)p.getY(), 4, 10);
        }

        // 3. Dibujar interfaz
        if (marcador != null) {
            marcador.mostrar(g2);
        }
    }

    @Override
    public void gameShutdown() {
        // Liberar recursos si es necesario
    }
}