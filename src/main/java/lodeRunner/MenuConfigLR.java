package lodeRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuConfigLR extends JFrame {

    // ── Componentes ────────────────────────────────────────────────────────
    private JRadioButton rdVentana;
    private JRadioButton rdPantallaCompleta;
    private JCheckBox    chkSonidoGeneral;
    private JCheckBox    chkMusica;
    private JCheckBox    chkEfectos;
    private JComboBox<String> comboMusica;
    private JComboBox<String> comboSkin;
    private JButton      btnGuardar;
    private JButton      btnReset;

    // Fuente y colores al estilo Pong
    private static final Font  FONT_LR  = new Font("Courier New", Font.BOLD, 16);
    private static final Color FG_COLOR = Color.WHITE;
    private static final Color BG_COLOR = Color.BLACK;

    public MenuConfigLR() {
        setTitle("Configuración - Lode Runner");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ── Panel principal con imagen de fondo ────────────────────────
        JPanel panelCompleto = new JPanel(new BorderLayout()) {
            private final Image fondo = new ImageIcon(
                getClass().getResource("/LodeRunner/portada_loderunner.png")).getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) {
                    // Oscurecer un poco para que el texto sea legible
                    g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(0, 0, 0, 160));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // ── Panel formulario con GridBagLayout (igual que Pong) ────────
        JPanel config = new JPanel(new GridBagLayout());
        config.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 10, 6, 10);

        // 1. Pantalla
        rdVentana         = makeRadioButton("Ventana",          !Configuracion.pantallaCompleta);
        rdPantallaCompleta = makeRadioButton("Pantalla completa", Configuracion.pantallaCompleta);
        ButtonGroup grupoPantalla = new ButtonGroup();
        grupoPantalla.add(rdVentana);
        grupoPantalla.add(rdPantallaCompleta);

        addRow(config, gbc, 0, "Pantalla:", rdVentana, rdPantallaCompleta);

        // 2. Sonido general
        chkSonidoGeneral = makeCheckBox("Activado", Configuracion.sonidoGeneralActivado);
        addRow(config, gbc, 1, "Sonido General:", chkSonidoGeneral, null);

        // 3. Música
        chkMusica = makeCheckBox("Activado", Configuracion.musicaActivada);
        addRow(config, gbc, 2, "Música de Fondo:", chkMusica, null);

        // 4. Efectos
        chkEfectos = makeCheckBox("Activado", Configuracion.efectosActivados);
        addRow(config, gbc, 3, "Efectos de Sonido:", chkEfectos, null);

        // 7. Pista musical
        String[] pistas = {"LR_musiquilla.wav", "retro.wav"};
        comboMusica = makeCombo(pistas, Configuracion.pistaMusicalSeleccionada);
        addRow(config, gbc, 6, "Pista Musical:", comboMusica, null);

        // 8. Skin
        String[] skins = {"original", "clasico_8bit", "moderno"};
        comboSkin = makeCombo(skins, Configuracion.skinPersonajeSeleccionado);
        addRow(config, gbc, 7, "Skin del Personaje:", comboSkin, null);

        // ── Panel botones ──────────────────────────────────────────────
        btnGuardar = makeButton("Guardar");
        btnReset   = makeButton("Restablecer");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setOpaque(false);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnReset);

        panelCompleto.add(config, BorderLayout.CENTER);
        panelCompleto.add(panelBotones, BorderLayout.SOUTH);
        add(panelCompleto);

        // ── Listeners ──────────────────────────────────────────────────
        btnGuardar.addActionListener(e -> guardar());
        btnReset.addActionListener(e -> reset());

        setVisible(true);
    }

    // ── Lógica ────────────────────────────────────────────────────────────

    private void guardar() {
        Configuracion.pantallaCompleta      = rdPantallaCompleta.isSelected();
        Configuracion.sonidoGeneralActivado = chkSonidoGeneral.isSelected();
        Configuracion.musicaActivada        = chkMusica.isSelected();
        Configuracion.efectosActivados      = chkEfectos.isSelected();
        Configuracion.pistaMusicalSeleccionada  = (String) comboMusica.getSelectedItem();
        Configuracion.skinPersonajeSeleccionado = (String) comboSkin.getSelectedItem();

        // Aplicar cambios de música inmediatamente
        if (!Configuracion.sonidoGeneralActivado || !Configuracion.musicaActivada) {
            clasesCompartidas.Musica.detenerMusicaFondo();
        } else {
            clasesCompartidas.Musica.detenerMusicaFondo();
            clasesCompartidas.Musica.iniciarMusica(Configuracion.pistaMusicalSeleccionada);
        }

        JOptionPane.showMessageDialog(this, "Configuración guardada con éxito.");
        dispose();
    }

    private void reset() {
        Configuracion.reset();

        rdVentana.setSelected(!Configuracion.pantallaCompleta);
        rdPantallaCompleta.setSelected(Configuracion.pantallaCompleta);
        chkSonidoGeneral.setSelected(Configuracion.sonidoGeneralActivado);
        chkMusica.setSelected(Configuracion.musicaActivada);
        chkEfectos.setSelected(Configuracion.efectosActivados);
        comboMusica.setSelectedItem(Configuracion.pistaMusicalSeleccionada);
        comboSkin.setSelectedItem(Configuracion.skinPersonajeSeleccionado);

        JOptionPane.showMessageDialog(this, "Se han restablecido los valores por defecto.");
    }

    // ── Helpers de construcción ───────────────────────────────────────────

    /** Agrega una fila con label + hasta 2 componentes al GridBag */
    private void addRow(JPanel panel, GridBagConstraints g, int row,
                        String labelText, JComponent c1, JComponent c2) {
        g.gridx = 0; g.gridy = row;
        panel.add(makeLabel(labelText), g);
        g.gridx = 1;
        panel.add(c1, g);
        if (c2 != null) { g.gridx = 2; panel.add(c2, g); }
    }

    /** Agrega una fila de slider: label | slider (ancho) | porcentaje */
    private void addSliderRow(JPanel panel, GridBagConstraints g, int row,
                              String labelText, JSlider slider, JLabel pct) {
        g.gridx = 0; g.gridy = row;
        panel.add(makeLabel(labelText), g);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        panel.add(slider, g);
        g.gridx = 2; g.fill = GridBagConstraints.NONE; g.weightx = 0;
        panel.add(pct, g);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LR);
        lbl.setForeground(FG_COLOR);
        return lbl;
    }

    private JRadioButton makeRadioButton(String text, boolean selected) {
        JRadioButton rb = new JRadioButton(text, selected);
        rb.setFont(FONT_LR);
        rb.setForeground(FG_COLOR);
        rb.setBackground(BG_COLOR);
        rb.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        rb.setOpaque(true);
        return rb;
    }

    private JCheckBox makeCheckBox(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setFont(FONT_LR);
        cb.setForeground(FG_COLOR);
        cb.setBackground(BG_COLOR);
        cb.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        cb.setOpaque(true);
        return cb;
    }

    private JComboBox<String> makeCombo(String[] items, String selected) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setSelectedItem(selected);
        cb.setFont(FONT_LR);
        cb.setForeground(FG_COLOR);
        cb.setBackground(BG_COLOR);
        cb.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        return cb;
    }

    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_LR);
        btn.setForeground(FG_COLOR);
        btn.setBackground(BG_COLOR);
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));
        return btn;
    }
}
