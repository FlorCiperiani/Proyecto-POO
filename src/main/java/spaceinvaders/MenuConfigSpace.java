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

    // ── Partida ───────────────────────────────────────────────────────────────
    private JRadioButton modoVentana;
    private JRadioButton modoPantalla;
    private JCheckBox    sonidoBox;
    private JComboBox<String> velocidadInvasores;

    // ── Visual ────────────────────────────────────────────────────────────────
    private JComboBox<String> comboGalaxia;   // fondo de pantalla

    // ── Audio ─────────────────────────────────────────────────────────────────
    private JComboBox<String> pistaMusical;

    // ── Botones ───────────────────────────────────────────────────────────────
    private JButton guardar;
    private JButton reset;

    private final String rutaArchivo = "spaceinvaders.properties";

    private static final Font  FONT_CONFIG   = new Font("Courier New", Font.BOLD, 18);
    private static final Font  FONT_SECTION  = new Font("Courier New", Font.BOLD, 16);
    private static final Color COLOR_FG      = Color.WHITE;
    private static final Color COLOR_BG      = Color.BLACK;
    private static final Color COLOR_BORDER  = Color.GRAY;
    private static final Color COLOR_SECTION = new Color(150, 200, 255);

    public MenuConfigSpace() {

        frame = new JFrame("Config Space Invaders");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // ── Panel con fondo de galaxia ────────────────────────────────────────
        JPanel panelCompleto = new JPanel(new BorderLayout()) {
            private final Image fondo = cargarFondo();
            private Image cargarFondo() {
                try {
                    java.net.URL url = getClass().getResource("/AssetsSpace/Galaxia.png");
                    return url != null ? new ImageIcon(url).getImage() : null;
                } catch (Exception e) { return null; }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
                else { g.setColor(Color.BLACK); g.fillRect(0, 0, getWidth(), getHeight()); }
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
        sonidoBox.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 3, true));

        velocidadInvasores = crearCombo(new String[]{"Lenta", "Media", "Rápida"});
        comboGalaxia       = crearCombo(new String[]{"Original", "Ciudad", "Oceano"});
        pistaMusical       = crearCombo(new String[]{
            "space-invaders.wav", "undertale.wav", "retro.wav", "arcade.wav"
        });

        guardar = crearBoton("Guardar");
        guardar.addActionListener(this);
        reset   = crearBoton("Restablecer");
        reset.addActionListener(this);

        // ── Layout ────────────────────────────────────────────────────────────
        JPanel config = new JPanel(new GridBagLayout());
        config.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 8, 4, 8);
        int f = 0;

        // — Modo de juego —
        seccion(config, gbc, "── Modo de juego ──", f++);
        label(config, gbc, "Pantalla:", 0, f);
        gbc.gridx = 1; gbc.gridy = f; config.add(modoVentana, gbc);
        gbc.gridx = 2; gbc.gridy = f; config.add(modoPantalla, gbc);
        f++;

        // — Sonido —
        espacio(config, gbc, f++);
        seccion(config, gbc, "── Sonido ──", f++);
        label(config, gbc, "Activado:", 0, f);
        gbc.gridx = 1; gbc.gridy = f++; config.add(sonidoBox, gbc);

        // — Velocidad —
        espacio(config, gbc, f++);
        seccion(config, gbc, "── Velocidad de invasores ──", f++);
        label(config, gbc, "Velocidad:", 0, f);
        comp(config, gbc, velocidadInvasores, 1, f++);

        // — Visual —
        espacio(config, gbc, f++);
        seccion(config, gbc, "── Visual ──", f++);
        label(config, gbc, "Fondo:", 0, f);
        comp(config, gbc, comboGalaxia, 1, f++);

        // — Controles —
        espacio(config, gbc, f++);
        seccion(config, gbc, "── Controles ──", f++);
        label(config, gbc, "Mover izquierda:", 0, f); comp(config, gbc, movIzquierda, 1, f++);
        label(config, gbc, "Mover derecha:",   0, f); comp(config, gbc, movDerecha,   1, f++);
        label(config, gbc, "Disparo:",         0, f); comp(config, gbc, teclaDisparo, 1, f++);

        // — Música —
        espacio(config, gbc, f++);
        seccion(config, gbc, "── Pista musical ──", f++);
        label(config, gbc, "Música:", 0, f); comp(config, gbc, pistaMusical, 1, f++);

        // ── Scroll ────────────────────────────────────────────────────────────
        JScrollPane scroll = new JScrollPane(config);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // ── Botones ───────────────────────────────────────────────────────────
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.add(guardar);
        panelBotones.add(reset);

        panelCompleto.add(scroll,       BorderLayout.CENTER);
        panelCompleto.add(panelBotones, BorderLayout.SOUTH);
        frame.add(panelCompleto);

        cargarConfiguracion();
        frame.setVisible(true);
    }

    // ── Helpers de layout ─────────────────────────────────────────────────────

    private void seccion(JPanel p, GridBagConstraints gbc, String txt, int f) {
        gbc.gridx = 0; gbc.gridy = f; gbc.gridwidth = 3;
        JLabel l = new JLabel(txt);
        l.setFont(FONT_SECTION);
        l.setForeground(COLOR_SECTION);
        p.add(l, gbc);
        gbc.gridwidth = 1;
    }

    private void espacio(JPanel p, GridBagConstraints gbc, int f) {
        gbc.gridx = 0; gbc.gridy = f; gbc.gridwidth = 3;
        p.add(new JLabel(" "), gbc);
        gbc.gridwidth = 1;
    }

    private void label(JPanel p, GridBagConstraints gbc, String txt, int x, int y) {
        gbc.gridx = x; gbc.gridy = y;
        JLabel l = new JLabel(txt);
        l.setFont(FONT_CONFIG);
        l.setForeground(COLOR_FG);
        p.add(l, gbc);
    }

    private void comp(JPanel p, GridBagConstraints gbc, JComponent c, int x, int y) {
        gbc.gridx = x; gbc.gridy = y;
        p.add(c, gbc);
    }

    // ── Helpers de creación ───────────────────────────────────────────────────

    private JTextField crearTextField(String def) {
        JTextField tf = new JTextField(def, 8);
        tf.setFont(FONT_CONFIG);
        tf.setForeground(COLOR_FG);
        tf.setBackground(COLOR_BG);
        tf.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 3, true));
        return tf;
    }

    private JComboBox<String> crearCombo(String[] opciones) {
        JComboBox<String> cb = new JComboBox<>(opciones);
        cb.setFont(FONT_CONFIG);
        cb.setForeground(COLOR_FG);
        cb.setBackground(COLOR_BG);
        cb.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 3, true));
        return cb;
    }

    private JRadioButton crearRadio(String txt, boolean sel) {
        JRadioButton rb = new JRadioButton(txt, sel);
        rb.setFont(FONT_CONFIG);
        rb.setForeground(COLOR_FG);
        rb.setBackground(COLOR_BG);
        rb.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 3, true));
        return rb;
    }

    private JButton crearBoton(String txt) {
        JButton btn = new JButton(txt);
        btn.setFont(FONT_CONFIG);
        btn.setForeground(COLOR_FG);
        btn.setBackground(COLOR_BG);
        btn.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 3, true));
        return btn;
    }

    // ── Carga / guardado ──────────────────────────────────────────────────────

    private void cargarConfiguracion() {
        Properties p = new Properties();
        try (InputStream in = new FileInputStream(rutaArchivo)) {
            p.load(in);
        } catch (IOException ignored) {}

        movIzquierda.setText(p.getProperty("teclaIzquierda", "LEFT"));
        movDerecha.setText(p.getProperty("teclaDerecha", "RIGHT"));
        teclaDisparo.setText(p.getProperty("teclaDisparo", "SPACE"));

        boolean esCompleta = "true".equals(p.getProperty("pantallaCompleta", "false"));
        modoPantalla.setSelected(esCompleta);
        modoVentana.setSelected(!esCompleta);

        sonidoBox.setSelected(!"false".equals(p.getProperty("sonido", "true")));

        seleccionar(velocidadInvasores, p.getProperty("velocidadInvasores", "Media"));
        seleccionar(comboGalaxia,       p.getProperty("fondoGalaxia",       "Original"));
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
            movIzquierda.setText("LEFT");
            movDerecha.setText("RIGHT");
            teclaDisparo.setText("SPACE");
            modoVentana.setSelected(true);
            sonidoBox.setSelected(true);
            velocidadInvasores.setSelectedIndex(1); // Media
            comboGalaxia.setSelectedIndex(0);       // Original
            pistaMusical.setSelectedIndex(0);        // space-invaders.wav
            return;
        }

        // ── Guardar ───────────────────────────────────────────────────────────
        Properties p = new Properties();
        p.setProperty("teclaIzquierda",    movIzquierda.getText().toUpperCase().trim());
        p.setProperty("teclaDerecha",      movDerecha.getText().toUpperCase().trim());
        p.setProperty("teclaDisparo",      teclaDisparo.getText().toUpperCase().trim());
        p.setProperty("pantallaCompleta",  String.valueOf(modoPantalla.isSelected()));
        p.setProperty("sonido",            String.valueOf(sonidoBox.isSelected()));
        p.setProperty("velocidadInvasores",(String) velocidadInvasores.getSelectedItem());
        p.setProperty("fondoGalaxia",      (String) comboGalaxia.getSelectedItem());
        p.setProperty("musicaFondo",       (String) pistaMusical.getSelectedItem());

        try (OutputStream out = new FileOutputStream(rutaArchivo)) {
            p.store(out, "Config Space Invaders");
            JOptionPane.showMessageDialog(frame, "¡Configuración guardada correctamente!");
            frame.dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame,
                "No se pudo guardar:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}