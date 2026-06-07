package clasesCompartidas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;

/**
 * Clase base compartida para menús de configuración de todos los juegos.
 *
 * Provee:
 *  - Ventana con fondo de imagen personalizable
 *  - Estilo visual uniforme (Courier New, negro/blanco/gris)
 *  - Lógica de carga y guardado en archivo .properties
 *  - Botones Guardar y Restablecer
 *  - GridBagLayout listo para agregar filas
 *
 * Cómo extender:
 *  1. Llamar a super(rutaArchivo, rutaImagenFondo) en el constructor
 *  2. Crear los componentes específicos del juego
 *  3. Agregarlos al panel con agregarSeccion(), agregarFila(), agregarEspacio()
 *  4. Implementar cargarValores() para leer el .properties y poblar los componentes
 *  5. Implementar guardarValores() para leer los componentes y escribir en el .properties
 *  6. Implementar restablecerDefectos() para el botón Reset
 *  7. Llamar a construir() al final del constructor de la subclase
 */
public abstract class Configuracion implements ActionListener {

    // ── Estilo visual compartido ──────────────────────────────────────────────
    public static final Font  FONT_CONFIG   = new Font("Courier New", Font.BOLD, 20);
    public static final Font  FONT_SECTION  = new Font("Courier New", Font.BOLD, 16);
    public static final Color COLOR_FG      = Color.WHITE;
    public static final Color COLOR_BG      = Color.BLACK;
    public static final Color COLOR_BORDER  = Color.GRAY;
    public static final Color COLOR_SECTION = new Color(150, 200, 255);

    // ── Infraestructura ───────────────────────────────────────────────────────
    protected JFrame  frame;
    protected JPanel  panelConfig;   // panel interno donde las subclases agregan filas
    protected JButton guardar;
    protected JButton reset;

    protected final String     rutaArchivo;
    protected       Properties props;

    // GridBagConstraints compartido — las subclases lo usan a través de los helpers
    private GridBagConstraints gbc;
    private int filaActual = 0;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * @param titulo          Título de la ventana
     * @param rutaArchivo     Ruta al .properties (ej: "spaceinvaders.properties")
     * @param rutaImagenFondo Ruta al recurso de imagen para el fondo (ej: "/AssetsSpace/Galaxia.png")
     *                        Si es null o no se encuentra, usa fondo negro.
     */
    protected Configuracion(String titulo, String rutaArchivo, String rutaImagenFondo) {
        this.rutaArchivo = rutaArchivo;
        this.props       = new Properties();

        frame = new JFrame(titulo);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // ── Panel con fondo de imagen ─────────────────────────────────────────
        Image fondoCargado = cargarImagen(rutaImagenFondo);
        JPanel panelCompleto = new JPanel(new BorderLayout()) {
            private final Image fondo = fondoCargado;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
                else { g.setColor(Color.BLACK); g.fillRect(0, 0, getWidth(), getHeight()); }
            }
        };

        // ── Panel de configuración (filas) ────────────────────────────────────
        panelConfig = new JPanel(new GridBagLayout());
        panelConfig.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ── Botones ───────────────────────────────────────────────────────────
        guardar = crearBoton("Guardar");
        guardar.addActionListener(this);
        reset   = crearBoton("Restablecer");
        reset.addActionListener(this);

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.add(guardar);
        panelBotones.add(reset);

        // ── Scroll para ventanas con muchos campos ────────────────────────────
        JScrollPane scroll = new JScrollPane(panelConfig);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelCompleto.add(scroll,       BorderLayout.CENTER);
        panelCompleto.add(panelBotones, BorderLayout.SOUTH);
        frame.add(panelCompleto);
    }

    // ── Métodos que deben implementar las subclases ───────────────────────────

    /** Lee el .properties ya cargado en {@code props} y puebla los componentes. */
    protected abstract void cargarValores();

    /** Lee los componentes y escribe los valores en {@code props}. */
    protected abstract void guardarValores();

    /** Vuelve todos los componentes a sus valores por defecto (sin guardar). */
    protected abstract void restablecerDefectos();

    // ── Método que las subclases llaman al final de su constructor ────────────

    /**
     * Finaliza la construcción: carga el .properties, puebla los componentes
     * y muestra la ventana. Llamar DESPUÉS de agregar todas las filas.
     */
    protected void construir() {
        cargarProperties();
        cargarValores();
        frame.setVisible(true);
    }

    // ── ActionListener base ───────────────────────────────────────────────────

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == reset) {
            restablecerDefectos();
        } else if (e.getSource() == guardar) {
            guardarValores();
            guardarProperties();
        }
    }

    // ── Helpers de layout para las subclases ─────────────────────────────────

    /** Agrega un título de sección (texto en azul claro, ocupa 3 columnas). */
    protected void agregarSeccion(String texto) {
        gbc.gridx = 0; gbc.gridy = filaActual++; gbc.gridwidth = 3;
        JLabel l = new JLabel(texto);
        l.setFont(FONT_SECTION);
        l.setForeground(COLOR_SECTION);
        panelConfig.add(l, gbc);
        gbc.gridwidth = 1;
    }

    /** Agrega una fila con etiqueta en col 0 y componente en col 1. */
    protected void agregarFila(String etiqueta, JComponent componente) {
        gbc.gridx = 0; gbc.gridy = filaActual;
        JLabel l = new JLabel(etiqueta);
        l.setFont(FONT_CONFIG);
        l.setForeground(COLOR_FG);
        panelConfig.add(l, gbc);

        gbc.gridx = 1; gbc.gridy = filaActual++;
        panelConfig.add(componente, gbc);
    }

    /**
     * Agrega una fila con etiqueta en col 0 y DOS componentes en col 1 y col 2.
     * Útil para RadioButtons lado a lado (ej: Ventana / Pantalla completa).
     */
    protected void agregarFilaDoble(String etiqueta, JComponent comp1, JComponent comp2) {
        gbc.gridx = 0; gbc.gridy = filaActual;
        JLabel l = new JLabel(etiqueta);
        l.setFont(FONT_CONFIG);
        l.setForeground(COLOR_FG);
        panelConfig.add(l, gbc);

        gbc.gridx = 1; gbc.gridy = filaActual;
        panelConfig.add(comp1, gbc);

        gbc.gridx = 2; gbc.gridy = filaActual++;
        panelConfig.add(comp2, gbc);
    }

    /** Agrega una fila vacía como separador visual. */
    protected void agregarEspacio() {
        gbc.gridx = 0; gbc.gridy = filaActual++; gbc.gridwidth = 3;
        panelConfig.add(new JLabel(" "), gbc);
        gbc.gridwidth = 1;
    }

    // ── Helpers de creación de componentes estilizados ────────────────────────

    protected JTextField crearTextField(String valorDefecto) {
        JTextField tf = new JTextField(valorDefecto, 8);
        tf.setFont(FONT_CONFIG);
        tf.setForeground(COLOR_FG);
        tf.setBackground(COLOR_BG);
        tf.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 3, true));
        return tf;
    }

    protected JTextField crearTextField(String valorDefecto, int columnas) {
        JTextField tf = new JTextField(valorDefecto, columnas);
        tf.setFont(FONT_CONFIG);
        tf.setForeground(COLOR_FG);
        tf.setBackground(COLOR_BG);
        tf.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 3, true));
        return tf;
    }

    protected JComboBox<String> crearCombo(String[] opciones) {
        JComboBox<String> cb = new JComboBox<>(opciones);
        cb.setFont(FONT_CONFIG);
        cb.setForeground(COLOR_FG);
        cb.setBackground(COLOR_BG);
        cb.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 3, true));
        return cb;
    }

    protected JRadioButton crearRadio(String texto, boolean seleccionado) {
        JRadioButton rb = new JRadioButton(texto, seleccionado);
        rb.setFont(FONT_CONFIG);
        rb.setForeground(COLOR_FG);
        rb.setBackground(COLOR_BG);
        rb.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 3, true));
        rb.setOpaque(true);
        return rb;
    }

    protected JCheckBox crearCheckBox(boolean seleccionado) {
        JCheckBox cb = new JCheckBox("", seleccionado);
        cb.setFont(FONT_CONFIG);
        cb.setForeground(COLOR_FG);
        cb.setBackground(COLOR_BG);
        cb.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 3, true));
        cb.setOpaque(true);
        return cb;
    }

    protected JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_CONFIG);
        btn.setForeground(COLOR_FG);
        btn.setBackground(COLOR_BG);
        btn.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 3, true));
        return btn;
    }

    // ── Helper para seleccionar un ítem en un combo ───────────────────────────

    protected void seleccionarEnCombo(JComboBox<String> combo, String valor) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(valor)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    // ── Carga y guardado del .properties ─────────────────────────────────────

    protected void cargarProperties() {
        try (InputStream in = new FileInputStream(rutaArchivo)) {
            props.load(in);
        } catch (IOException ignored) {
            // Si no existe el archivo, props queda vacío y se usan los defectos
        }
    }

    protected void guardarProperties() {
        try (OutputStream out = new FileOutputStream(rutaArchivo)) {
            props.store(out, "Configuracion");
            JOptionPane.showMessageDialog(frame, "¡Configuración guardada correctamente!");
            frame.dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame,
                "No se pudo guardar:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Carga de imagen de fondo ──────────────────────────────────────────────

    private Image cargarImagen(String ruta) {
        if (ruta == null) return null;
        try {
            java.net.URL url = getClass().getResource(ruta);
            return url != null ? new ImageIcon(url).getImage() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // ── Método estático para cargar propiedades desde archivo ──────────────────

    /**
     * Método estático para cargar propiedades desde un archivo.
     * Útil para cargar configuración directamente sin crear una ventana.
     *
     * @param props   El objeto Properties donde se cargarán los valores
     * @param rutaArchivo La ruta al archivo .properties
     */
    public static void cargarEnArchivo(Properties props, String rutaArchivo) {
        try (InputStream in = new FileInputStream(rutaArchivo)) {
            props.load(in);
        } catch (IOException ignored) {
            // Si no existe el archivo, props queda vacío y se usan los defectos
        }
    }
}