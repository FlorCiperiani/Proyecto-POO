package spaceinvaders;

import clasesCompartidas.Configuracion;
import javax.swing.*;

public class MenuConfigSpace extends Configuracion {

    // ── Componentes específicos de Space Invaders ─────────────────────────────
    private JRadioButton modoVentana;
    private JRadioButton modoPantalla;
    private JCheckBox    sonidoBox;
    private JComboBox<String> velocidadInvasores;
    private JComboBox<String> comboGalaxia;
    private JComboBox<String> pistaMusical;
    private JTextField movIzquierda;
    private JTextField movDerecha;
    private JTextField teclaDisparo;

    public MenuConfigSpace() {
        super("Config Space Invaders",
              "spaceinvaders.properties",
              "/AssetsSpace/Galaxia.png");

        // ── Crear componentes ─────────────────────────────────────────────────
        modoVentana  = crearRadio("Ventana", true);
        modoPantalla = crearRadio("Pantalla completa", false);
        ButtonGroup grupoPantalla = new ButtonGroup();
        grupoPantalla.add(modoVentana);
        grupoPantalla.add(modoPantalla);

        sonidoBox          = crearCheckBox(true);
        velocidadInvasores = crearCombo(new String[]{"Lenta", "Media", "Rápida"});
        comboGalaxia       = crearCombo(new String[]{"Original", "Ciudad", "Oceano"});
        pistaMusical       = crearCombo(new String[]{
            "space-invaders.wav", "undertale.wav", "retro.wav", "arcade.wav"
        });
        movIzquierda = crearTextField("LEFT");
        movDerecha   = crearTextField("RIGHT");
        teclaDisparo = crearTextField("SPACE");

        // ── Armar el panel usando los helpers de la clase base ─────────────────
        agregarSeccion("── Modo de juego ──");
        agregarFilaDoble("Pantalla:", modoVentana, modoPantalla);

        agregarEspacio();
        agregarSeccion("── Sonido ──");
        agregarFila("Activado:", sonidoBox);

        agregarEspacio();
        agregarSeccion("── Velocidad de invasores ──");
        agregarFila("Velocidad:", velocidadInvasores);

        agregarEspacio();
        agregarSeccion("── Visual ──");
        agregarFila("Fondo:", comboGalaxia);

        agregarEspacio();
        agregarSeccion("── Controles ──");
        agregarFila("Mover izquierda:", movIzquierda);
        agregarFila("Mover derecha:",   movDerecha);
        agregarFila("Disparo:",         teclaDisparo);

        agregarEspacio();
        agregarSeccion("── Pista musical ──");
        agregarFila("Música:", pistaMusical);

        // ── Finalizar: carga .properties, puebla componentes, muestra ventana ──
        construir();
    }

    // ── Implementación de los métodos abstractos ──────────────────────────────

    @Override
    protected void cargarValores() {
        boolean esCompleta = "true".equals(props.getProperty("pantallaCompleta", "false"));
        modoPantalla.setSelected(esCompleta);
        modoVentana.setSelected(!esCompleta);

        sonidoBox.setSelected(!"false".equals(props.getProperty("sonido", "true")));

        seleccionarEnCombo(velocidadInvasores, props.getProperty("velocidadInvasores", "Media"));
        seleccionarEnCombo(comboGalaxia,       props.getProperty("fondoGalaxia",       "Original"));
        seleccionarEnCombo(pistaMusical,        props.getProperty("musicaFondo",        "space-invaders.wav"));

        movIzquierda.setText(props.getProperty("teclaIzquierda", "LEFT"));
        movDerecha.setText(  props.getProperty("teclaDerecha",   "RIGHT"));
        teclaDisparo.setText(props.getProperty("teclaDisparo",   "SPACE"));
    }

    @Override
    protected void guardarValores() {
        props.setProperty("pantallaCompleta",  String.valueOf(modoPantalla.isSelected()));
        props.setProperty("sonido",            String.valueOf(sonidoBox.isSelected()));
        props.setProperty("velocidadInvasores",(String) velocidadInvasores.getSelectedItem());
        props.setProperty("fondoGalaxia",      (String) comboGalaxia.getSelectedItem());
        props.setProperty("musicaFondo",       (String) pistaMusical.getSelectedItem());
        props.setProperty("teclaIzquierda",    movIzquierda.getText().toUpperCase().trim());
        props.setProperty("teclaDerecha",      movDerecha.getText().toUpperCase().trim());
        props.setProperty("teclaDisparo",      teclaDisparo.getText().toUpperCase().trim());
    }

    @Override
    protected void restablecerDefectos() {
        modoVentana.setSelected(true);
        sonidoBox.setSelected(true);
        velocidadInvasores.setSelectedIndex(1); // Media
        comboGalaxia.setSelectedIndex(0);       // Original
        pistaMusical.setSelectedIndex(0);       // space-invaders.wav
        movIzquierda.setText("LEFT");
        movDerecha.setText("RIGHT");
        teclaDisparo.setText("SPACE");
    }
}