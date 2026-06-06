
package jgame.gradle;

import java.awt.*;

import javax.swing.*;


public class Yapa extends JFrame {

   public Yapa(){
         super("Yapa");
  

  
      JPanel panel = new JPanel();
      GridBagLayout layout = new GridBagLayout();

      panel.setLayout(layout);        
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets=new Insets(2,2,2,2); //separacion entre los componentes


      gbc.ipady = 40;
      gbc.gridx = 0;
      gbc.gridy = 0;      
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridheight = 2;
      gbc.gridwidth = 6;
      panel.add(new JButton(" P O N G "),gbc);  

      gbc.gridx = 6;
      gbc.gridy = 0;      
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridwidth = 6;
      panel.add(new JButton(" Space Invaders "),gbc); 


      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.ipady = 20;  
 
      gbc.gridwidth = 4;
      gbc.gridx = 0;
      gbc.gridy = 3;
      panel.add(new JButton("PUBG"),gbc); 

      gbc.gridx = 4;
      gbc.gridy = 3;       
      panel.add(new JButton("APEX"),gbc);  

      gbc.gridx = 8;
       gbc.gridy = 3;       
       panel.add(new JButton("Destiny 2"),gbc);  


      gbc.ipady = 10;
      gbc.ipadx = 0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
        
      gbc.gridwidth = 3;
      gbc.gridx = 0;
      gbc.gridy = 6;
      panel.add(new JButton("Brawlhalla"),gbc);

      gbc.gridx = 3;
      gbc.gridy = 6;
      panel.add(new JButton("Sims4"),gbc); 

      gbc.gridx = 6;
      gbc.gridy = 6;
      panel.add(new JButton("eFootBall"),gbc); 

      gbc.gridx = 9;
      gbc.gridy = 6;
      panel.add(new JButton("Fallout Shelter"),gbc); 

      gbc.fill = GridBagConstraints.NONE;
      gbc.gridwidth = 1;
            gbc.gridheight = 1;
 
      gbc.gridy = 8;


      Dimension s32x32 = new Dimension(32, 32);
 
 
      for(int i=0;i<12;i++){
        gbc.gridx = i;
         panel.add(new Box.Filler(s32x32, s32x32, s32x32),gbc);
      }
     


      this.getContentPane().add(panel, BorderLayout.CENTER);
   


         this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  


         this.setSize(800,600);      
         this.setLocationRelativeTo(null);  
         this.setVisible(true);
   }

   public static void main(String[] args) {
      Yapa y=new Yapa();

   }

   

  
}