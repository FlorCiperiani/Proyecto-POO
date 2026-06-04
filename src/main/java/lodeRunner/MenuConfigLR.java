package lodeRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuConfigLR extends JFrame {

    // Componentes interactivos de la interfaz
    private JCheckBox chkPantallaCompleta;
    private JCheckBox chkSonidoGeneral;
    private JCheckBox chkMusica;
    private JCheckBox chkEfectos;
    private JComboBox<String> comboMusica;
    private JComboBox<String> comboSkin;
    
    private JButton btnGuardar;
    private JButton btnReset;

    public MenuConfigLR() {
        // Configuración básica de la ventana emergente
        setTitle("Configuración - Lode Runner");
        setSize(400, 350);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null); // La centra en la pantalla
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra esta ventana, no todo el juego

        // ── PANEL DE PARAMETROS (Formulario) ───────────────────────────
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Modo de pantalla
        panelFormulario.add(new JLabel("Modo de Pantalla:"));
        chkPantallaCompleta = new JCheckBox("Pantalla Completa", Configuracion.pantallaCompleta);
        panelFormulario.add(chkPantallaCompleta);

        // 2. Sonido General (Master Switch)
        panelFormulario.add(new JLabel("Sonido General:"));
        chkSonidoGeneral = new JCheckBox("Activado", Configuracion.sonidoGeneralActivado);
        panelFormulario.add(chkSonidoGeneral);

        // 3. Música de Fondo
        panelFormulario.add(new JLabel("Música de Fondo:"));
        chkMusica = new JCheckBox("Activado", Configuracion.musicaActivada);
        panelFormulario.add(chkMusica);

        // 4. Efectos de Sonido
        panelFormulario.add(new JLabel("Efectos de Sonido:"));
        chkEfectos = new JCheckBox("Activado", Configuracion.efectosActivados);
        panelFormulario.add(chkEfectos);

        // 5. Selección de Pista Musical
        panelFormulario.add(new JLabel("Pista Musical:"));
        String[] pistas = {"tema_original.wav", "tema_moderno.wav", "tema_retro.wav"};
        comboMusica = new JComboBox<>(pistas);
        comboMusica.setSelectedItem(Configuracion.pistaMusicalSeleccionada);
        panelFormulario.add(comboMusica);

        // 6. Selección de Skin
        panelFormulario.add(new JLabel("Skin del Personaje:"));
        String[] skins = {"original", "clasico_8bit", "moderno"};
        comboSkin = new JComboBox<>(skins);
        comboSkin.setSelectedItem(Configuracion.skinPersonajeSeleccionado);
        panelFormulario.add(comboSkin);

        add(panelFormulario, BorderLayout.CENTER);

        // ── PANEL DE BOTONES (Guardar y Reset) ─────────────────────────
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnGuardar = new JButton("Guardar");
        btnReset = new JButton("Reset");
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnReset);
        add(panelBotones, BorderLayout.SOUTH);

        // ── LÓGICA DE LOS BOTONES (Listeners) ──────────────────────────
        
        // Acción del Botón Guardar
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pasamos los valores de la UI a nuestras variables estáticas de Configuración
                Configuracion.pantallaCompleta = chkPantallaCompleta.isSelected();
                Configuracion.sonidoGeneralActivado = chkSonidoGeneral.isSelected();
                Configuracion.musicaActivada = chkMusica.isSelected();
                Configuracion.efectosActivados = chkEfectos.isSelected();
                Configuracion.pistaMusicalSeleccionada = (String) comboMusica.getSelectedItem();
                Configuracion.skinPersonajeSeleccionado = (String) comboSkin.getSelectedItem();

                JOptionPane.showMessageDialog(MenuConfigLR.this, "Configuración guardada con éxito.");
                dispose(); // Cierra la ventana de configuración
            }
        });

        // Acción del Botón Reset
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Volvemos los valores lógicos a fábrica
                Configuracion.reset();
                
                // Refrescamos los componentes de la interfaz para que reflejen el reset
                chkPantallaCompleta.setSelected(Configuracion.pantallaCompleta);
                chkSonidoGeneral.setSelected(Configuracion.sonidoGeneralActivado);
                chkMusica.setSelected(Configuracion.musicaActivada);
                chkEfectos.setSelected(Configuracion.efectosActivados);
                comboMusica.setSelectedItem(Configuracion.pistaMusicalSeleccionada);
                comboSkin.setSelectedItem(Configuracion.skinPersonajeSeleccionado);
                
                JOptionPane.showMessageDialog(MenuConfigLR.this, "Se han restablecido los valores por defecto.");
            }
        });

        // Hacer visible la ventana apenas se instancia
        setVisible(true);
    }
}