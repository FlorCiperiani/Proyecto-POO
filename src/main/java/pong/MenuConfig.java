package pong;

import clasesCompartidas.Configuracion;
import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class MenuConfig extends Configuracion {

    // ── Componentes Pong ──────────────────────────────────────
    protected JCheckBox    musicaBox;
    protected JComboBox<String> pistaMusical;
    protected JComboBox<String> pelota;
    protected JComboBox<String> cancha;
    protected JComboBox<String> paleta;

    public MenuConfig() {
        super("Configuración Pong",
              "defaultPong.properties",
              "/pong/fondoConfig.jpg");

        musicaBox    = crearCheckBox(true);
        pistaMusical = crearCombo(new String[]{"retro.wav", "arcade.wav", "undertale.wav"});
        pelota       = crearCombo(new String[]{"Original", "Disco", "Planeta"});
        paleta       = crearCombo(new String[]{"Original", "Paleta azul", "Paleta roja"});
        cancha       = crearCombo(new String[]{"Original", "Cancha 1", "Cancha 2"});

        agregarSeccion("── Música ──");
        agregarFila("Activada:", musicaBox);
        agregarFila("Pista musical:", pistaMusical);

        agregarEspacio();
        agregarSeccion("── Apariencia ──");
        agregarFila("Pelota:", pelota);
        agregarFila("Cancha:", cancha);
        agregarFila("Paleta:", paleta);

        construir();
    }

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
        musicaBox.setSelected(!"false".equals(props.getProperty("musicaBox", "true")));

        seleccionarEnCombo(pistaMusical, props.getProperty("pistaMusical", "retro.wav"));
        seleccionarEnCombo(pelota,       props.getProperty("pelota",       "Original"));
        seleccionarEnCombo(paleta,       props.getProperty("paleta",       "Original"));
        seleccionarEnCombo(cancha,       props.getProperty("cancha",       "Original"));
    }

    @Override
    protected void guardarValores() {
        props.setProperty("musicaBox",        String.valueOf(musicaBox.isSelected()));
        props.setProperty("pistaMusical",     (String) pistaMusical.getSelectedItem());
        props.setProperty("pelota",           (String) pelota.getSelectedItem());
        props.setProperty("paleta",           (String) paleta.getSelectedItem());
        props.setProperty("cancha",           (String) cancha.getSelectedItem());
    }

    @Override
    protected void restablecerDefectos() {
        musicaBox.setSelected(true);
        pistaMusical.setSelectedIndex(0);
        pelota.setSelectedIndex(0);
        paleta.setSelectedIndex(0);
        cancha.setSelectedIndex(0);
    }
}
