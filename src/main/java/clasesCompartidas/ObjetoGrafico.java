package clasesCompartidas;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;


public abstract class ObjetoGrafico implements ObjetoMovible{
    protected BufferedImage imagen = null;

    protected double posicionX = 0;
    protected  double posicionY = 0;

    public ObjetoGrafico(){}
   public ObjetoGrafico(String filename) {
    try {
        InputStream is = getClass().getResourceAsStream(filename);
        if (is == null) {
            System.out.println("NO SE ENCONTRÓ: " + filename);
        } else {
            imagen = ImageIO.read(is);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public int getAncho() {
    return imagen != null ? imagen.getWidth() : 0;
    }
    public int getAlto() {
    return imagen != null ? imagen.getHeight() : 0;
    }

    public void setPosicion(double x,double y){
        this.posicionX = x;
        this.posicionY = y;
    }

    public void mostrar(Graphics2D g2) {
        g2.drawImage(imagen,(int) this.posicionX,(int) this.posicionY,null);
    }

    public double getX(){
        return posicionX;
    }

    public double getY(){
        return posicionY;
    }
    public void setImagen(BufferedImage imagen) {
    this.imagen = imagen;
    }

    public BufferedImage getImagen() {
        return this.imagen;
    }

    public void setY(double y) {this.posicionY = y;}
    public void setX(double x){this.posicionX = x;}
}
