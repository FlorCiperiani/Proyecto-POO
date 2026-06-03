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
    
    // Componentes de la interfaz adaptados a Space Invaders
    private JRadioButton pantallaCompleta;
    private JCheckBox musicaBox;
    private JComboBox<String> pistaMusical;
    private JComboBox<String> comboGalaxia; // Reemplaza a 'cancha'
    
    private JTextField movIzquierda;        // Reemplaza a movArriba1
    private JTextField movDerecha;          // Reemplaza a movAbajo1
    private JTextField vidasIniciales;       // Nueva opción útil
    
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

        // ===== Inicializar componentes =====
        inicializarComponentes();

        // ===== Cargar configuración existente =====
        cargarConfiguracion();

        // ===== Mostrar =====
        frame.setVisible(true);
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
        // Ejemplo de cómo adaptar los campos de texto
        movIzquierda = new JTextField(10);
        movDerecha = new JTextField(10);
        vidasIniciales = new JTextField(5);
        
        String[] opcionesGalaxia = {"Original", "Ciudad", "Futbol"};
        comboGalaxia = new JComboBox<>(opcionesGalaxia);
        
        // Registro en el mapa para automatizar guardado/lectura
        componentes.put("teclaIzquierda", movIzquierda);
        componentes.put("teclaDerecha", movDerecha);
        componentes.put("vidas", vidasIniciales);
        componentes.put("fondoGalaxia", comboGalaxia);
        
        guardar = new JButton("Guardar");
        guardar.addActionListener(this);
        // ... añadir botones al frame
    }

    private void cargarConfiguracion() {
        Properties props = new Properties(defaultProps);
        try (InputStream input = new FileInputStream(rutaArchivo)) {
            props.load(input);
        } catch (IOException ex) {
            System.out.println("No se encontró archivo previo, usando defecto.");
        }
        
        // Asignar los valores cargados a los componentes visuales
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