package clasesCompartidas;

import clasesCompartidas.JPanelImage;
import pong.MenuConfig;
import spaceinvaders.MenuConfigSpace;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import com.entropyinteractive.*;
import pong.Pong;
import spaceinvaders.SpaceInvaders;
import lodeRunner.LodeRunner;

public class LanzadorJuego extends JFrame implements ActionListener {
    private JGame juego;
    private Thread t;
    private final DefaultListModel<String> listaJuegos;
    private final JList<String> juegos;
    private final JButton botonConfig;
    private final JButton botonIniciar;
    private final JPanel panelImg;
    private final JScrollPane scrollPane;

    public LanzadorJuego() {
        setTitle("Sistema de videojuego");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // 1. Lista de juegos (Fijate bien los nombres exactos)
        listaJuegos = new DefaultListModel<>();
        listaJuegos.addElement("Pong");
        listaJuegos.addElement("SpaceInvaders");
        listaJuegos.addElement("Lode Runner"); // Mantenemos el espacio
        listaJuegos.addElement("Counter-Strike");

        // Botones
        botonConfig = new JButton("Configuración");
        botonConfig.addActionListener(this);
        botonIniciar = new JButton("Iniciar juego");
        botonIniciar.addActionListener(this);

        juegos = new JList<>(listaJuegos);
        scrollPane = new JScrollPane(juegos);
        add(scrollPane, BorderLayout.WEST);

        panelImg = new JPanel();
        
        // Listener de la lista corregido en rutas y cadenas
        juegos.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selected = juegos.getSelectedValue();
                    panelImg.removeAll();
                    panelImg.setLayout(new BorderLayout());

                    // CORRECCIÓN DE RUTAS: Usar rutas de Classpath uniformes (con /)
                    if ("SpaceInvaders".equals(selected)) {
                        panelImg.add(new JPanelImage("/AssetsSpace/Galaxia.png"));
                    } else if ("Pong".equals(selected)) {
                        panelImg.add(new JPanelImage("/pong/portada_pong.png"));
                    } else if ("Lode Runner".equals(selected)) {
                        panelImg.add(new JPanelImage("/lodeRunner/portada_loderunner.png"));
                    } 
                    
                    panelImg.revalidate();
                    panelImg.repaint();
                }
            }
        });

        add(panelImg, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.add(botonIniciar);
        panelBotones.add(botonConfig);

        add(panelBotones, BorderLayout.SOUTH);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonIniciar) {
            String juegoSeleccionado = juegos.getSelectedValue();
            if (juegoSeleccionado == null) {
                JOptionPane.showMessageDialog(LanzadorJuego.this, "Error, seleccione un juego para jugar");
                return;
            }

            if ("Pong".equals(juegoSeleccionado)) {
                juego = new Pong("Pong", 800, 600);
                iniciarHiloJuego();
            } else if ("SpaceInvaders".equals(juegoSeleccionado)) {
                juego = new SpaceInvaders("SpaceInvaders", 800, 600);
                iniciarHiloJuego();
            } else if ("Lode Runner".equals(juegoSeleccionado)) {
                juego = new LodeRunner("Lode Runner", 800, 600);
                iniciarHiloJuego();
            } else if ("Counter-Strike".equals(juegoSeleccionado)) {
                JOptionPane.showMessageDialog(LanzadorJuego.this, "No somos tan buenos, todavia no sabemos hacer este");
            }
        }
        
        if (e.getSource() == botonConfig) {
            String juegoSeleccionado = juegos.getSelectedValue();
            if (juegoSeleccionado == null) {
                JOptionPane.showMessageDialog(LanzadorJuego.this, "Error, seleccione un juego para configurar");
                return;
            } 
            
            if ("SpaceInvaders".equals(juegoSeleccionado)) {
                new MenuConfigSpace();
            } else if ("Pong".equals(juegoSeleccionado)) {
                new MenuConfig();
            } else if ("Counter-Strike".equals(juegoSeleccionado)) {
                JOptionPane.showMessageDialog(LanzadorJuego.this, "Configuración en proceso... (Jugate otro juego)");
            }
        }
    }

    // Método auxiliar para evitar repetir código de hilos repetidas veces
    private void iniciarHiloJuego() {
        t = new Thread() {
            @Override
            public void run() {
                juego.run(1.0 / 60.0);
            }
        };
        t.start();
    }

    public static void main(String[] args) {
        new LanzadorJuego();
    }
}
