package spaceinvaders;

import com.entropyinteractive.JGame;
import com.entropyinteractive.Keyboard;

import clasesCompartidas.Ranking;
import clasesCompartidas.Sonido;
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
    private double velocidadBaseAliens = 14.0;
    private double velocidadBaseUFO = 100.0;

    // ================== CONTROL DE RITMO RETRO ==================
    private double acumuladorRitmo = 0.0;
    private boolean moverEnEsteFotograma = false;
    private boolean disparoPresionado = false;

    // ================== CONTROL DE NIVELES ==================
    private int desplazamientoNivelY = 0;

    // ================== ESTADO DE VIDAS, MENÚ Y GAME OVER ==================
    private int vidas = 3;
    private boolean juegoTerminado = false;
    private boolean enMenuInicio = true; // <--- NUEVO: Controla la pantalla de bienvenida
    private int vidasConfiguradas = 3;
    private RankingSpace ranking;

    // ================== CONFIGURACIÓN LEÍDA DEL ARCHIVO ==================
    private boolean sonidoActivado = true;
    private String skinInvasores = "Original";
    private String skinNave = "Original";   

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
        ranking = new RankingSpace();
    
        Properties config = cargarProperties();

        // ── Vidas ────────────────────────────────────────────────────────────
        try {
            vidasConfiguradas = Integer.parseInt(config.getProperty("vidas", "3"));
            if (vidasConfiguradas <= 0) vidasConfiguradas = 3;
        } catch (NumberFormatException e) {
            vidasConfiguradas = 3;
        }

        // ── Velocidad de invasores ────────────────────────────────────────────
        switch (config.getProperty("velocidadInvasores", "Media")) {
            case "Lenta":  velocidadBaseAliens = 8.0;  break;
            case "Rápida": velocidadBaseAliens = 20.0; break;
            default:       velocidadBaseAliens = 14.0; break;
        }

        // ── Sonido ───────────────────────────────────────────────────────────
        sonidoActivado = !"false".equals(config.getProperty("sonido", "true"));

        // ── Skins ────────────────────────────────────────────────────────────
        skinInvasores = config.getProperty("skinInvasores", "Original");
        skinNave      = config.getProperty("skinNave", "Original");

        // ── Teclas ───────────────────────────────────────────────────────────
        teclaIzquierdaCodigo = conversorTecla.convertirTecla(config.getProperty("teclaIzquierda", "LEFT"));
        teclaDerechaCodigo   = conversorTecla.convertirTecla(config.getProperty("teclaDerecha",   "RIGHT"));
        teclaDisparoCodigo   = conversorTecla.convertirTecla(config.getProperty("teclaDisparo",   "SPACE"));

        // ── Fondo ────────────────────────────────────────────────────────────
        galaxia = new Galaxia();
        galaxia.setEstilo(config.getProperty("fondoGalaxia", "Original"));

        // ── Marcador ─────────────────────────────────────────────────────────
        marcador = new Marcador(30, 65);

        // ── Listas ───────────────────────────────────────────────────────────
        proyectiles = new ArrayList<>();
        enemigos    = new ArrayList<>();
        escudos     = new ArrayList<>();

        // ── UFO ──────────────────────────────────────────────────────────────
        ufo = new NaveNodriza(velocidadBaseUFO);

        // ── Partida ───────────────────────────────────────────────────────────
        reiniciarPartidaCompleta();
        enMenuInicio = true; // Nos aseguramos de empezar siempre mostrando el menú

        // ── Música ───────────────────────────────────────────────────────────
        if (sonidoActivado) {
            clasesCompartidas.Musica.iniciarMusica(
                config.getProperty("musicaFondo", "space-invaders.wav")
            );
        }
    }

    // =================================================
    // REINICIO
    // =================================================
    private void reiniciarPartidaCompleta() {
        desplazamientoNivelY = 0;
        contadorDisparos     = 0;
        tiempoParaProximoUfo = 15.0;
        tiempoDisparoEnemigo = 0;
        acumuladorRitmo      = 0.0;
        disparoPresionado    = false;

        vidas          = vidasConfiguradas;
        juegoTerminado = false;

        marcador.resetearPuntaje();

        proyectiles.clear();
        enemigos.clear();
        escudos.clear();

        canion = new Canion(getWidth() / 2.0, getHeight() - 80, obtenerRutaImagenNave());

        generarHordaEnemigos();

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
                    enemigos.add(new Pulpo(x, y, obtenerRutaImagenInvasores("Pulpo"), velocidadBaseAliens));
                } else if (fila <= 3) {
                    enemigos.add(new Cangrejo(x, y, obtenerRutaImagenInvasores("Cangrejo"), velocidadBaseAliens));
                } else {
                    enemigos.add(new Calamar(x, y, obtenerRutaImagenInvasores("Calamar"), velocidadBaseAliens));
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

        // LÓGICA DEL MENÚ DE INICIO (Antes de comenzar)
        if (enMenuInicio) {
            if (kb.isKeyPressed(KeyEvent.VK_ENTER)) {
                enMenuInicio = false; // Comienza el juego al presionar ENTER
            }
            return;
        }

        // LÓGICA DE FIN DE JUEGO (Al finalizar)
        if (juegoTerminado) {
            if (kb.isKeyPressed(KeyEvent.VK_R)) {
                reiniciarPartidaCompleta();
                if (sonidoActivado) {
                    clasesCompartidas.Musica.iniciarMusica(leerPropiedadMusica());
                }
            }
            return;
        }

        // ── Movimiento ───────────────────────────────────────────────────────
        if (kb.isKeyPressed(teclaIzquierdaCodigo)) canion.moverIzquierda(delta);
        if (kb.isKeyPressed(teclaDerechaCodigo))   canion.moverDerecha(delta, getWidth());

        // ── Disparo jugador ──────────────────────────────────────────────────
        if (kb.isKeyPressed(teclaDisparoCodigo)) {
            if (!disparoPresionado && !hayDisparoJugadorActivo()) {
                proyectiles.add(new Proyectil(
                    canion.getX() + canion.getAncho() / 2.0,
                    canion.getY(),
                    true
                ));
                contadorDisparos++;
                disparoPresionado = true;
                if (sonidoActivado) clasesCompartidas.Sonido.reproducir("laser.wav");
            }
        } else {
            disparoPresionado = false;
        }

        // ── UFO ──────────────────────────────────────────────────────────────
        if (!ufo.isActiva()) {
            tiempoParaProximoUfo -= delta;
            if (tiempoParaProximoUfo <= 0) {
                ufo.aparecer(getWidth());
                if (sonidoActivado) Sonido.reproducir("nave-nodriza.wav"); 
                tiempoParaProximoUfo = 20 + Math.random() * 15;
            }
        } else {
            ufo.update(delta);
        }

        // ── Enemigos ─────────────────────────────────────────────────────────
        int aliensVivos = enemigos.size();

        if (aliensVivos == 0) {
            desplazamientoNivelY += 40;
            acumuladorRitmo = 0.0;
            generarHordaEnemigos();
            return;
        }

        double intervalo = 0.06 + (0.79 * ((double) aliensVivos / 55));
        moverEnEsteFotograma = false;
        acumuladorRitmo += delta;

        if (acumuladorRitmo >= intervalo) {
            acumuladorRitmo = 0.0;
            moverEnEsteFotograma = true;
        }

        boolean cambiarDireccion = false;

        if (moverEnEsteFotograma) {
            for (Enemigo e : enemigos) {
                e.update(1.0);
                if ((e.getY() + e.getAlto()) >= canion.getY()) juegoTerminado = true;
                if (e.getX() < 10 || e.getX() > getWidth() - e.getAncho() - 10) cambiarDireccion = true;
            }

            if (juegoTerminado) {
                clasesCompartidas.Musica.detenerMusicaFondo();
                manejarGuardadoRanking();
                return;
            }

            if (cambiarDireccion) {
                for (Enemigo e : enemigos) {
                    e.invertirDireccionYBajar();
                    e.setVelocidadX(Math.signum(e.getVelocidadX()) * velocidadBaseAliens);
                }
            }
        }

        // ── Proyectiles ──────────────────────────────────────────────────────
        for (int i = proyectiles.size() - 1; i >= 0; i--) {
            Proyectil p = proyectiles.get(i);
            p.update(delta);
            if (p.getY() < 0 || p.getY() > getHeight()) proyectiles.remove(i);
        }

        // ── Colisiones ───────────────────────────────────────────────────────
        for (int i = proyectiles.size() - 1; i >= 0; i--) {
            Proyectil p = proyectiles.get(i);

            if (p.isDisparoJugador()) {
                boolean impacto = false;
                for (int j = enemigos.size() - 1; j >= 0; j--) {
                    if (p.colisionaCon(enemigos.get(j))) {
                        marcador.incrementarPuntaje(enemigos.get(j).getPuntos());
                        enemigos.remove(j);
                        proyectiles.remove(i);
                        impacto = true;
                        if (sonidoActivado) Sonido.reproducir("tenton.wav"); 
                        break;
                    }
                }
                if (impacto) continue;

                if (ufo.isActiva() && p.colisionaCon(ufo)) {
                    marcador.incrementarPuntaje(ufo.calcularPuntajeEspecial(contadorDisparos));
                    ufo.desactivar();
                    proyectiles.remove(i);
                    continue;
                }
            } else {
                if (p.colisionaCon(canion)) {
                    vidas--;
                    proyectiles.remove(i);
                    if (sonidoActivado) clasesCompartidas.Sonido.reproducir("explosion.wav");
                    
                    if (vidas <= 0) {
                        juegoTerminado = true;
                        clasesCompartidas.Musica.detenerMusicaFondo();
                        manejarGuardadoRanking(); // <--- Llamada modularizada para pedir nombre
                    }
                    continue;
                }
            }
        }

        // ── Escudos ──────────────────────────────────────────────────────────
        for (int i = proyectiles.size() - 1; i >= 0; i--) {
            Proyectil p = proyectiles.get(i);
            if (p.isDisparoJugador() && p.getY() > getHeight() - 140) continue;

            for (int j = escudos.size() - 1; j >= 0; j--) {
                Escudo esc = escudos.get(j);
                if (esc.verificarImpactoYDegradar(p)) {
                    proyectiles.remove(i);
                    if (esc.estaVacio()) escudos.remove(j);
                    break;
                }
            }
        }

        // ── Disparo enemigo ───────────────────────────────────────────────────
        tiempoDisparoEnemigo += delta;
        if (tiempoDisparoEnemigo >= INTERVALO_DISPARO_ENEMIGO) {
            disparoEnemigoAleatorio();
            tiempoDisparoEnemigo = 0.0;
        }
    }

    /**
     * Pide el nombre de forma gráfica y procesa la persistencia del ranking.
     */
    private void manejarGuardadoRanking() {
        String nombre = javax.swing.JOptionPane.showInputDialog(
            null, 
            "¡GAME OVER!\nTu puntaje: " + marcador.getPuntaje() + "\nIngresá tu nombre para el Ranking:", 
            "Guardar Puntaje", 
            javax.swing.JOptionPane.PLAIN_MESSAGE
        );
        int nivelAlcanzado = (desplazamientoNivelY / 40) + 1;
        ranking.guardar(nombre, nivelAlcanzado, marcador.getPuntaje());
    }

    // =================================================
    // DRAW
    // =================================================
    @Override
    public void gameDraw(Graphics2D g2) {
        // Fondo del espacio común para todas las vistas
        galaxia.mostrar(g2, getWidth(), getHeight());

        // 1. VISTA ANTES DE COMENZAR (MENÚ DE INICIO CON TOP 10)
        if (enMenuInicio) {
            g2.setColor(new Color(0, 0, 0, 220));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(new Font("Monospaced", Font.BOLD, 40));
            g2.setColor(Color.GREEN);
            String tituloMenu = "SPACE INVADERS";
            g2.drawString(tituloMenu, (getWidth() - g2.getFontMetrics().stringWidth(tituloMenu)) / 2, 60);

            // Dibujar el Ranking en la Interfaz Gráfica
            dibujarTablaRanking(g2, 120);

            g2.setFont(new Font("Monospaced", Font.BOLD, 20));
            g2.setColor(Color.WHITE);
            String jugarTxt = "[ Presioná 'ENTER' para comenzar ]";
            g2.drawString(jugarTxt, (getWidth() - g2.getFontMetrics().stringWidth(jugarTxt)) / 2, getHeight() - 40);
            return;
        }

        // 2. VISTA DE PARTIDA EN CURSO
        if (!juegoTerminado) {
            double origX = canion.getX();
            double origY = canion.getY();
            int anchoCanion  = canion.getAncho();
            int altoCanion   = canion.getAlto();
            int vidaAncho    = Math.max(24, anchoCanion / 2);
            int vidaAlto     = Math.max(16, altoCanion / 2);
            int margenDerecho = getWidth() - 30;

            for (int i = 0; i < vidas; i++) {
                int xVida = margenDerecho - vidaAncho - (i * (vidaAncho + 15));
                int yVida = 35;
                g2.drawImage(canion.getImagen(), xVida, yVida, vidaAncho, vidaAlto, null);
            }
            canion.setX(origX);
            canion.setY(origY);
            canion.mostrar(g2);

            for (Enemigo e : enemigos) e.mostrar(g2);
            for (Proyectil p : proyectiles) p.mostrar(g2);
            for (Escudo esc : escudos) esc.mostrar(g2);
            if (ufo.isActiva()) ufo.mostrar(g2);
            marcador.mostrar(g2);

        // 3. VISTA AL FINALIZAR LA PARTIDA (GAME OVER CON TOP 10)
        } else {
            g2.setColor(new Color(0, 0, 0, 230));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(new Font("Monospaced", Font.BOLD, 45));
            g2.setColor(Color.RED);
            String txt1 = "¡PERDISTE!";
            g2.drawString(txt1, (getWidth() - g2.getFontMetrics().stringWidth(txt1)) / 2, 55);

            g2.setFont(new Font("Monospaced", Font.PLAIN, 20));
            g2.setColor(Color.WHITE);
            String txt2 = "Tu puntuación final: " + marcador.getPuntaje();
            g2.drawString(txt2, (getWidth() - g2.getFontMetrics().stringWidth(txt2)) / 2, 90);

            // Dibujar el Ranking actualizado en la interfaz
            dibujarTablaRanking(g2, 140);

            g2.setFont(new Font("Monospaced", Font.BOLD, 20));
            g2.setColor(Color.GREEN);
            String txt3 = "[ Presioná 'R' para volver a jugar ]";
            g2.drawString(txt3, (getWidth() - g2.getFontMetrics().stringWidth(txt3)) / 2, getHeight() - 35);
        }
    }

    /**
     * Dibuja los 10 mejores puntajes directamente sobre el lienzo del juego (g2).
     */
    private void dibujarTablaRanking(Graphics2D g2, int inicioY) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 22));
        g2.setColor(Color.YELLOW);
        String cabecera = "=== TOP 10 MEJORES PUNTAJES ===";
        g2.drawString(cabecera, (getWidth() - g2.getFontMetrics().stringWidth(cabecera)) / 2, inicioY);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
        g2.setColor(Color.CYAN);
        
        // Cabecera estructurada con todas las propiedades de la consigna
        String columnas = String.format("%-3s %-15s %-7s %-10s %-16s", "POS", "JUGADOR", "NIVEL", "PUNTOS", "FECHA");
        int posX = (getWidth() - g2.getFontMetrics().stringWidth(columnas)) / 2;
        g2.drawString(columnas, posX, inicioY + 30);

        g2.setColor(Color.WHITE);
        ArrayList<Ranking.Entrada> top10 = ranking.obtenerTop10();
        
        int renglonY = inicioY + 55;
        int posicion = 1;
        
        for (Ranking.Entrada e : top10) {
            // Formateamos ordenadamente cada campo obtenido de la base de datos
            String filaStr = String.format("%02d. %-15s Niv.%-4d %6d pts  %s", 
                posicion, 
                e.nombre.length() > 14 ? e.nombre.substring(0, 14) : e.nombre, 
                e.nivel, 
                e.puntaje, 
                e.fecha
            );
            g2.drawString(filaStr, posX, renglonY);
            renglonY += 24; // Espaciado vertical entre renglones del ranking
            posicion++;
        }
        
        if(top10.isEmpty()){
            g2.setColor(Color.GRAY);
            String vacio = "No hay registros aún. ¡Sé el primero!";
            g2.drawString(vacio, (getWidth() - g2.getFontMetrics().stringWidth(vacio)) / 2, renglonY + 20);
        }
    }

    // =================================================
    // UTILS
    // =================================================
    private boolean hayDisparoJugadorActivo() {
        for (Proyectil p : proyectiles) if (p.isDisparoJugador()) return true;
        return false;
    }

   private String obtenerRutaImagenInvasores(String tipo) {
        if (tipo == null) return "/AssetsSpace/red.png";

        if ("Alternativa".equals(skinInvasores)) {
            switch (tipo) {
                case "Pulpo":    return "/AssetsSpace/nuevoAzul.png";
                case "Cangrejo": return "/AssetsSpace/nuevorojo.png";
                case "Calamar":  return "/AssetsSpace/nuevoVerde.png";
            }
        } else if ("Oceano".equals(skinInvasores)) {
            switch (tipo) {
                case "Pulpo":    return "/AssetsSpace/Pirania-oceano.png";
                case "Cangrejo": return "/AssetsSpace/Cangrejo-oceano.png";
                case "Calamar":  return "/AssetsSpace/Caracol-oceano.png";
            }
        }

        // Original
        switch (tipo) {
            case "Pulpo":    return "/AssetsSpace/red.png";
            case "Cangrejo": return "/AssetsSpace/yellow.png";
            case "Calamar":  return "/AssetsSpace/green.png";
        }

        return "/AssetsSpace/red.png"; // respaldo obligatorio
    }

    private String obtenerRutaImagenNave() {
        //if (tipo == null) return "/AssetsSpace/player.png";
        if ("Alternativa".equals(skinNave)) {
            return "/AssetsSpace/nuevaNave.png";
        }else if ("Oceano".equals(skinNave)){
            return "/AssetsSpace/Canion-oceano.png";
        }
        return "/AssetsSpace/player.png";
    }

    private void disparoEnemigoAleatorio() {
        if (enemigos.isEmpty()) return;
        Enemigo e = enemigos.get((int)(Math.random() * enemigos.size()));
        proyectiles.add(new Proyectil(e.getX() + e.getAncho() / 2.0, e.getY() + e.getAlto(), false));
    }

    private Properties cargarProperties() {
        Properties p = new Properties();
        try (InputStream in = new FileInputStream("spaceinvaders.properties")) {
            p.load(in);
        } catch (IOException e) {
            p.setProperty("teclaIzquierda",    "LEFT");
            p.setProperty("teclaDerecha",       "RIGHT");
            p.setProperty("teclaDisparo",       "SPACE");
            p.setProperty("fondoGalaxia",       "Original");
            p.setProperty("musicaFondo",        "space-invaders.wav");
            p.setProperty("vidas",              "3");
            p.setProperty("sonido",             "true");
            p.setProperty("pantallaCompleta",   "false");
            p.setProperty("velocidadInvasores", "Media");
            p.setProperty("skinInvasores",      "Original");
            p.setProperty("skinNave",           "Original");
        }
        return p;
    }

    private String leerPropiedadMusica() {
        Properties p = new Properties();
        try (InputStream in = new FileInputStream("spaceinvaders.properties")) {
            p.load(in);
        } catch (IOException ignored) {}
        return p.getProperty("musicaFondo", "space-invaders.wav");
    }

    @Override
    public void gameShutdown() {
        clasesCompartidas.Musica.detenerMusicaFondo();
        if (ranking != null) {
            ranking.cerrar();
        }
    }
}