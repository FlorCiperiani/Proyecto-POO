package spaceinvaders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;

public class MenuConfigSpace implements ActionListener {

    private JFrame frame;

    // ── Controles ─────────────────────────────────────────────────────────────
    private JTextField movIzquierda;
    private JTextField movDerecha;
    private JTextField teclaDisparo;

    // ── Pantalla / Partida ────────────────────────────────────────────────────
    private JRadioButton modoVentana;
    private JRadioButton modoPantalla;
    private JCheckBox sonidoBox;
    private JComboBox<String> velocidadInvasores;

    // ── Visual ────────────────────────────────────────────────────────────────
    private JComboBox<String> comboGalaxia;
    private JComboBox<String> comboSkinInvasores;
    private JComboBox<String> comboSkinNave;

    // ── Proyectil (CONFIGURABLE DESDE LA VENTANA) ─────────────────────────────
    private JComboBox<String> comboTipoProyectil;
    private JComboBox<String> comboVelocidadProyectil;

    // ── Audio ─────────────────────────────────────────────────────────────────
    private JComboBox<String> pistaMusical;

    // ── Botones ───────────────────────────────────────────────────────────────
    private JButton guardar;
    private JButton reset;

    private final String rutaArchivo = "spaceinvaders.properties";

    private static final Font FONT_CONFIG = new Font("Courier New", Font.BOLD, 18);
    private static final Font FONT_SECTION = new Font("Courier New", Font.BOLD, 16);
    private static final Color COLOR_FG = Color.WHITE;
    private static final Color COLOR_BG = Color.BLACK;
    private static final Color COLOR_BORDER = Color.GRAY;
    private static final Color COLOR_SECTION = new Color(150, 200, 255);

    public MenuConfigSpace() {

        frame = new JFrame("Configuración Space Invaders");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelCompleto = new JPanel(new BorderLayout()) {
            private final Image fondo = cargarFondo();
            private Image cargarFondo() {
                try {
                    return new ImageIcon(getClass()
                            .getResource("/AssetsSpace/Galaxia.png")).getImage();
                } catch (Exception e) { return null; }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null)
                    g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
                else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // ── Componentes ───────────────────────────────────────────────────────
        movIzquierda = crearTextField("LEFT");
        movDerecha   = crearTextField("RIGHT");
        teclaDisparo = crearTextField("SPACE");

        modoVentana  = crearRadio("Ventana", true);
        modoPantalla = crearRadio("Pantalla completa", false);
        ButtonGroup grupoPantalla = new ButtonGroup();
        grupoPantalla.add(modoVentana);
        grupoPantalla.add(modoPantalla);

        sonidoBox = new JCheckBox("", true);
        sonidoBox.setFont(FONT_CONFIG);
        sonidoBox.setForeground(COLOR_FG);
        sonidoBox.setBackground(COLOR_BG);

        velocidadInvasores = crearCombo(new String[]{"Lenta", "Media", "Rápida"});
        comboGalaxia       = crearCombo(new String[]{"Original", "Ciudad", "Oceano"});
        comboSkinInvasores = crearCombo(new String[]{"Original", "Alternativa"});
        comboSkinNave      = crearCombo(new String[]{"Original", "Alternativa"});

        // 🚀 PROYECTIL CONFIGURABLE DESDE LA VENTANA
        comboTipoProyectil = crearCombo(new String[]{"Original", "Laser", "Plasma"});
        comboVelocidadProyectil = crearCombo(new String[]{"Lenta", "Media", "Rápida"});

        pistaMusical = crearCombo(new String[]{
                "space-invaders.wav", "retro.wav", "arcade.wav"
        });

        guardar = crearBoton("Guardar");
        reset   = crearBoton("Restablecer");
        guardar.addActionListener(this);
        reset.addActionListener(this);

        JPanel config = new JPanel(new GridBagLayout());
        config.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        int f = 0;

        seccion(config, gbc, "── Pantalla ──", f++);
        label(config, gbc, "Modo:", 0, f);
        comp(config, gbc, modoVentana, 1, f);
        comp(config, gbc, modoPantalla, 2, f++);

        seccion(config, gbc, "── Sonido ──", f++);
        label(config, gbc, "Activado:", 0, f);
        comp(config, gbc, sonidoBox, 1, f++);

        seccion(config, gbc, "── Visual ──", f++);
        label(config, gbc, "Fondo:", 0, f); comp(config, gbc, comboGalaxia, 1, f++);
        label(config, gbc, "Nave:", 0, f); comp(config, gbc, comboSkinNave, 1, f++);
        label(config, gbc, "Invasores:", 0, f); comp(config, gbc, comboSkinInvasores, 1, f++);

        // 🔥 SECCIÓN PROYECTIL (VISIBLE Y MODIFICABLE)
        seccion(config, gbc, "── Proyectil ──", f++);
        label(config, gbc, "Tipo:", 0, f); comp(config, gbc, comboTipoProyectil, 1, f++);
        label(config, gbc, "Velocidad:", 0, f); comp(config, gbc, comboVelocidadProyectil, 1, f++);
        label(config, gbc, "Fondo:", 0, f);
        comp(config, gbc, comboGalaxia, 1, f++);
        label(config, gbc, "Invasores:", 0, f);
        comp(config, gbc, comboSkinInvasores, 1, f++);
        label(config, gbc, "Nave:", 0, f);
        comp(config, gbc, comboSkinNave, 1, f++);

        seccion(config, gbc, "── Controles ──", f++);
        label(config, gbc, "Izquierda:", 0, f); comp(config, gbc, movIzquierda, 1, f++);
        label(config, gbc, "Derecha:", 0, f); comp(config, gbc, movDerecha, 1, f++);
        label(config, gbc, "Disparo:", 0, f); comp(config, gbc, teclaDisparo, 1, f++);

        seccion(config, gbc, "── Música ──", f++);
        label(config, gbc, "Pista:", 0, f); comp(config, gbc, pistaMusical, 1, f++);

        JScrollPane scroll = new JScrollPane(config);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        JPanel botones = new JPanel();
        botones.setOpaque(false);
        botones.add(guardar);
        botones.add(reset);

        panelCompleto.add(scroll, BorderLayout.CENTER);
        panelCompleto.add(botones, BorderLayout.SOUTH);
        frame.add(panelCompleto);

        cargarConfiguracion();
        frame.setVisible(true);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private JTextField crearTextField(String def) {
        JTextField tf = new JTextField(def, 8);
        tf.setFont(FONT_CONFIG);
        tf.setForeground(COLOR_FG);
        tf.setBackground(COLOR_BG);
        tf.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 2));
        return tf;
    }

    private JComboBox<String> crearCombo(String[] opciones) {
        JComboBox<String> cb = new JComboBox<>(opciones);
        cb.setFont(FONT_CONFIG);
        return cb;
    }

    private JRadioButton crearRadio(String txt, boolean sel) {
        JRadioButton rb = new JRadioButton(txt, sel);
        rb.setFont(FONT_CONFIG);
        rb.setOpaque(false);
        return rb;
    }

    private JButton crearBoton(String txt) {
        JButton b = new JButton(txt);
        b.setFont(FONT_CONFIG);
        return b;
    }

    private void seccion(JPanel p, GridBagConstraints gbc, String t, int f) {
        gbc.gridx = 0; gbc.gridy = f; gbc.gridwidth = 3;
        JLabel l = new JLabel(t);
        l.setFont(FONT_SECTION);
        l.setForeground(COLOR_SECTION);
        p.add(l, gbc);
        gbc.gridwidth = 1;
    }

    private void label(JPanel p, GridBagConstraints gbc, String t, int x, int y) {
        gbc.gridx = x; gbc.gridy = y;
        JLabel l = new JLabel(t);
        l.setFont(FONT_CONFIG);
        l.setForeground(COLOR_FG);
        p.add(l, gbc);
    }

    private void comp(JPanel p, GridBagConstraints gbc, JComponent c, int x, int y) {
        gbc.gridx = x; gbc.gridy = y;
        p.add(c, gbc);
    }

    // ── Configuración ─────────────────────────────────────────────────────────

    private void cargarConfiguracion() {
        Properties p = new Properties();
        try (InputStream in = new FileInputStream(rutaArchivo)) {
            p.load(in);
        } catch (IOException ignored) {}

        movIzquierda.setText(p.getProperty("teclaIzquierda", "LEFT"));
        movDerecha.setText(p.getProperty("teclaDerecha", "RIGHT"));
        teclaDisparo.setText(p.getProperty("teclaDisparo", "SPACE"));

        comboTipoProyectil.setSelectedItem(p.getProperty("tipoProyectil", "Original"));
        comboVelocidadProyectil.setSelectedItem(p.getProperty("velocidadProyectil", "Media"));
        boolean esCompleta = "true".equals(p.getProperty("pantallaCompleta", "false"));
        modoPantalla.setSelected(esCompleta);
        modoVentana.setSelected(!esCompleta);

        sonidoBox.setSelected(!"false".equals(p.getProperty("sonido", "true")));

        seleccionar(velocidadInvasores, p.getProperty("velocidadInvasores", "Media"));
        seleccionar(comboGalaxia,       p.getProperty("fondoGalaxia",       "Original"));
        seleccionar(comboSkinInvasores, p.getProperty("skinInvasores",      "Original"));
        seleccionar(comboSkinNave,      p.getProperty("skinNave",           "Original"));
        seleccionar(pistaMusical,       p.getProperty("musicaFondo",        "space-invaders.wav"));
    }

    private void seleccionar(JComboBox<String> cb, String valor) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).equals(valor)) { cb.setSelectedIndex(i); return; }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == reset) {
            comboTipoProyectil.setSelectedIndex(0);
            comboVelocidadProyectil.setSelectedIndex(1);
            movIzquierda.setText("LEFT");
            movDerecha.setText("RIGHT");
            teclaDisparo.setText("SPACE");
            modoVentana.setSelected(true);
            sonidoBox.setSelected(true);
            velocidadInvasores.setSelectedIndex(1); // Media
            comboGalaxia.setSelectedIndex(0);       // Original
            comboSkinInvasores.setSelectedIndex(0); // Original
            comboSkinNave.setSelectedIndex(0);      // Original
            pistaMusical.setSelectedIndex(0);        // space-invaders.wav
            return;
        }

        Properties p = new Properties();
        p.setProperty("tipoProyectil", (String) comboTipoProyectil.getSelectedItem());
        p.setProperty("velocidadProyectil", (String) comboVelocidadProyectil.getSelectedItem());
        p.setProperty("teclaIzquierda",    movIzquierda.getText().toUpperCase().trim());
        p.setProperty("teclaDerecha",      movDerecha.getText().toUpperCase().trim());
        p.setProperty("teclaDisparo",      teclaDisparo.getText().toUpperCase().trim());
        p.setProperty("pantallaCompleta",  String.valueOf(modoPantalla.isSelected()));
        p.setProperty("sonido",            String.valueOf(sonidoBox.isSelected()));
        p.setProperty("velocidadInvasores",(String) velocidadInvasores.getSelectedItem());
        p.setProperty("fondoGalaxia",      (String) comboGalaxia.getSelectedItem());
        p.setProperty("skinInvasores",     (String) comboSkinInvasores.getSelectedItem());
        p.setProperty("skinNave",          (String) comboSkinNave.getSelectedItem());
        p.setProperty("musicaFondo",       (String) pistaMusical.getSelectedItem());

        try (OutputStream out = new FileOutputStream(rutaArchivo)) {
            p.store(out, "Config Space Invaders");
            JOptionPane.showMessageDialog(frame, "Configuración guardada");
            frame.dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error al guardar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}