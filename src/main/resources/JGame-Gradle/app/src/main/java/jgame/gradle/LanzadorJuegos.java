package jgame.gradle;



import java.awt.*;
import java.awt.event.*;



import javax.swing.*;
import javax.swing.event.*;

import com.entropyinteractive.*; //las librerias JGame,GameLoop,KeyBoard,Mouse,etc...


public class LanzadorJuegos extends JPanel implements ActionListener{


	JGame juego;
	Thread t;
    public LanzadorJuegos(){
    	int filas=0;
    	int columnas=1;
    	int separacion=10;

    	this.setLayout(new GridLayout(filas,columnas,separacion,separacion));

    	String[] arrEtiquetas={"Juego02","AppCamaraHeroe","JuegoConDB"};
    	JButton boton;

    	for(String etiqueta:arrEtiquetas){

    		boton=new JButton(etiqueta);

    		boton.addActionListener(this);
    		this.add(boton);
    	}



    }

	public void actionPerformed(ActionEvent e){

				if (e.getActionCommand().equals("Juego02")){
					 	juego = new Juego02();

						t = new Thread() {
						    public void run() {
						        juego.run(1.0 / 60.0);
						    }
						};

						t.start();
				}

				if (e.getActionCommand().equals("AppCamaraHeroe")){
						juego = new AppCamaraHeroe();

						t = new Thread() {
						    public void run() {
						        juego.run(1.0 / 60.0);
						    }
						};

						t.start();
				}
				if (e.getActionCommand().equals("JuegoConDB")){
						juego = new JuegoConDB();

						t = new Thread() {
						    public void run() {
						        juego.run(1.0 / 60.0);
						    }
						};

						t.start();
				}
	}
    public static void main(String...z){
		JFrame f=new JFrame("Lanzador");

		f.add(new LanzadorJuegos());
		WindowListener l=new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
                        System.exit(0);
                };
        };

        f.addWindowListener(l);
        f.pack();
        f.setVisible(true);
	 	f.setLocationRelativeTo(null);
	}


}