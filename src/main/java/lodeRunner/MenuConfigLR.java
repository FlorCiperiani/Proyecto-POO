package lodeRunner;

import clasesCompartidas.Configuracion;
import javax.swing.*;
import java.awt.*;

public class MenuConfigLR extends Configuracion {

    public static boolean pantallaCompleta        = false;
    public static boolean sonidoGeneralAtrapado   = true;
    public static boolean musicaActivada          = true;
    public static boolean efectosActivados        = true;
    public static boolean sonidoGeneralActivado   = true;

    public static String pistaMusicalSeleccionada  = "LR_musiquilla.wav";
    public static String skinPersonajeSeleccionado = "original";

    public static float volumenMusica  = 0.3f;
    public static float volumenEfectos = 0.3f;

    public static void resetConfig() {
        pantallaCompleta          = false;
        sonidoGeneralAtrapado     = true;
        musicaActivada            = true;
        efectosActivados          = true;
        sonidoGeneralActivado     = true;
        pistaMusicalSeleccionada  = "LR_musiquilla.wav";
        skinPersonajeSeleccionado = "original";
        volumenMusica             = 0.3f;
        volumenEfectos            = 0.3f;
    }

    private JRadioButton rdVentana;
    private JRadioButton rdPantallaCompleta;
    private JCheckBox    chkSonidoGeneral;
    private JCheckBox    chkMusica;
    private JCheckBox    chkEfectos;
    private JComboBox<String> comboMusica;
    private JComboBox<String> comboSkin;

    public MenuConfigLR() {

        super("Configuración - Lode Runner",
              null,
              "/LodeRunner/portadaLR.png");

        rdVentana          = crearRadio("Ventana",          !pantallaCompleta);
        rdPantallaCompleta = crearRadio("Pantalla completa", pantallaCompleta);
        ButtonGroup grupoPantalla = new ButtonGroup();
        grupoPantalla.add(rdVentana);
        grupoPantalla.add(rdPantallaCompleta);

        chkSonidoGeneral = crearCheckBoxConTexto("Activado", sonidoGeneralActivado);
        chkMusica        = crearCheckBoxConTexto("Activado", musicaActivada);
        chkEfectos       = crearCheckBoxConTexto("Activado", efectosActivados);

        comboMusica = crearCombo(new String[]{"LR_musiquilla.wav", "retro.wav"});
        comboSkin   = crearCombo(new String[]{"original", "skin_alternativa"});

        agregarSeccion("── Pantalla ──");
        agregarFilaDoble("Pantalla:", rdVentana, rdPantallaCompleta);

        agregarEspacio();
        agregarSeccion("── Sonido ──");
        agregarFila("Sonido General:",    chkSonidoGeneral);
        agregarFila("Música de Fondo:",   chkMusica);
        agregarFila("Efectos de Sonido:", chkEfectos);
        agregarFila("Pista Musical:",     comboMusica);

        agregarEspacio();
        agregarSeccion("── Apariencia ──");
        agregarFila("Skin del Personaje:", comboSkin);

        construir();
    }


    @Override
    protected void cargarValores() {
        // LR no usa .properties: leemos directo de las variables estáticas
        rdVentana.setSelected(!pantallaCompleta);
        rdPantallaCompleta.setSelected(pantallaCompleta);
        chkSonidoGeneral.setSelected(sonidoGeneralActivado);
        chkMusica.setSelected(musicaActivada);
        chkEfectos.setSelected(efectosActivados);
        seleccionarEnCombo(comboMusica, pistaMusicalSeleccionada);
        seleccionarEnCombo(comboSkin,   skinPersonajeSeleccionado);
    }

    @Override
    protected void guardarValores() {
        pantallaCompleta          = rdPantallaCompleta.isSelected();
        sonidoGeneralActivado     = chkSonidoGeneral.isSelected();
        musicaActivada            = chkMusica.isSelected();
        efectosActivados          = chkEfectos.isSelected();
        pistaMusicalSeleccionada  = (String) comboMusica.getSelectedItem();
        skinPersonajeSeleccionado = (String) comboSkin.getSelectedItem();

        if (!sonidoGeneralActivado || !musicaActivada) {
            clasesCompartidas.Musica.detenerMusicaFondo();
        } else {
            clasesCompartidas.Musica.detenerMusicaFondo();
            clasesCompartidas.Musica.iniciarMusica(pistaMusicalSeleccionada);
        }


        if (LodeRunner.jugadorActual != null) {
            LodeRunner.jugadorActual.recargarSprites();
        }
    }

    @Override
    protected void restablecerDefectos() {
        resetConfig(); // llama al método estático que ya existía
        // Refrescar los componentes con los valores reseteados
        rdVentana.setSelected(!pantallaCompleta);
        rdPantallaCompleta.setSelected(pantallaCompleta);
        chkSonidoGeneral.setSelected(sonidoGeneralActivado);
        chkMusica.setSelected(musicaActivada);
        chkEfectos.setSelected(efectosActivados);
        seleccionarEnCombo(comboMusica, pistaMusicalSeleccionada);
        seleccionarEnCombo(comboSkin,   skinPersonajeSeleccionado);
    }

    @Override
    protected void guardarProperties() {
        JOptionPane.showMessageDialog(frame, "Configuración guardada con éxito.");
        frame.dispose();
    }

    private JCheckBox crearCheckBoxConTexto(String texto, boolean seleccionado) {
        JCheckBox cb = new JCheckBox(texto, seleccionado);
        cb.setFont(FONT_CONFIG);
        cb.setForeground(COLOR_FG);
        cb.setBackground(COLOR_BG);
        cb.setBorder(javax.swing.BorderFactory.createLineBorder(COLOR_BORDER, 2, true));
        cb.setOpaque(true);
        return cb;
    }

    @Override
    protected void construir() {
        frame.getContentPane().setBackground(Color.BLACK);

        cargarValores(); // no hay properties que leer, usa variables estáticas
        frame.setVisible(true);
    }
}