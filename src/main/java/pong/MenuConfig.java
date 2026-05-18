package pong;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.awt.event.ActionEvent;

import javax.swing.*;

public class MenuConfig implements ActionListener {
    protected JRadioButton ventana;
    protected JRadioButton pantallaCompleta;
    protected JCheckBox musicaBox;
    protected JComboBox<String> pistaMusical;
    protected JComboBox<String> pelota;
    protected JComboBox<String> cancha;
    protected JComboBox<String> paleta;
    protected JTextField movArriba1;
    protected JTextField movAbajo1;
    protected JTextField movArriba2;
    protected JTextField movAbajo2;
    protected JButton reset;
    protected JButton guardar;
    protected JPanel config;
    protected JPanel panelConfigBot;
    protected JFrame frame;
    protected JPanel panelCompleto;
    protected String archivoConfig = "defaultPong.properties";
    protected Properties defaultProps;
    protected Map<String, JComponent> componentes;
    protected String rutaArchivo;

    public MenuConfig() {

        frame = new JFrame();
        frame.setTitle("Configuracion");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Crear el panel principal del menú de configuración con fondo de imagen
        panelCompleto = new JPanel(new BorderLayout()) {
            Image pongFondo = new ImageIcon(getClass().getResource("/pong/confgFondo.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (pongFondo != null) {
                    g.drawImage(pongFondo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    System.out.println("No se pudo cargar la imagen de fondo.");
                }
            }
        };
        // Config botones
        ventana = new JRadioButton("Ventana", true);
        pantallaCompleta = new JRadioButton("Pantalla completa", false);
        ButtonGroup grupoVentana = new ButtonGroup();
        grupoVentana.add(ventana);
        grupoVentana.add(pantallaCompleta);
        musicaBox = new JCheckBox("", true);
        musicaBox.addActionListener(this);
        movArriba2 = new JTextField("↑", 5);
        movAbajo2 = new JTextField("↓", 5);
        movArriba1 = new JTextField("W", 5);
        movAbajo1 = new JTextField("S", 5);
        pistaMusical = new JComboBox<>(new String[] { "pong_cancion.wav", "homero.wav", "undertale.wav" });
        pelota = new JComboBox<>(new String[] { "Original", "Futbol", "Basquet"});
        paleta = new JComboBox<>(new String[] { "Original", "Paleta azul", "Paleta roja"});
        cancha = new JComboBox<>(new String[] { "Original", "Futbol", "Ciudad"});
        reset = new JButton("Restablecer");
        reset.addActionListener(this);
        reset.setVisible(true);
        guardar = new JButton("Guardar");
        guardar.addActionListener(this);
        guardar.setVisible(true);
        defaultProps = new Properties();
        // JComponent el cual engloba todo
        componentes = new HashMap<>();

        // Cargo en un mapa las key y los valores
        // JRadioButtons
        componentes.put("ventana", ventana);
        componentes.put("pantallaCompleta", pantallaCompleta);
        // JCheckBoxes
        componentes.put("musicaBox", musicaBox);
        // JTextFields
        componentes.put("movArriba1", movArriba1);
        componentes.put("movAbajo1", movAbajo1);
        componentes.put("movArriba2", movArriba2);
        componentes.put("movAbajo2", movAbajo2);
        // JComboBox
        componentes.put("pistaMusical", pistaMusical);
        componentes.put("pelota", pelota);
        componentes.put("paleta", paleta);
        componentes.put("cancha", cancha);

        // Iniciara el mapa (cargarConfig) con los datos que le pase el archivos
        cargarEnArchivo(defaultProps, archivoConfig);
        cargarConfiguracion(componentes, defaultProps);

        // Creo el menu configuracion
        // Añado sus botones
        config = new JPanel(new GridBagLayout());
        config.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(5, 5, 5, 5);
        config.add(new JLabel("Pantalla: "), g);
        g.gridx++;
        config.add(ventana, g);
        g.gridx++;
        config.add(pantallaCompleta, g);
        g.gridx = 0;
        g.gridy++;
        g.gridy++;
        config.add(new JLabel("Musica: "), g);
        g.gridx++;
        config.add(musicaBox, g);
        g.gridx = 0;
        g.gridy++;
        g.gridy++;
        g.gridy++;
        config.add(new JLabel("Teclas por defecto: "), g);
        g.gridy++;
        g.gridy++;
        config.add(new JLabel("Habilidades jugador 1: "), g);
        g.gridy++;
        g.gridy++;
        config.add(new JLabel("Movimiento arriba: "), g);
        g.gridx++;
        config.add(movArriba1, g);
        g.gridx = 0;
        g.gridy++;
        config.add(new JLabel("Movimiento abajo: "), g);
        g.gridx++;
        config.add(movAbajo1, g);
        g.gridx = 0;
        g.gridy++;
        g.gridy++;
        config.add(new JLabel("Habilidades jugador 2: "), g);
        g.gridy++;
        g.gridy++;
        config.add(new JLabel("Movimiento arriba: "), g);
        g.gridx++;
        config.add(movArriba2, g);
        g.gridx = 0;
        g.gridy++;
        config.add(new JLabel("Movimiento abajo: "), g);
        g.gridx++;
        config.add(movAbajo2, g);
        g.gridx = 0;
        g.gridy++;
        config.add(new JLabel("Elegir pista musical: "), g);
        g.gridx++;
        config.add(pistaMusical, g);
        g.gridx = 0;
        g.gridy++;
        config.add(new JLabel("Elegir pelota: "), g);
        g.gridx++;
        config.add(pelota, g);
        g.gridx = 0;
        g.gridy++;
        config.add(new JLabel("Elegir cancha: "), g);
        g.gridx++;
        config.add(cancha, g);
        g.gridx = 0;
        g.gridy++;
        config.add(new JLabel("Elegir paleta: "), g);
        g.gridx++;
        config.add(paleta, g);

        // Config panel botones
        panelConfigBot = new JPanel();
        panelConfigBot.add(guardar);
        panelConfigBot.add(reset);
        panelConfigBot.setOpaque(false);
        // Configuro los botones con color blanco y fondo transparente
        Font buttonFontConfg = new Font("Courier New", Font.BOLD, 20);
        ventana.setFont(buttonFontConfg);
        ventana.setForeground(Color.WHITE);
        ventana.setBackground(Color.BLACK);
        ventana.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));

        pantallaCompleta.setFont(buttonFontConfg);
        pantallaCompleta.setForeground(Color.WHITE);
        pantallaCompleta.setBackground(Color.BLACK);
        pantallaCompleta.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));

        musicaBox.setFont(buttonFontConfg);
        musicaBox.setForeground(Color.WHITE);
        musicaBox.setBackground(Color.BLACK);
        musicaBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));

        pistaMusical.setFont(buttonFontConfg);
        pistaMusical.setForeground(Color.WHITE);
        pistaMusical.setBackground(Color.BLACK);
        pistaMusical.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));

        pelota.setFont(buttonFontConfg);
        pelota.setForeground(Color.WHITE);
        pelota.setBackground(Color.BLACK);
        pelota.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));

        cancha.setFont(buttonFontConfg);
        cancha.setForeground(Color.WHITE);
        cancha.setBackground(Color.BLACK);
        cancha.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));

        paleta.setFont(buttonFontConfg);
        paleta.setForeground(Color.WHITE);
        paleta.setBackground(Color.BLACK);
        paleta.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));

        movArriba1.setFont(buttonFontConfg);
        movArriba1.setForeground(Color.WHITE);
        movArriba1.setBackground(Color.BLACK);
        movArriba1.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));

        movAbajo1.setFont(buttonFontConfg);
        movAbajo1.setForeground(Color.WHITE);
        movAbajo1.setBackground(Color.BLACK);
        movAbajo1.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));

        movArriba2.setFont(buttonFontConfg);
        movArriba2.setForeground(Color.WHITE);
        movArriba2.setBackground(Color.BLACK);
        movArriba2.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));

        movAbajo2.setFont(buttonFontConfg);
        movAbajo2.setForeground(Color.WHITE);
        movAbajo2.setBackground(Color.BLACK);
        movAbajo2.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));

        reset.setFont(buttonFontConfg);
        reset.setForeground(Color.WHITE);
        reset.setBackground(Color.BLACK);
        reset.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));

        guardar.setFont(buttonFontConfg);
        guardar.setForeground(Color.WHITE);
        guardar.setBackground(Color.BLACK);
        guardar.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));
        // Cambiar color de todos los JLabel en el panel de configuración
        Component[] components = config.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                component.setForeground(Color.WHITE);
                component.setFont(new Font("Courier New", Font.BOLD, 20));
            }
        }
        // Unificar y agregar paneles al frame

        panelCompleto.add(config, BorderLayout.CENTER);
        panelCompleto.add(panelConfigBot, BorderLayout.SOUTH);
        frame.add(panelCompleto);
        frame.setVisible(true);
    }

    private void cargarConfiguracion(Map<String, JComponent> componentes, Properties defaultProps) {
        for (Map.Entry<String, JComponent> entry : componentes.entrySet()) {
            String clave = entry.getKey();
            JComponent comp = entry.getValue();
            String valor = defaultProps.getProperty(clave);

            if (valor == null)
                continue;

            if (comp instanceof JRadioButton) {
                ((JRadioButton) comp).setSelected(Boolean.parseBoolean(valor));
            } else if (comp instanceof JCheckBox) {
                ((JCheckBox) comp).setSelected(Boolean.parseBoolean(valor));
            } else if (comp instanceof JTextField) {
                ((JTextField) comp).setText(valor);
            } else if (comp instanceof JComboBox) {
                ((JComboBox<String>) comp).setSelectedItem(valor);
            }
        }
    }

    private void guardarConfiguracion(Map<String, JComponent> componentes, Properties defaultProps) {
        for (Map.Entry<String, JComponent> entry : componentes.entrySet()) {
            String clave = entry.getKey();
            JComponent comp = entry.getValue();

            if (comp instanceof JRadioButton) {
                defaultProps.setProperty(clave, String.valueOf(((JRadioButton) comp).isSelected()));
            } else if (comp instanceof JCheckBox) {
                defaultProps.setProperty(clave, String.valueOf(((JCheckBox) comp).isSelected()));
            } else if (comp instanceof JTextField) {
                defaultProps.setProperty(clave, ((JTextField) comp).getText());
            } else if (comp instanceof JComboBox) {
                defaultProps.setProperty(clave, String.valueOf(((JComboBox<String>) comp).getSelectedItem()));
            }
        }
    }

    protected static void guardarEnArchivo(Properties defaultProps, String rutaArchivo) {
        try (FileOutputStream out = new FileOutputStream(rutaArchivo)) {
            defaultProps.store(out, "ConfgUsuario");
        } catch (Exception e) {
            System.out.println("No se pudo encontrar la ruta");
            e.printStackTrace();
        }
    }

    protected static void cargarEnArchivo(Properties defaultProps, String rutaArchivo) {
        try (FileInputStream in = new FileInputStream(rutaArchivo)) {
            defaultProps.load(in);
        } catch (Exception e) {
            // Lo normal seria que a la primera tire error
            System.out.println("No se pudo cargar configuración previa.");
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == guardar) {
            guardarConfiguracion(componentes, defaultProps);
            guardarEnArchivo(defaultProps, archivoConfig);
        } else if (e.getSource() == reset) {
            // Reestablece botones
            musicaBox.setSelected(true);
            ventana.setSelected(true);
            movArriba2.setText("↑");
            movAbajo2.setText("↓");
            movArriba1.setText("W");
            movAbajo1.setText("S");
            pistaMusical.setSelectedIndex(0);
            pelota.setSelectedIndex(0);
            paleta.setSelectedIndex(0);
            cancha.setSelectedIndex(0);
        }
    }
}