package clasesCompartidas;

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

        // Lista de juegos
        listaJuegos = new DefaultListModel<>();
        listaJuegos.addElement("Pong");
        listaJuegos.addElement("SpaceInvaders");
        listaJuegos.addElement("Counter-Strike");
        // Bottones
        botonConfig = new JButton("Configuración");
        botonConfig.addActionListener(this);
        botonIniciar = new JButton("Iniciar juego");
        botonIniciar.addActionListener(this);

        // Añado los juegos a la barra de juegos
        juegos = new JList<>(listaJuegos);
        scrollPane = new JScrollPane(juegos);
        add(scrollPane, BorderLayout.WEST);

        // Creo el panel de la imagen
        panelImg = new JPanel();
        //Cargo las imagenes segun aprete los juegos de la lista 
        juegos.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selected = juegos.getSelectedValue();
                    panelImg.removeAll();
                    panelImg.setLayout(new BorderLayout());

                    if ("SpaceInvaders".equals(selected)) {
                        panelImg.add(new JPanelImage("/lemmings/portada_lemmings.png"));
                    } else if ("Pong".equals(selected)) {
                        panelImg.add(new JPanelImage("/pong/portada_pong.png"));
                    }
                    panelImg.revalidate();
                    panelImg.repaint();
                }
            }
        });

        // Meto el panel al frame
        add(panelImg, BorderLayout.CENTER);

        // Creo un panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.add(botonIniciar);
        panelBotones.add(botonConfig);

        // Unifico y agrego panel al frame
        add(panelBotones, BorderLayout.SOUTH);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonIniciar) {
            // Agarra el valor del juego seleccionado en la barra
            String juegoSeleccionado = juegos.getSelectedValue();
            if (juegoSeleccionado == null) {
                JOptionPane.showMessageDialog(LanzadorJuego.this, "Error, seleccione un juego para jugar");
            }else if ("Pong".equals(juegoSeleccionado)) {
                juego = new Pong("Pong", 800, 600);
                t = new Thread() {
                    public void run() {
                        juego.run(1.0 / 60.0);
                    }
                };
                t.start();
            } else if ("SpaceInvaders".equals(juegoSeleccionado)) {
                //Nuevo (modifique lo de adentro del else if por equals para mas mejor)
                juego = new SpaceInvaders("SpaceInvaders",800,600);
                t = new Thread() {
                    public void run() {
                        juego.run(1.0 / 60.0);
                    }
                };
                t.start();
            } else if ("Counter-Strike".equals(juegoSeleccionado))
            {
                JOptionPane.showMessageDialog(LanzadorJuego.this, "No somos tan buenos, todavia no sabemos hacer este");
            }
            //Nuevo
        }
        if (e.getSource() == botonConfig) {
            // Agarra el valor del juego seleccionado en la barra
            String juegoSeleccionado = juegos.getSelectedValue();
            if (juegoSeleccionado == null) {
                JOptionPane.showMessageDialog(LanzadorJuego.this, "Error, seleccione un juego para configurar");
            } else if (juegoSeleccionado.equals("SpaceInvaders") ) {
                //new MenuConfigLem();
            } else if (juegoSeleccionado.equals("Pong") ) {
                new MenuConfig();
            } else if (juegoSeleccionado.equals("Counter-Strike")) {
                JOptionPane.showMessageDialog(LanzadorJuego.this, "Configuración en proceso... (Jugate otro juego)");
            }
        }
    }

    public static void main(String[] args) {
        new LanzadorJuego();
    }

}