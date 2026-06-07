package pong;

import clasesCompartidas.Configuracion;
import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class MenuConfig extends Configuracion {

    // ── Componentes específicos del Pong ──────────────────────────────────────
    protected JRadioButton ventana;
    protected JRadioButton pantallaCompleta;
    protected JCheckBox    musicaBox;
    protected JComboBox<String> pistaMusical;
    protected JComboBox<String> pelota;
    protected JComboBox<String> cancha;
    protected JComboBox<String> paleta;
    protected JTextField movArriba1;
    protected JTextField movAbajo1;
    protected JTextField movArriba2;
    protected JTextField movAbajo2;

    public MenuConfig() {
        super("Configuración Pong",
              "defaultPong.properties",
              "/pong/fondoConfig.jpg");

        ventana          = crearRadio("Ventana", true);
        pantallaCompleta = crearRadio("Pantalla completa", false);
        ButtonGroup grupoVentana = new ButtonGroup();
        grupoVentana.add(ventana);
        grupoVentana.add(pantallaCompleta);

        musicaBox    = crearCheckBox(true);
        pistaMusical = crearCombo(new String[]{"retro.wav", "arcade.wav", "undertale.wav"});
        pelota       = crearCombo(new String[]{"Original", "Disco", "Planeta"});
        paleta       = crearCombo(new String[]{"Original", "Paleta azul", "Paleta roja"});
        cancha       = crearCombo(new String[]{"Original", "Cancha 1", "Cancha 2"});

        movArriba1 = crearTextField("W", 5);
        movAbajo1  = crearTextField("S", 5);
        movArriba2 = crearTextField("↑", 5);
        movAbajo2  = crearTextField("↓", 5);

        agregarSeccion("── Pantalla ──");
        agregarFilaDoble("Pantalla:", ventana, pantallaCompleta);

        agregarEspacio();
        agregarSeccion("── Música ──");
        agregarFila("Activada:", musicaBox);
        agregarFila("Pista musical:", pistaMusical);

        agregarEspacio();
        agregarSeccion("── Controles jugador 1 ──");
        agregarFila("Movimiento arriba:", movArriba1);
        agregarFila("Movimiento abajo:",  movAbajo1);

        agregarEspacio();
        agregarSeccion("── Controles jugador 2 ──");
        agregarFila("Movimiento arriba:", movArriba2);
        agregarFila("Movimiento abajo:",  movAbajo2);

        agregarEspacio();
        agregarSeccion("── Apariencia ──");
        agregarFila("Pelota:", pelota);
        agregarFila("Cancha:", cancha);
        agregarFila("Paleta:", paleta);

        construir();
    }

    // ── Método estático que Pong.java usa directamente ────────────────────────
    // Se mantiene aquí para no romper las llamadas: MenuConfig.cargarEnArchivo(...)
    public static void cargarEnArchivo(Properties props, String rutaArchivo) {
        try (FileInputStream in = new FileInputStream(rutaArchivo)) {
            props.load(in);
        } catch (Exception e) {
            System.out.println("No se pudo cargar configuración: " + rutaArchivo);
        }
    }

    // ── Implementación de los métodos abstractos ──────────────────────────────

    @Override
    protected void cargarValores() {
        boolean esCompleta = "true".equals(props.getProperty("pantallaCompleta", "false"));
        pantallaCompleta.setSelected(esCompleta);
        ventana.setSelected(!esCompleta);

        musicaBox.setSelected(!"false".equals(props.getProperty("musicaBox", "true")));

        seleccionarEnCombo(pistaMusical, props.getProperty("pistaMusical", "retro.wav"));
        seleccionarEnCombo(pelota,       props.getProperty("pelota",       "Original"));
        seleccionarEnCombo(paleta,       props.getProperty("paleta",       "Original"));
        seleccionarEnCombo(cancha,       props.getProperty("cancha",       "Original"));

        movArriba1.setText(props.getProperty("movArriba1", "W"));
        movAbajo1.setText( props.getProperty("movAbajo1",  "S"));
        movArriba2.setText(props.getProperty("movArriba2", "↑"));
        movAbajo2.setText( props.getProperty("movAbajo2",  "↓"));
    }

    @Override
    protected void guardarValores() {
        props.setProperty("pantallaCompleta", String.valueOf(pantallaCompleta.isSelected()));
        props.setProperty("ventana",          String.valueOf(ventana.isSelected()));
        props.setProperty("musicaBox",        String.valueOf(musicaBox.isSelected()));
        props.setProperty("pistaMusical",     (String) pistaMusical.getSelectedItem());
        props.setProperty("pelota",           (String) pelota.getSelectedItem());
        props.setProperty("paleta",           (String) paleta.getSelectedItem());
        props.setProperty("cancha",           (String) cancha.getSelectedItem());
        props.setProperty("movArriba1",       movArriba1.getText().trim());
        props.setProperty("movAbajo1",        movAbajo1.getText().trim());
        props.setProperty("movArriba2",       movArriba2.getText().trim());
        props.setProperty("movAbajo2",        movAbajo2.getText().trim());
    }

    @Override
    protected void restablecerDefectos() {
        ventana.setSelected(true);
        musicaBox.setSelected(true);
        pistaMusical.setSelectedIndex(0);
        pelota.setSelectedIndex(0);
        paleta.setSelectedIndex(0);
        cancha.setSelectedIndex(0);
        movArriba1.setText("W");
        movAbajo1.setText("S");
        movArriba2.setText("↑");
        movAbajo2.setText("↓");
    }
}