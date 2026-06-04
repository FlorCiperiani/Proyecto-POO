package spaceinvaders;

import com.entropyinteractive.JGame;
import com.entropyinteractive.Keyboard;
import clasesCompartidas.ObjetoGrafico;
import clasesCompartidas.conversorTecla;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class SpaceInvaders extends JGame {

    // ================== COMPONENTES ==================
    private NaveNodriza ufo;
    private Canion canion;
    private Galaxia galaxia;
    private Marcador marcador;

    private ArrayList<Enemigo> enemigos;
    private ArrayList<Proyectil> proyectiles;
    private ArrayList<Escudo> escudos;

    // ================== ESTADO ==================
    private int contadorDisparos = 0;
    private double tiempoParaProximoUfo = 15.0;
    private double tiempoDisparoEnemigo = 0;
    private static final double INTERVALO_DISPARO_ENEMIGO = 5.0;
    private double velocidadBaseAliens = 80.0;
    private double velocidadBaseUFO = 100.0;
  
    // ================== CONTROL DE RITMO RETRO ==================
    private double acumuladorRitmo = 0.0;               
    private boolean moverEnEsteFotograma = false; 

    private boolean disparoPresionado = false;

    // ================== CONTROL DE NIVELES ==================
    private int desplazamientoNivelY = 0;

    // ================== ESTADO DE VIDAS Y GAME OVER ==================
    private int vidas = 3;                  // El jugador arranca con 3 cañones (vidas)
    private boolean juegoTerminado = false; 
    private int vidasConfiguradas = 3;

    // ================== TECLAS ==================
    private int teclaIzquierdaCodigo;
    private int teclaDerechaCodigo;
    private int teclaDisparoCodigo;

    public SpaceInvaders(String titulo, int ancho, int alto) {
        super(titulo, ancho, alto);
    }

    // =================================================
    // STARTUP
    // =================================================
    @Override
    public void gameStartup() {

        // ---- Cargar configuración ----
        Properties config = new Properties();
        try (InputStream input = new FileInputStream("spaceinvaders.properties")) {
            config.load(input);
        } catch (IOException e) {
            config.setProperty("teclaIzquierda", "LEFT");
            config.setProperty("teclaDerecha", "RIGHT");
            config.setProperty("teclaDisparo", "SPACE");
            config.setProperty("fondoGalaxia", "Original");
            config.setProperty("vidas", "3");
        }

        // LEER LAS VIDAS CONFIGURADAS DESDE EL ARCHIVO
        try {
            vidasConfiguradas = Integer.parseInt(config.getProperty("vidas", "3"));
        } catch (NumberFormatException e) {
            vidasConfiguradas = 3; // Resguardo por si el archivo tiene texto inválido
        }
        // ---- Configuración de velocidad ----
        String velocidadConfig = config.getProperty("velocidadInvasores", "Media");
        if ("Lenta".equals(velocidadConfig)) {
            velocidadBaseAliens = 8.0;
        } else if ("Rápida".equals(velocidadConfig)) {
            velocidadBaseAliens = 20.0;
        } else {
            velocidadBaseAliens = 14.0; 
        }

        // ---- Teclas ----
        teclaIzquierdaCodigo = conversorTecla.convertirTecla(config.getProperty("teclaIzquierda"));
        teclaDerechaCodigo   = conversorTecla.convertirTecla(config.getProperty("teclaDerecha"));
        teclaDisparoCodigo   = conversorTecla.convertirTecla(config.getProperty("teclaDisparo"));

        // ---- Fondo y HUD ----
        galaxia = new Galaxia();
        galaxia.setEstilo(config.getProperty("fondoGalaxia", "Original"));
        marcador = new Marcador(30, 65);

        // ---- Listas ----
        proyectiles = new ArrayList<>();
        enemigos = new ArrayList<>();
        escudos = new ArrayList<>();

        // ---- UFO ----
        ufo = new NaveNodriza(velocidadBaseUFO);

        // ---- Inicializar partida ----
        reiniciarPartidaCompleta();

        String cancionElegida = config.getProperty("musicaFondo", "space-invaders.wav");
        clasesCompartidas.Musica.iniciarMusica(cancionElegida);
    }

    // Método que restablece todas las variables al estado inicial
    private void reiniciarPartidaCompleta() {
        desplazamientoNivelY = 0;
        contadorDisparos = 0;
        tiempoParaProximoUfo = 15.0;
        tiempoDisparoEnemigo = 0;
        acumuladorRitmo = 0.0;
        
        // Asignamos las vidas que el usuario guardó en la ventana de configuración
        vidas = vidasConfiguradas; 
        juegoTerminado = false;

        marcador = new Marcador(30, 65); 

        proyectiles.clear();
        enemigos.clear();
        escudos.clear();

        generarHordaEnemigos();

        canion = new Canion(getWidth() / 2.0, getHeight() - 80);

        for (int i = 0; i < 4; i++) {
            double x = (getWidth() / 5.0) * (i + 1) - 40;
            double y = getHeight() - 150;
            escudos.add(new Escudo(x, y));
        }
    }

    private void generarHordaEnemigos() {
        enemigos.clear();
        for (int fila = 0; fila < 5; fila++) {
            for (int col = 0; col < 11; col++) {
                double x = 60 + col * 55;
                double y = 70 + (fila * 40) + desplazamientoNivelY;

                if (fila <= 1) {
                    enemigos.add(new Pulpo(x, y, velocidadBaseAliens));
                } else if (fila <= 3) {
                    enemigos.add(new Cangrejo(x, y, velocidadBaseAliens));
                } else {
                    enemigos.add(new Calamar(x, y, velocidadBaseAliens));
                }
            }
        }
    }

    // =================================================
    // UPDATE
    // =================================================
    @Override
    public void gameUpdate(double delta) {

        Keyboard kb = getKeyboard();

        // SI EL JUEGO TERMINÓ: Escuchamos únicamente la tecla R para reiniciar
        if (juegoTerminado) {
            if (kb.isKeyPressed(KeyEvent.VK_R)) {
                reiniciarPartidaCompleta();
            }
            return; 
        }

        // ---- Movimiento jugador ----
        if (kb.isKeyPressed(teclaIzquierdaCodigo)) canion.moverIzquierda(delta);
        if (kb.isKeyPressed(teclaDerechaCodigo)) canion.moverDerecha(delta, getWidth());

        // ---- Disparo jugador ----
        if (kb.isKeyPressed(teclaDisparoCodigo)) {
            if (!disparoPresionado && !hayDisparoJugadorActivo()) {
                proyectiles.add(new Proyectil(
                        canion.getX() + canion.getAncho() / 2.0,
                        canion.getY(),
                        true
                ));
                contadorDisparos++;
                disparoPresionado = true;
                clasesCompartidas.Sonido.reproducir("laser.wav");
            }
        } else {
            disparoPresionado = false;
        }

        // ---- UFO ----
        if (!ufo.isActiva()) {
            tiempoParaProximoUfo -= delta;
            if (tiempoParaProximoUfo <= 0) {
                ufo.aparecer(getWidth());
                tiempoParaProximoUfo = 20 + Math.random() * 15;
            }
        } else {
            ufo.update(delta);
        }

        // ---- Enemigos ----
        int totalInicialAliens = 55;
        int aliensVivos = enemigos.size();

        if (aliensVivos == 0) {
            desplazamientoNivelY += 40; 
            acumuladorRitmo = 0.0;
            generarHordaEnemigos();
            return; 
        }

        double intervaloGolpeMovimiento = 0.06 + (0.79 * ((double) aliensVivos / totalInicialAliens));

        moverEnEsteFotograma = false;
        acumuladorRitmo += delta;

        if (acumuladorRitmo >= intervaloGolpeMovimiento) {
            acumuladorRitmo = 0.0;        
            moverEnEsteFotograma = true;  
        }

        boolean cambiarDireccion = false;

        if (moverEnEsteFotograma) {
            for (Enemigo e : enemigos) {
                e.update(1.0); 

                // COMPROBACIÓN DE INVASIÓN TOTAL
                if ((e.getY() + e.getAlto()) >= canion.getY()) {
                    juegoTerminado = true; 
                }

                if (e.getX() < 10 || e.getX() > getWidth() - e.getAncho() - 10) {
                    cambiarDireccion = true;
                }
            }

            if (juegoTerminado) {
                System.out.println("GAME OVER: ¡Invasión total detectada!");
                clasesCompartidas.Musica.detenerMusicaFondo();
                return;
            }

            if (cambiarDireccion) {
                for (Enemigo e : enemigos) {
                    e.invertirDireccionYBajar();
                    double direccion = Math.signum(e.getVelocidadX());
                    e.setVelocidadX(direccion * velocidadBaseAliens);
                }
            }
        }

        // ---- Proyectiles ----
        for (int i = proyectiles.size() - 1; i >= 0; i--) {
            Proyectil p = proyectiles.get(i);
            p.update(delta);
            if (p.getY() < 0 || p.getY() > getHeight()) {
                proyectiles.remove(i);
            }
        }

        // ---- COLISIONES PROYECTIL VS ENEMIGOS / JUGADOR ----
        for (int i = proyectiles.size() - 1; i >= 0; i--) {
            Proyectil p = proyectiles.get(i);
            
            // Caso A: Disparo del jugador golpea a un alien
            if (p.isDisparoJugador()) {
                for (int j = enemigos.size() - 1; j >= 0; j--) {
                    Enemigo e = enemigos.get(j);
                    if (p.colisionaCon(e)) {
                        marcador.incrementarPuntaje(e.getPuntos());
                        enemigos.remove(j);
                        proyectiles.remove(i);
                        break;
                    }
                }
            } 
            // Caso B: Disparo alienígena golpea al cañón del jugador
            else {
                if (p.colisionaCon(canion)) {
                    vidas--; // El jugador pierde un cañón
                    proyectiles.remove(i); // Quitamos la bala enemiga
                    clasesCompartidas.Sonido.reproducir("explosion.wav"); // Sonido de impacto opcional
                    
                    if (vidas <= 0) {
                        juegoTerminado = true;
                        clasesCompartidas.Musica.detenerMusicaFondo();
                    }
                    break;
                }
            }
        }

        // ---- Colisión UFO ----
        if (ufo.isActiva()) {
            for (int i = proyectiles.size() - 1; i >= 0; i--) {
                Proyectil p = proyectiles.get(i);
                if (p.colisionaCon(ufo)) {
                    marcador.incrementarPuntaje(ufo.calcularPuntajeEspecial(contadorDisparos));
                    ufo.desactivar();
                    proyectiles.remove(i);
                    break;
                }
            }
        }

        // ---- Escudos ----
        for (int i = proyectiles.size() - 1; i >= 0; i--) {
            Proyectil p = proyectiles.get(i);
            
            if (p.isDisparoJugador() && p.getY() > getHeight() - 140) {
                continue; 
            }
            
            for (int j = escudos.size() - 1; j >= 0; j--) {
                Escudo esc = escudos.get(j);
                if (esc.verificarImpactoYDegradar(p)) {
                    proyectiles.remove(i);
                    if (esc.estaVacio()) escudos.remove(j);
                    break;
                }
            }
        }

        // ---- Disparo enemigo ----
        tiempoDisparoEnemigo += delta;
        if (tiempoDisparoEnemigo >= INTERVALO_DISPARO_ENEMIGO) {
            disparoEnemigoAleatorio();
            tiempoDisparoEnemigo = 0.0; 
        }
    }

    // =================================================
    // DRAW
    // =================================================
    @Override
    public void gameDraw(Graphics2D g2) {

        galaxia.mostrar(g2, getWidth(), getHeight());
        
        if (!juegoTerminado) {
            canion.mostrar(g2);
            for (Enemigo e : enemigos) e.mostrar(g2);
            for (Proyectil p : proyectiles) p.mostrar(g2);
            for (Escudo esc : escudos) esc.mostrar(g2);
            if (ufo.isActiva()) ufo.mostrar(g2);
            marcador.mostrar(g2);

            // ---- DIBUJAR VIDAS (Esquina superior derecha) ----
            double origX = canion.getX();
            double origY = canion.getY();

            int anchoCanion = canion.getAncho();
            int margenDerecho = getWidth() - 30; // Punto inicial (tope derecho del HUD)
            int Y_Vidas = 35;                    // Altura del HUD superior

            // Recorremos las vidas que le quedan al jugador
            for (int i = 0; i < vidas; i++) {
                int posX_Vida = margenDerecho - anchoCanion - (i * (anchoCanion + 15));
                
                canion.setX(posX_Vida);
                canion.setY(Y_Vidas);
                canion.mostrar(g2); 
            }

            // Restauramos las coordenadas del cañón jugable
            canion.setX(origX);
            canion.setY(Y_Vidas);
            canion.setY(origY);

        } else {
            // ---- PANTALLA DE GAME OVER ----
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(new Font("Monospaced", Font.BOLD, 50));
            g2.setColor(Color.RED);
            String textoPerdiste = "¡Perdiste!";
            int anchoTextoPerdiste = g2.getFontMetrics().stringWidth(textoPerdiste);
            g2.drawString(textoPerdiste, (getWidth() - anchoTextoPerdiste) / 2, getHeight() / 2 - 60);

            g2.setFont(new Font("Monospaced", Font.PLAIN, 24));
            g2.setColor(Color.WHITE);
            String textoPuntos = "Tu puntuacion final es: " + marcador.getPuntaje(); 
            int anchoTextoPuntos = g2.getFontMetrics().stringWidth(textoPuntos);
            g2.drawString(textoPuntos, (getWidth() - anchoTextoPuntos) / 2, getHeight() / 2);

            g2.setFont(new Font("Monospaced", Font.BOLD, 22));
            g2.setColor(Color.GREEN);
            String textoReiniciar = "[ Presiona 'R' para Volver a Jugar ]";
            int anchoTextoReiniciar = g2.getFontMetrics().stringWidth(textoReiniciar);
            g2.drawString(textoReiniciar, (getWidth() - anchoTextoReiniciar) / 2, getHeight() / 2 + 70);
        }
    }

    // =================================================
    // UTILS
    // =================================================
    private boolean hayDisparoJugadorActivo() {
        for (Proyectil p : proyectiles) {
            if (p.isDisparoJugador()) return true;
        }
        return false;
    }

    private void disparoEnemigoAleatorio(){
        if (enemigos.isEmpty()) return;

        int index = (int)(Math.random() * enemigos.size());
        Enemigo e = enemigos.get(index);

        Proyectil p = new Proyectil(
            e.getX() + e.getAncho() / 2.0, 
            e.getY() + e.getAlto(),       
            false
        );
        proyectiles.add(p);
    }

    @Override
    public void gameShutdown() {
        clasesCompartidas.Musica.detenerMusicaFondo();
    }
}