package clasesCompartidas;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class JPanelImage extends JPanel {
    private final String path;
    private Image img;

    public JPanelImage(String path)
    {
        this.path=path;
        loadImage();
    }

    private void loadImage() {
        try {
            img = new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen: " + path);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

