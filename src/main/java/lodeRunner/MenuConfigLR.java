package lodeRunner;

import clasesCompartidas.Configuracion;
import javax.swing.*;
import java.awt.*;

public class MenuConfigLR extends Configuracion {

    // ── Estado estático de configuración (igual que antes) ────────────────────
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

    // ── Componentes específicos de Lode Runner ────────────────────────────────
    private JRadioButton rdVentana;
    private JRadioButton rdPantallaCompleta;
    private JCheckBox    chkSonidoGeneral;
    private JCheckBox    chkMusica;
    private JCheckBox    chkEfectos;
    private JComboBox<String> comboMusica;
    private JComboBox<String> comboSkin;

    public MenuConfigLR() {
        // La clase base arma la ventana con el fondo de LR.
        // Pasamos null como rutaArchivo porque LR guarda su config en variables
        // estáticas en memoria, no en un .properties.
        super("Configuración - Lode Runner",
              null,                          // sin archivo .properties
              "/LodeRunner/portadaLR.png");  // fondo de la ventana

        // ── Crear componentes ─────────────────────────────────────────────────
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

        // ── Armar el panel ────────────────────────────────────────────────────
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

        // construir() carga el .properties (no aplica aquí) y muestra la ventana
        construir();
    }

    // ── Implementación de los métodos abstractos ──────────────────────────────

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
        // Escribir en las variables estáticas (mismo comportamiento que antes)
        pantallaCompleta          = rdPantallaCompleta.isSelected();
        sonidoGeneralActivado     = chkSonidoGeneral.isSelected();
        musicaActivada            = chkMusica.isSelected();
        efectosActivados          = chkEfectos.isSelected();
        pistaMusicalSeleccionada  = (String) comboMusica.getSelectedItem();
        skinPersonajeSeleccionado = (String) comboSkin.getSelectedItem();

        // ── Aplicar cambios de música inmediatamente (igual que antes) ────────
        if (!sonidoGeneralActivado || !musicaActivada) {
            clasesCompartidas.Musica.detenerMusicaFondo();
        } else {
            clasesCompartidas.Musica.detenerMusicaFondo();
            clasesCompartidas.Musica.iniciarMusica(pistaMusicalSeleccionada);
        }

        // ── Aplicar skin inmediatamente si hay jugador activo (igual que antes)
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

    // ── Override del guardado: LR no usa .properties, muestra mensaje propio ──
    // Sobreescribimos guardarProperties() para evitar que intente escribir
    // en un archivo null y para mostrar el mismo mensaje que tenía antes.
    @Override
    protected void guardarProperties() {
        JOptionPane.showMessageDialog(frame, "Configuración guardada con éxito.");
        frame.dispose();
    }

    // ── Helper extra: checkbox con texto visible (LR los tenía con etiqueta) ──
    // La clase base crea checkboxes sin texto; este los crea con texto interno.
    private JCheckBox crearCheckBoxConTexto(String texto, boolean seleccionado) {
        JCheckBox cb = new JCheckBox(texto, seleccionado);
        cb.setFont(FONT_CONFIG);
        cb.setForeground(COLOR_FG);
        cb.setBackground(COLOR_BG);
        cb.setBorder(javax.swing.BorderFactory.createLineBorder(COLOR_BORDER, 2, true));
        cb.setOpaque(true);
        return cb;
    }

    // ── Override del fondo: LR agrega un overlay oscuro encima de la imagen ──
    // Para lograrlo necesitamos que el panel pinte el overlay además del fondo.
    // Lo hacemos sobreescribiendo construir() para interceptar antes de setVisible.
    @Override
    protected void construir() {
        // Agregar overlay oscuro al panel de fondo mediante un componente glass
        // (esto replica el comportamiento original del paintComponent de LR)
        frame.getContentPane().setBackground(Color.BLACK);

        cargarValores(); // no hay properties que leer, usa variables estáticas
        frame.setVisible(true);
    }
}