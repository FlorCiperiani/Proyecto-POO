package lodeRunner;

import javax.swing.*;
import java.awt.*;

public class MenuConfigLR extends JFrame {

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
        pantallaCompleta        = false;
        sonidoGeneralAtrapado   = true;
        musicaActivada          = true;
        efectosActivados        = true;
        sonidoGeneralActivado   = true;
        pistaMusicalSeleccionada  = "LR_musiquilla.wav";
        skinPersonajeSeleccionado = "original";
        volumenMusica  = 0.3f;
        volumenEfectos = 0.3f;
    }


    private JRadioButton rdVentana;
    private JRadioButton rdPantallaCompleta;
    private JCheckBox    chkSonidoGeneral;
    private JCheckBox    chkMusica;
    private JCheckBox    chkEfectos;
    private JComboBox<String> comboMusica;
    private JComboBox<String> comboSkin;
    private JButton      btnGuardar;
    private JButton      btnReset;

    private static final Font  FONT_LR  = new Font("Courier New", Font.BOLD, 16);
    private static final Color FG_COLOR = Color.WHITE;
    private static final Color BG_COLOR = Color.BLACK;

    public MenuConfigLR() {
        setTitle("Configuración - Lode Runner");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelCompleto = new JPanel(new BorderLayout()) {
            private final Image fondo = new ImageIcon(
                getClass().getResource("/LodeRunner/portadaLR.png")).getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) {
                    g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(0, 0, 0, 160));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        JPanel config = new JPanel(new GridBagLayout());
        config.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 10, 6, 10);

        // Pantalla
        rdVentana          = makeRadioButton("Ventana",           !pantallaCompleta);
        rdPantallaCompleta = makeRadioButton("Pantalla completa",  pantallaCompleta);
        ButtonGroup grupoPantalla = new ButtonGroup();
        grupoPantalla.add(rdVentana);
        grupoPantalla.add(rdPantallaCompleta);
        addRow(config, gbc, 0, "Pantalla:", rdVentana, rdPantallaCompleta);

        // Sonido general
        chkSonidoGeneral = makeCheckBox("Activado", sonidoGeneralActivado);
        addRow(config, gbc, 1, "Sonido General:", chkSonidoGeneral, null);

        // Música
        chkMusica = makeCheckBox("Activado", musicaActivada);
        addRow(config, gbc, 2, "Música de Fondo:", chkMusica, null);

        // Efectos
        chkEfectos = makeCheckBox("Activado", efectosActivados);
        addRow(config, gbc, 3, "Efectos de Sonido:", chkEfectos, null);

        // Pista musical
        String[] pistas = {"LR_musiquilla.wav", "retro.wav"};
        comboMusica = makeCombo(pistas, pistaMusicalSeleccionada);
        addRow(config, gbc, 4, "Pista Musical:", comboMusica, null);

        // Skin
        String[] skins = {"original", "skin_alternativa"};
        comboSkin = makeCombo(skins, skinPersonajeSeleccionado);
        addRow(config, gbc, 5, "Skin del Personaje:", comboSkin, null);

        // Botones
        btnGuardar = makeButton("Guardar");
        btnReset   = makeButton("Restablecer");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setOpaque(false);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnReset);

        panelCompleto.add(config, BorderLayout.CENTER);
        panelCompleto.add(panelBotones, BorderLayout.SOUTH);
        add(panelCompleto);

        btnGuardar.addActionListener(e -> guardar());
        btnReset.addActionListener(e -> reset());

        setVisible(true);
    }

    private void guardar() {
        pantallaCompleta          = rdPantallaCompleta.isSelected();
        sonidoGeneralActivado     = chkSonidoGeneral.isSelected();
        musicaActivada            = chkMusica.isSelected();
        efectosActivados          = chkEfectos.isSelected();
        pistaMusicalSeleccionada  = (String) comboMusica.getSelectedItem();
        skinPersonajeSeleccionado = (String) comboSkin.getSelectedItem();

        // Aplicar cambios de música
        if (!sonidoGeneralActivado || !musicaActivada) {
            clasesCompartidas.Musica.detenerMusicaFondo();
        } else {
            clasesCompartidas.Musica.detenerMusicaFondo();
            clasesCompartidas.Musica.iniciarMusica(pistaMusicalSeleccionada);
        }

        // Aplicar skin si hay jugador activo
        if (LodeRunner.jugadorActual != null) {
            LodeRunner.jugadorActual.recargarSprites();
        }

        JOptionPane.showMessageDialog(this, "Configuración guardada con éxito.");
        dispose();
    }

    private void reset() {
        resetConfig();
        rdVentana.setSelected(!pantallaCompleta);
        rdPantallaCompleta.setSelected(pantallaCompleta);
        chkSonidoGeneral.setSelected(sonidoGeneralActivado);
        chkMusica.setSelected(musicaActivada);
        chkEfectos.setSelected(efectosActivados);
        comboMusica.setSelectedItem(pistaMusicalSeleccionada);
        comboSkin.setSelectedItem(skinPersonajeSeleccionado);
        JOptionPane.showMessageDialog(this, "Se han restablecido los valores por defecto.");
    }

    private void addRow(JPanel panel, GridBagConstraints g, int row, String labelText, JComponent c1, JComponent c2) {
        g.gridx = 0; g.gridy = row;
        panel.add(makeLabel(labelText), g);
        g.gridx = 1;
        panel.add(c1, g);
        if (c2 != null) { g.gridx = 2; panel.add(c2, g); }
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
