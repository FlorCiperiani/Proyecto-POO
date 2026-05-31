package clasesCompartidas;

//import clasesCompartidas.JPanelImage;
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


        // Botones
        botonConfig = new JButton("Configuración");
        botonConfig.addActionListener(this);
        botonIniciar = new JButton("Iniciar juego");
        botonIniciar.addActionListener(this);

        juegos = new JList<>(listaJuegos);
        juegos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        juegos.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        juegos.setVisibleRowCount(1);
        juegos.setFixedCellWidth(140);
        juegos.setFixedCellHeight(140);
        juegos.setPreferredSize(new Dimension(listaJuegos.size() * 150, 140));
        juegos.setCellRenderer(new IconListRenderer());
        juegos.setOpaque(false);

        scrollPane = new JScrollPane(juegos);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(listaJuegos.size() * 150, 150));

        JPanel cardsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        cardsWrapper.setBackground(Color.white);
        cardsWrapper.add(scrollPane);

        JLabel pregunta = new JLabel("¿Qué quieres jugar?", SwingConstants.CENTER);
        pregunta.setFont(new Font("SansSerif", Font.PLAIN, 18));
        pregunta.setForeground(Color.black);

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(Color.white);
        norte.add(pregunta, BorderLayout.NORTH);
        norte.add(cardsWrapper, BorderLayout.CENTER);

        add(norte, BorderLayout.NORTH);

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
                        // Cambiado de src\\main\\... a la ruta relativa del recurso empaquetado
                        panelImg.add(new JPanelImage("/assetsLanzador/portadaSpaceInvaders.png"));
                    } else if ("Pong".equals(selected)) {
                        panelImg.add(new JPanelImage("/assetsLanzador/portadaPong.png"));
                    } else if ("Lode Runner".equals(selected)) { // CORREGIDO: Ahora coincide con la lista
                        panelImg.add(new JPanelImage("/assetsLanzador/portadaLR.png")); 
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

    // Renderizador simple: icono arriba, texto abajo (estilo perfiles)
    private class IconListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(true);
            panel.setBackground(isSelected ? Color.DARK_GRAY : list.getBackground());

            String nombre = value == null ? "" : value.toString();
            ImageIcon ico = getIconForGame(nombre);
            JLabel iconLabel = new JLabel();
            if (ico != null) iconLabel.setIcon(ico);
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel text = new JLabel(nombre, SwingConstants.CENTER);
            text.setForeground(isSelected ? Color.YELLOW : Color.WHITE);

            panel.add(iconLabel, BorderLayout.CENTER);
            panel.add(text, BorderLayout.SOUTH);
            panel.setBorder(BorderFactory.createEmptyBorder(6,8,6,8));
            return panel;
        }
    }

    // Carga y escala ícono desde recursos empaquetados
    private ImageIcon getIconForGame(String nombre) {
        String ruta = null;
        if ("Pong".equals(nombre)) ruta = "/assetsLanzador/gato.png";
        else if ("SpaceInvaders".equals(nombre)) ruta = "/assetsLanzador/hombre-lobo.png";
        else if ("Lode Runner".equals(nombre)) ruta = "/assetsLanzador/oso.png";
        

        try {
            java.net.URL url = getClass().getResource(ruta);
            if (url == null) return null;
            Image img = new ImageIcon(url).getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception ex) {
            return null;
        }
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

            } else if ("Lode Runner".equals(juegoSeleccionado)) {
            

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