package spaceinvaders;

import com.entropyinteractive.JGame;
import com.entropyinteractive.Keyboard;
import clasesCompartidas.ObjetoGrafico;
import clasesCompartidas.conversorTecla;

import java.awt.Graphics2D;
import java.awt.Color;
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

    private boolean disparoPresionado = false;

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
        }

        // ---- Velocidad aliens ----
        String velocidadConfig = config.getProperty("velocidadInvasores", "Media");
        if ("Lenta".equals(velocidadConfig)) velocidadBaseAliens = 50;
        else if ("Rápida".equals(velocidadConfig)) velocidadBaseAliens = 120;
        else velocidadBaseAliens = 80;

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
        ufo = new NaveNodriza(velocidadBaseAliens);

        // ---- Horda ----
        for (int fila = 0; fila < 5; fila++) {
            for (int col = 0; col < 11; col++) {
                double x = 60 + col * 55;
                double y = 70 + fila * 40;

                if (fila <= 1) enemigos.add(new Pulpo(x, y, velocidadBaseAliens));
                else if (fila <= 3) enemigos.add(new Cangrejo(x, y, velocidadBaseAliens));
                else enemigos.add(new Calamar(x, y, velocidadBaseAliens));
            }
        }

        // ---- Jugador ----
        canion = new Canion(getWidth() / 2.0, getHeight() - 80);

        // ---- Escudos ----
        for (int i = 0; i < 4; i++) {
            double x = (getWidth() / 5.0) * (i + 1) - 40;
            double y = getHeight() - 150;
            escudos.add(new Escudo(x, y));
        }
    }

    // =================================================
    // UPDATE
    // =================================================
    @Override
    public void gameUpdate(double delta) {

        Keyboard kb = getKeyboard();

        // ---- Movimiento jugador ----
        if (kb.isKeyPressed(teclaIzquierdaCodigo)) canion.moverIzquierda(delta);
        if (kb.isKeyPressed(teclaDerechaCodigo)) canion.moverDerecha(delta, getWidth());

        // ---- Disparo jugador (1 solo) ----
        if (kb.isKeyPressed(teclaDisparoCodigo)) {
            if (!disparoPresionado && !hayDisparoJugadorActivo()) {
                proyectiles.add(new Proyectil(
                        canion.getX() + canion.getAncho() / 2.0,
                        canion.getY(),
                        true
                ));
                contadorDisparos++;
                disparoPresionado = true;
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
        boolean cambiarDireccion = false;
        for (Enemigo e : enemigos) {
            e.update(delta);
            if (e.getX() < 10 || e.getX() > getWidth() - e.getAncho() - 10) {
                cambiarDireccion = true;
            }
        }

        if (cambiarDireccion) {
            double factor = 1.0 + (55.0 - enemigos.size()) * 0.04;
            for (Enemigo e : enemigos) {
                e.invertirDireccionYBajar();
                e.setVelocidadX(Math.signum(e.getVelocidadX()) * velocidadBaseAliens * factor);
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

        // ---- Colisiones proyectil vs enemigos ----
        for (int i = proyectiles.size() - 1; i >= 0; i--) {
            Proyectil p = proyectiles.get(i);
            for (int j = enemigos.size() - 1; j >= 0; j--) {
                Enemigo e = enemigos.get(j);
                if (p.isDisparoJugador() && p.colisionaCon(e)) {
                    marcador.incrementarPuntaje(e.getPuntos());
                    enemigos.remove(j);
                    proyectiles.remove(i);
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

        // ---- Escudos (CORREGIDO: Evita que el disparo del jugador se borre solo) ----
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

        // ---- Disparo enemigo (CORREGIDO: Sintaxis limpia y espaciada) ----
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
        canion.mostrar(g2);

        for (Enemigo e : enemigos) e.mostrar(g2);
        for (Proyectil p : proyectiles) p.mostrar(g2);
        for (Escudo esc : escudos) esc.mostrar(g2);

        if (ufo.isActiva()) ufo.mostrar(g2);
        marcador.mostrar(g2);
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
            e.getX() + e.getAncho() / 2.0, // Cambiado de getPosicionX a getX
            e.getY() + e.getAlto(),       // Cambiado de getPosicionY a getY
            false
        );
        proyectiles.add(p);
    }

    @Override
    public void gameShutdown() {}
}