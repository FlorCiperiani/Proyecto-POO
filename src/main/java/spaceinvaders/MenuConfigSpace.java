package spaceinvaders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
//import java.util.HashMap;
import java.util.Properties;
import java.util.Map;

public class MenuConfigSpace extends Object implements ActionListener {
    private JFrame frame;
    private JPanel panelCompleto;
    
  
    private JRadioButton pantallaCompleta;
    private JCheckBox musicaBox;
    private JComboBox<String> pistaMusical;
    private JComboBox<String> comboGalaxia; 
    
    private JTextField movIzquierda;      
    private JTextField movDerecha;         
    private JTextField vidasIniciales;      
    
    private JButton reset;
    private JButton guardar;
    
    private Properties defaultProps;
    private Map<String, JComponent> componentes;
    private String rutaArchivo = "spaceinvaders.properties";

    public MenuConfigSpace() {

        // ===== Frame =====
        frame = new JFrame("Config Space Invaders");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // ===== PROPERTIES =====
        defaultProps = new Properties();
        componentes = new java.util.HashMap<>();

        setPropertiesPorDefecto();

        // ===== Panel principal con fondo =====
        panelCompleto = new JPanel(new BorderLayout()) {
            Image fondo = new ImageIcon(
                MenuConfigSpace.class.getResource("/AssetsSpace/Galaxia.webp")
            ).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };

        frame.setContentPane(panelCompleto);
        pistaMusical = new JComboBox<>(new String[] { "Alien Homeworld.mp3", "laser.mp3", "undertale.wav" });

        // ===== Inicializar componentes =====
        inicializarComponentes();

        // ===== Cargar configuración existente =====
        cargarConfiguracion();

        // ===== Mostrar =====
        frame.setVisible(true);

        // =====Configuracion=====
        

       
    }



    private void setPropertiesPorDefecto() {
        defaultProps.setProperty("pantallaCompleta", "false");
        defaultProps.setProperty("musica", "true");
        defaultProps.setProperty("fondoGalaxia", "Original");
        defaultProps.setProperty("teclaIzquierda", "LEFT");
        defaultProps.setProperty("teclaDerecha", "RIGHT");
        defaultProps.setProperty("vidas", "3");
    }

    private void inicializarComponentes() {
        movIzquierda = new JTextField(10);
        movDerecha = new JTextField(10);
        vidasIniciales = new JTextField(5);
        
        String[] opcionesGalaxia = {"Original", "Ciudad", "Futbol", "Oceano"}; // Sumamos Océano por si lo usás
        comboGalaxia = new JComboBox<>(opcionesGalaxia);
        
        componentes.put("teclaIzquierda", movIzquierda);
        componentes.put("teclaDerecha", movDerecha);
        componentes.put("vidas", vidasIniciales);
        componentes.put("fondoGalaxia", comboGalaxia);
        componentes.put("pistaMusical", pistaMusical);
        
        guardar = new JButton("Guardar");
        guardar.addActionListener(this);

        // --- DISEÑO DE LA VENTANA SOBRE EL PANEL DE FONDO ---
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));
        panelFormulario.setOpaque(false); // Clave para que se vea la galaxia de fondo

        // Creamos etiquetas con texto blanco para que resalten sobre el fondo oscuro
        JLabel lblIzquierda = new JLabel("Mover Izquierda:"); lblIzquierda.setForeground(Color.WHITE);
        JLabel lblDerecha = new JLabel("Mover Derecha:"); lblDerecha.setForeground(Color.WHITE);
        JLabel lblVidas = new JLabel("Vidas Iniciales:"); lblVidas.setForeground(Color.WHITE);
        JLabel lblFondo = new JLabel("Estilo de Fondo:"); lblFondo.setForeground(Color.WHITE);
        JLabel lblMusica = new JLabel("Pista Musical:"); lblMusica.setForeground(Color.WHITE);

        panelFormulario.add(lblIzquierda);     panelFormulario.add(movIzquierda);
        panelFormulario.add(lblDerecha);       panelFormulario.add(movDerecha);
        panelFormulario.add(lblVidas);         panelFormulario.add(vidasIniciales);
        panelFormulario.add(lblFondo);         panelFormulario.add(comboGalaxia);
        panelFormulario.add(lblMusica);        panelFormulario.add(pistaMusical);
        
        // Espacio vacío y botón Guardar
        panelFormulario.add(new JLabel());
        panelFormulario.add(guardar);

        // Añadimos un margen prolijo alrededor del formulario
        panelCompleto.add(panelFormulario, BorderLayout.CENTER);
        panelCompleto.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void cargarConfiguracion() {
        Properties props = new Properties(defaultProps);
        try (InputStream input = new FileInputStream(rutaArchivo)) {
            props.load(input);
        } catch (IOException ex) {
            System.out.println("No se encontró archivo previo, usando defecto.");
        }
        
      
        movIzquierda.setText(props.getProperty("teclaIzquierda"));
        movDerecha.setText(props.getProperty("teclaDerecha"));
        vidasIniciales.setText(props.getProperty("vidas"));
        comboGalaxia.setSelectedItem(props.getProperty("fondoGalaxia"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == guardar) {
            Properties props = new Properties();
            props.setProperty("teclaIzquierda", movIzquierda.getText().toUpperCase());
            props.setProperty("teclaDerecha", movDerecha.getText().toUpperCase());
            props.setProperty("vidas", vidasIniciales.getText());
            props.setProperty("fondoGalaxia", (String) comboGalaxia.getSelectedItem());
            
            props.setProperty("musicaFondo", (String) pistaMusical.getSelectedItem());
            
            try (OutputStream output = new FileOutputStream(rutaArchivo)) {
                props.store(output, "Configuracion de Space Invaders");
                JOptionPane.showMessageDialog(frame, "Configuración guardada correctamente.");
                frame.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}